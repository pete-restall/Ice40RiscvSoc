import pytest
import random
from myhdl import *
from src.core.device_bus import DeviceBus
from src.core.femtorv32_bus import Femtorv32Bus
from tests.cosimulatable_dut import CosimulatableDut

class DeviceBusFixture:
	def __init__(self):
		seed = random.getrandbits(32)
		print(f"Using non-determinism seed: {seed}")
		random.seed(seed)

		self._processor_bus = Femtorv32Bus()
		self._num_addressable_words = self.any_num_addressable_words()
		self._base_address = self.any_base_address_allowing(self._num_addressable_words * 4)
		self._device_bus = None
		self._dut = None

	def any_num_addressable_words(self):
		return random.randint(1, 2**len(self._processor_bus.address) // 4)

	def any_base_address_allowing(self, num_addressable_words, num_address_bits=-1):
		address_space = 2**num_address_bits if num_address_bits > 1 else self._power_of_2_exponent_containing(num_addressable_words * 4)
		if address_space == len(self._processor_bus.address):
			return 0

		max_base_address = (2**len(self._processor_bus.address) - 1) & ~(2**address_space - 1)
		return random.randint(0, max_base_address) & ~(2**address_space - 1)

	def _power_of_2_exponent_containing(self, num):
		for i in range(0, 32 + 1):
			if 2**i >= num:
				return i

		raise ValueError(f"Number outside reasonable range for power-of-2 determination; num={num}")

	def any_word(self):
		return random.randint(0, 2**32 - 1)

	@property
	def processor_bus(self):
		return self._processor_bus

	@property
	def base_address(self):
		return self._base_address

	@base_address.setter
	def base_address(self, value):
		self._base_address = value

	@property
	def num_addressable_words(self):
		return self._num_addressable_words

	@num_addressable_words.setter
	def num_addressable_words(self, value):
		self._num_addressable_words = value

	@property
	def device_bus(self):
		if self._device_bus is None:
			self._device_bus = DeviceBus(
				self._processor_bus,
				self._base_address,
				self._num_addressable_words)

		return self._device_bus

	def generators(self):
		if self._dut is None:
			self._dut = CosimulatableDut(self.device_bus.generators()).dut

		return self._dut

	def any_address(self):
		return random.randint(self._processor_bus.address.min, self._processor_bus.address.max)

class TestDeviceBus:
	@pytest.fixture(scope="function")
	def fixture(self):
		return DeviceBusFixture()

	def run(self, fixture, *generators):
		sim = Simulation(fixture.generators(), *generators)
		sim.run()

	def test_constructor_with_no_bus_raises_type_error(self, fixture):
		num_addressable_words = fixture.any_num_addressable_words()
		with pytest.raises(TypeError, match=r"(?i)bus must be specified"):
			DeviceBus(None, fixture.any_base_address_allowing(num_addressable_words), num_addressable_words)

	def test_constructor_with_negative_base_address_raises_value_error(self, fixture):
		for invalid_base_address in range(-8, 0):
			with pytest.raises(ValueError, match=r"(?i)base address must be in the range"):
				DeviceBus(fixture.processor_bus, invalid_base_address, num_addressable_words=16)

	@pytest.mark.parametrize("invalid_base_address,num_addressable_words,num_address_bits", [
		(4, 2**24 // 4, 0),
		(4, 1, 24),
		(2**24 - 8, 1, 4)
	])
	def test_constructor_with_base_address_too_high_for_num_address_bits_raises_value_error(
		self,
		fixture,
		invalid_base_address,
		num_addressable_words,
		num_address_bits):

		with pytest.raises(ValueError, match=r"(?i)base address must be in the range"):
			DeviceBus(fixture.processor_bus, invalid_base_address, num_addressable_words, num_address_bits)

	@pytest.mark.parametrize("invalid_base_address,num_addressable_words,num_address_bits", [
		(1, 1, -1),
		(1, 1, 2),
		(1, 1, 3),
		(4, 1, 3),
		(16, 2, 5),
		(16, 8, -1),
		(16, 8, 5),
		(2**18 - 2048, 1024, -1),
		(2**18 - 4096, 1024, 18),
		(2**18 + 2048, 1024, -1),
		(2**18 + 4096, 1024, 18)
	])
	def test_constructor_with_base_address_not_aligned_to_num_address_bits_raises_value_error(
		self,
		fixture,
		invalid_base_address,
		num_addressable_words,
		num_address_bits):

		with pytest.raises(ValueError, match=r"(?i)address must be aligned"):
			DeviceBus(fixture.processor_bus, invalid_base_address, num_addressable_words, num_address_bits)

	def test_constructor_with_negative_num_addressable_words_raises_value_error(self, fixture):
		for invalid_num_addressable_words in range(-3, 0):
			with pytest.raises(ValueError, match=r"(?i)addressable words must be at least 1"):
				DeviceBus(fixture.processor_bus, fixture.any_base_address_allowing(100), invalid_num_addressable_words)

	def test_constructor_with_zero_num_addressable_words_raises_value_error(self, fixture):
		with pytest.raises(ValueError, match=r"(?i)addressable words must be at least 1"):
			DeviceBus(fixture.processor_bus, fixture.any_base_address_allowing(100), num_addressable_words=0)

	@pytest.mark.parametrize("num_addressable_words,num_address_bits", [
		(2, 2),
		(3, 3),
		(4, 3),
		(5, 4),
		(2**16 // 4, 15),
		(2**24 // 4, 23)
	])
	def test_constructor_with_num_address_bits_specified_but_not_able_to_hold_addressable_words_raises_value_error(
		self,
		fixture,
		num_addressable_words,
		num_address_bits):

		with pytest.raises(ValueError, match=r"(?i)address space is not enough"):
			DeviceBus(fixture.processor_bus, fixture.any_base_address_allowing(num_addressable_words), num_addressable_words, num_address_bits)

	def test_constructor_with_num_address_bits_greater_than_bus_address_width_raises_value_error(self, fixture):
		with pytest.raises(ValueError, match=r"(?i)address space exceeds address bus width"):
			DeviceBus(
				fixture.processor_bus,
				fixture.any_base_address_allowing(100),
				num_addressable_words=1,
				num_address_bits=len(fixture.processor_bus.address) + 1)

	@pytest.mark.parametrize("num_addressable_words,num_address_bits,expected_address_width", [
		(1, -1, 2),
		(1, 0, 2),
		(1, 1, 2),
		(1, 2, 2),
		(2, -1, 3),
		(3, 10, 10),
		(2**16 // 4, -1, 16),
		(2**24 // 4, -1, 24),
	])
	def test_address_is_same_width_as_num_address_bits(self, fixture, num_addressable_words, num_address_bits, expected_address_width):
		bus = DeviceBus(
			fixture.processor_bus,
			fixture.any_base_address_allowing(num_addressable_words, num_address_bits),
			num_addressable_words,
			num_address_bits)

		assert len(bus.address) == expected_address_width

	def test_address_is_modbv_signal(self, fixture):
		expected = Signal(modbv())
		assert isinstance(fixture.device_bus.address, expected.__class__)
		assert isinstance(fixture.device_bus.address.val, expected.val.__class__)

	def test_address_is_shadow_of_processor_address_lsbs(self, fixture):
		@instance
		def test():
			nonlocal fixture
			fixture.processor_bus.address.next = fixture.any_address()
			yield delay(1)
			assert fixture.device_bus.address.val == fixture.processor_bus.address(len(fixture.device_bus.address), 0).val

		self.run(fixture, test)

	@pytest.mark.parametrize("num_addressable_words,num_address_bits,expected_num_address_bits", [
		(1, -1, 2),
		(1, 0, 2),
		(2, 1, 3),
		(3, 1, 4),
		(2**16 // 4, 1, 16),
		(2**24 // 4, 1, 24),
	])
	def test_reserved_bits_is_calculated_when_num_address_bits_was_specified_less_than_two(
		self,
		fixture,
		num_addressable_words,
		num_address_bits,
		expected_num_address_bits):

		bus = DeviceBus(
			fixture.processor_bus,
			fixture.any_base_address_allowing(num_addressable_words, num_address_bits),
			num_addressable_words,
			num_address_bits)

		assert len(bus.address) == expected_num_address_bits

	def test_wdata_is_shadow_of_processor_wdata(self, fixture):
		@instance
		def test():
			nonlocal fixture
			wdata = fixture.any_word()
			fixture.processor_bus.wdata.next = wdata
			yield delay(1)
			assert fixture.device_bus.wdata.val == wdata

		self.run(fixture, test)

	def test_wmask_remains_low_when_address_outside_of_upper_range(self, fixture):
		fixture.base_address = 0x432100
		fixture.num_addressable_words = 10

		@instance
		def test():
			nonlocal fixture
			for too_high in range(fixture.num_addressable_words * 4, fixture.num_addressable_words * 4 + 10):
				fixture.processor_bus.address.next = fixture.base_address + too_high
				fixture.processor_bus.wmask.next = 1
				yield delay(1)
				assert not fixture.device_bus.wmask

		self.run(fixture, test)

	def test_wmask_remains_low_when_address_outside_of_lower_range(self, fixture):
		fixture.base_address = 0x432100
		fixture.num_addressable_words = 10

		@instance
		def test():
			nonlocal fixture
			for too_low in range(-10, 0):
				fixture.processor_bus.address.next = fixture.base_address + too_low
				fixture.processor_bus.wmask.next = 0x0f
				yield delay(1)
				assert not fixture.device_bus.wmask

		self.run(fixture, test)

	def test_wmask_goes_high_for_each_address_inside_range(self, fixture):
		fixture.base_address = 0x432100
		fixture.num_addressable_words = 10

		@instance
		def test():
			nonlocal fixture
			for offset in range(0, fixture.num_addressable_words * 4):
				wmask = fixture.any_word() & 0x0f
				fixture.processor_bus.address.next = fixture.base_address + offset
				fixture.processor_bus.wmask.next = wmask
				yield delay(1)
				assert fixture.device_bus.wmask == wmask

		self.run(fixture, test)

	@pytest.mark.parametrize("wbusy", [True, False])
	def test_wbusy_is_shadow_of_processor_wbusy(self, fixture, wbusy):
		@instance
		def test():
			nonlocal fixture
			fixture.device_bus.wbusy.next = wbusy
			yield delay(1)
			assert fixture.processor_bus.wbusy.val == wbusy

		self.run(fixture, test)

	def test_rdata_is_shadow_of_processor_rdata(self, fixture):
		@instance
		def test():
			nonlocal fixture
			rdata = fixture.any_word()
			fixture.device_bus.rdata.next = rdata
			yield delay(1)
			assert fixture.processor_bus.rdata.val == rdata

		self.run(fixture, test)

	def test_rstrb_remains_low_when_address_outside_of_upper_range(self, fixture):
		fixture.base_address = 0x123400
		fixture.num_addressable_words = 10

		@instance
		def test():
			nonlocal fixture
			for too_high in range(fixture.num_addressable_words * 4, fixture.num_addressable_words * 4 + 10):
				fixture.processor_bus.address.next = fixture.base_address + too_high
				fixture.processor_bus.rstrb.next = 1
				yield delay(1)
				assert not fixture.device_bus.rstrb

		self.run(fixture, test)

	def test_rstrb_remains_low_when_address_outside_of_lower_range(self, fixture):
		base_address = 0x123400
		num_addressable_words = 10
		bus = DeviceBus(fixture.processor_bus, base_address, num_addressable_words)

		@instance
		def test():
			nonlocal fixture
			for too_low in range(-10, 0):
				fixture.processor_bus.address.next = base_address + too_low
				fixture.processor_bus.rstrb.next = 1
				yield delay(1)
				assert not bus.rstrb

		self.run(fixture, test, bus.generators())

	def test_rstrb_goes_high_for_each_address_inside_range(self, fixture):
		base_address = 0x123400
		num_addressable_words = 10
		bus = DeviceBus(fixture.processor_bus, base_address, num_addressable_words)

		@instance
		def test():
			nonlocal fixture
			for offset in range(0, num_addressable_words * 4):
				fixture.processor_bus.address.next = base_address + offset
				fixture.processor_bus.rstrb.next = 1
				yield delay(1)
				assert bus.rstrb

		self.run(fixture, test, bus.generators())

	@pytest.mark.parametrize("rbusy", [True, False])
	def test_rbusy_is_shadow_of_processor_rbusy(self, fixture, rbusy):
		@instance
		def test():
			nonlocal fixture
			fixture.device_bus.rbusy.next = rbusy
			yield delay(1)
			assert fixture.processor_bus.rbusy.val == rbusy

		self.run(fixture, test)
