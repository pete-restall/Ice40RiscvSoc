import pytest
import random
from myhdl import *
from types import SimpleNamespace
from src.core.femtorv32_processor import Femtorv32Processor
from src.core.femtorv32_bus import Femtorv32Bus
from tests.cosimulatable_dut import *

_MAXIMUM_IO_WRITE_ADDRESS_BITS = 12
_MAXIMUM_IO_WRITE_ADDRESS = (1 << _MAXIMUM_IO_WRITE_ADDRESS_BITS) - 1
_MAXIMUM_IO_WRITE_VALUE_BITS = 6
_MAXIMUM_IO_WRITE_VALUE = (1 << _MAXIMUM_IO_WRITE_VALUE_BITS) - 1

class Femtorv32Fixture:
	def __init__(self):
		seed = random.getrandbits(32)
		print(f"Using non-determinism seed: {seed}")
		random.seed(seed)

		self.io_writes = []

		self.reset_address = 0x123400
		self._reset = ResetSignal(bool(0), active=bool(0), isasync=True)
		self.clk = Signal(bool(0))
		self.bus = Femtorv32Bus()
		self.irq = Signal(bool(0))
		self.halted = Signal(False)

		self.dut = CosimulatableDut(
			Femtorv32Processor(
				self._reset,
				self.clk,
				self.bus,
				self.irq,
				reset_address=self.reset_address)).dut

	def reset(self):
		@instance
		def generator():
			nonlocal self
			self._reset.next = self._reset.active
			yield self.clock()
			self._reset.next = not self._reset.active
			yield self.clock()

		return generator

	def clock(self):
		@instance
		def generator():
			nonlocal self
			self.clk.next = 1
			yield delay(cycles(1))
			self.clk.next = 0
			yield delay(cycles(1))

		return generator

	def program_writing_to(self, io_address, value):
		if io_address < 0 or io_address > _MAXIMUM_IO_WRITE_ADDRESS:
			raise ValueError(f"IO Address must be in the range [0x0000, {hex(_MAXIMUM_IO_WRITE_ADDRESS)}] to fit in the instruction encoding")

		if value < 0 or value > _MAXIMUM_IO_WRITE_VALUE:
			raise ValueError(f"Value must be in the range [0x00, {hex(_MAXIMUM_IO_WRITE_VALUE)}] to fit in the instruction encoding")

		@always(self.clk.posedge)
		def instructions():
			nonlocal self
			nonlocal io_address
			nonlocal value
			if self._reset == self._reset.active or not self.bus.rstrb:
				return

			invalid = 0
			c_jal_2 = int("0b001_00000000100_01", base=2) # c.jal 2
			c_li_x1_value = int("0b010_0_00001_00000_01", base=2) | ((1 << 12) if (value & 0x20) else 0) | ((value & 0x1f) << 2) # c.li x1, value
			sw_x1_ioaddress_x0 = int("0b0000000_00001_00000_010_00000_0100011", base=2) | ((io_address & 0x0fe0) << 20) | ((io_address & 0x1f) << 7) # sw x1, io_address(x0)

			c_nop = 1
			program = [
				c_jal_2 | invalid,
				c_li_x1_value | ((sw_x1_ioaddress_x0 & 0x0000ffff) << 16),
				((sw_x1_ioaddress_x0 >> 16) & 0x0000ffff) | (c_nop << 16),
				c_nop | (c_nop << 16)
			]

			if self.bus.address == self.reset_address:
				self.bus.rdata.next = program[0]
			elif self.bus.address == self.reset_address + 4:
				self.bus.rdata.next = program[1]
			elif self.bus.address == self.reset_address + 8:
				self.bus.rdata.next = program[2]
			elif self.bus.address == self.reset_address + 12:
				self.bus.rdata.next = program[3]
			else:
				self.halted.next = True

			self.bus.rbusy.next = bool(0)

		return instructions

	def io_write_spy(self):
		@always(self.clk.posedge)
		def spy():
			nonlocal self
			if self._reset == self._reset.active or not self.bus.wmask:
				return

			mask = 0xff000000 if (self.bus.wmask & 8) else 0x00000000
			mask |= 0x00ff0000 if (self.bus.wmask & 4) else 0x00000000
			mask |= 0x0000ff00 if (self.bus.wmask & 2) else 0x00000000
			mask |= 0x000000ff if (self.bus.wmask & 1) else 0x00000000

			self.io_writes.append(SimpleNamespace(address=int(self.bus.address.val), value=int(self.bus.wdata.val) & mask))

		return spy

	def any_word_aligned_io_address(self):
		return random.randint(0, _MAXIMUM_IO_WRITE_ADDRESS) & 0xfffffffc

	def any_io_value(self):
		return random.randint(0, _MAXIMUM_IO_WRITE_VALUE)

	def generators(self):
		return self.dut

class TestFemtorv32:
	@pytest.fixture(scope="function")
	def fixture(self):
		return Femtorv32Fixture()

	def run(self, fixture, *generators):
		sim = Simulation(fixture.generators(), *generators)
		sim.run()

	def test_cpu_can_execute_instructions_and_write_values_to_the_bus(self, fixture):
		io_address = fixture.any_word_aligned_io_address()
		io_value = fixture.any_io_value()

		@instance
		def test():
			nonlocal fixture
			yield fixture.reset()
			for _ in range(0, 100):
				yield fixture.clock()
				if fixture.halted:
					break

			nonlocal io_value
			nonlocal io_address
			assert fixture.halted
			assert len(fixture.io_writes) == 1
			assert fixture.io_writes[0].address == sign_extended_to_address_bus_width(io_address)
			assert fixture.io_writes[0].value == sign_extended_to_data_bus_width(io_value)

		def sign_extended_to_address_bus_width(value):
			return sign_extended(value, _MAXIMUM_IO_WRITE_ADDRESS_BITS, len(fixture.bus.address))

		def sign_extended(value, narrow_bits, wide_bits):
			sign_bit = 1 << (narrow_bits - 1)
			if not (value & sign_bit):
				return value

			return ((2**(wide_bits - narrow_bits) - 1) << narrow_bits) | value

		def sign_extended_to_data_bus_width(value):
			return sign_extended(value, _MAXIMUM_IO_WRITE_VALUE_BITS, 32)

		self.run(
			fixture,
			test,
			fixture.program_writing_to(io_address, io_value),
			fixture.io_write_spy())
