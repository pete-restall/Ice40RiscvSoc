import pytest
import random
from abc import ABC, abstractmethod
from myhdl import *
from src.primitives.register import Register
from tests.cosimulatable_dut import CosimulatableDut

class RegisterFixture:
	def __init__(self, is_write_enable_active_high, negedge):
		seed = random.getrandbits(32)
		print(f"Using non-determinism seed: {seed}")
		random.seed(seed)

		self._clk = Signal(negedge)
		self.reset = ResetSignal(val=bool(0), active=bool(0), isasync=False)
		self.input = Signal(intbv(val=0, min=0, max=2))
		self.write_enable = Signal(not is_write_enable_active_high)
		self.reset_value = 0
		self.is_write_enable_active_high = is_write_enable_active_high
		self.negedge = negedge
		self._register = None
		self._dut = None

	@property
	def clk(self):
		return self._clk

	@property
	def register(self):
		if self._register is None:
			self._register = Register(
				self._clk,
				self.reset,
				self.input,
				self.write_enable,
				self.reset_value,
				self.is_write_enable_active_high,
				self.negedge)

		return self._register

	def ensure_initial_state(self):
		if self.reset.isasync:
			yield self._reset_async()
		else:
			yield self._reset_sync()

	def _reset_async(self):
		self.reset.next = self.reset.active
		yield delay(1)
		self.reset.next = not self.reset.active
		yield delay(1)

	def _reset_sync(self):
		self.reset.next = self.reset.active
		yield self.clock_pulse()
		self.reset.next = not self.reset.active

	def clock_pulse(self):
		yield self.clock_active()
		yield self.clock_inactive()

	def clock_active(self):
		if self.negedge:
			yield self.clock_fall()
		else:
			yield self.clock_rise()

	def clock_rise(self):
		self._clk.next = bool(1)
		yield delay(1)

	def clock_fall(self):
		self._clk.next = bool(0)
		yield delay(1)

	def clock_inactive(self):
		if self.negedge:
			yield self.clock_rise()
		else:
			yield self.clock_fall()

	def any_reset_value(self):
		return self.any_input_value()

	def any_input_value(self):
		return self.any_int_in_range(self.input.min, self.input.max)

	def any_int_in_range(self, min, half_open_max):
		return random.randint(min, half_open_max - 1)

	def any_input_value_except(self, excluded):
		val = self.any_int_in_range(self.input.min, self.input.max)
		return val if val != excluded else self.any_input_value_except(excluded)

	def disable_writes(self):
		self.enable_writes(False)

	def enable_writes(self, enabled=True):
		self.write_enable.next = self.is_write_enable_active_high if enabled else not self.is_write_enable_active_high

	def generators(self):
		if self._dut is None:
			self._dut = CosimulatableDut(self.register.generators()).dut

		return self._dut

class RegisterTestSuite(ABC):
	@pytest.fixture(scope="function")
	@abstractmethod
	def fixture(self):
		...

	def run(self, fixture, *generators):
		sim = Simulation(fixture.generators(), *generators)
		sim.run()

	def test_constructor_with_no_clk_raises_type_error(self, fixture):
		with pytest.raises(TypeError):
			Register(None, fixture.reset, fixture.input, fixture.reset_value, fixture.write_enable, fixture.is_write_enable_active_high, fixture.negedge)

	def test_constructor_with_no_reset_raises_type_error(self, fixture):
		with pytest.raises(TypeError):
			Register(fixture.clk, None, fixture.input, fixture.reset_value, fixture.write_enable, fixture.is_write_enable_active_high, fixture.negedge)

	def test_constructor_with_no_input_raises_type_error(self, fixture):
		with pytest.raises(TypeError):
			Register(fixture.clk, fixture.reset, None, fixture.reset_value, fixture.write_enable, fixture.is_write_enable_active_high, fixture.negedge)

	def test_constructor_with_no_reset_value_raises_type_error(self, fixture):
		with pytest.raises(TypeError):
			Register(fixture.clk, fixture.reset, fixture.input, None, fixture.write_enable, fixture.is_write_enable_active_high, fixture.negedge)

	def test_constructor_with_no_write_enable_raises_type_error(self, fixture):
		with pytest.raises(TypeError):
			Register(fixture.clk, fixture.reset, fixture.input, fixture.reset_value, None, fixture.is_write_enable_active_high, fixture.negedge)

	def test_output_is_intbv_signal_when_input_is_intbv(self, fixture):
		expected = Signal(intbv(val=123))
		fixture.input = Signal(intbv(val=456))
		assert isinstance(fixture.register.output, expected.__class__)
		assert isinstance(fixture.register.output.val, expected.val.__class__)

	@pytest.mark.parametrize("input_width", [1, 2, 3, 64])
	def test_output_is_intbv_signal_of_same_width_when_input_is_intbv(self, fixture, input_width):
		expected = Signal(intbv(val=0, min=0, max=2**input_width))
		fixture.input = Signal(intbv(val=0, min=0, max=2**input_width))
		assert fixture.register.output.min == expected.min and fixture.register.output.max == expected.max

	def test_output_value_is_reset_value_when_input_is_intbv(self, fixture):
		fixture.input = Signal(intbv(val=0, min=0, max=32))
		fixture.reset_value = fixture.any_int_in_range(0, 32)
		assert fixture.register.output.val == fixture.reset_value

	def test_output_is_modbv_signal_when_input_is_modbv(self, fixture):
		expected = Signal(modbv(val=0, min=0, max=123))
		fixture.input = Signal(modbv(val=0, min=0, max=456))
		assert isinstance(fixture.register.output, expected.__class__)
		assert isinstance(fixture.register.output.val, expected.val.__class__)

	@pytest.mark.parametrize("input_width", [1, 2, 3, 64])
	def test_output_is_modbv_signal_of_same_width_when_input_is_modbv(self, fixture, input_width):
		expected = Signal(modbv(val=0, min=0, max=2**input_width))
		fixture.input = Signal(modbv(val=0, min=0, max=2**input_width))
		assert fixture.register.output.min == expected.min and fixture.register.output.max == expected.max

	def test_output_value_is_reset_value_when_input_is_modbv(self, fixture):
		fixture.input = Signal(modbv(val=0, min=0, max=32))
		fixture.reset_value = fixture.any_reset_value()
		assert fixture.register.output.val == fixture.reset_value

	def test_output_is_not_same_instance_as_input(self, fixture):
		assert fixture.register.output is not fixture.input

	@pytest.mark.parametrize("reset_active,is_reset_async", [
		[bool(0), False],
		[bool(0), True],
		[bool(1), False],
		[bool(1), True]])
	def test_output_is_reset_value_on_initial_state_when_write_enable_is_active(self, fixture, reset_active, is_reset_async):
		fixture.input = Signal(intbv(val=0, min=0, max=65536))
		fixture.reset = ResetSignal(val=not reset_active, active=reset_active, isasync=is_reset_async)
		fixture.reset_value = fixture.any_reset_value()

		@instance
		def test():
			nonlocal fixture
			fixture.enable_writes()
			yield fixture.ensure_initial_state()
			assert fixture.register.output.val == fixture.reset_value

		self.run(fixture, test)

	@pytest.mark.parametrize("reset_active,is_reset_async", [
		[bool(0), False],
		[bool(0), True],
		[bool(1), False],
		[bool(1), True]])
	def test_output_is_reset_value_on_initial_state_when_write_enable_is_inactive(self, fixture, reset_active, is_reset_async):
		fixture.input = Signal(intbv(val=0, min=0, max=65536))
		fixture.reset = ResetSignal(val=not reset_active, active=reset_active, isasync=is_reset_async)
		fixture.reset_value = fixture.any_reset_value()

		@instance
		def test():
			nonlocal fixture
			fixture.disable_writes()
			yield fixture.ensure_initial_state()
			assert fixture.register.output.val == fixture.reset_value

		self.run(fixture, test)

	def test_output_is_latched_on_active_clock_edge_when_write_enable_is_active(self, fixture):
		fixture.input = Signal(intbv(val=0, min=0, max=2**32))
		fixture.reset_value = fixture.any_reset_value()

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			fixture.enable_writes()
			fixture.input.next = fixture.any_input_value_except(fixture.reset_value)
			yield fixture.clock_active()
			assert fixture.register.output.val == fixture.input.val

		self.run(fixture, test)

	def test_output_is_not_latched_before_active_clock_edge_when_write_enable_is_active(self, fixture):
		fixture.input = Signal(intbv(val=0, min=0, max=2**32))
		fixture.reset_value = fixture.any_reset_value()

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			fixture.enable_writes()
			fixture.input.next = fixture.any_input_value_except(fixture.reset_value)
			yield delay(1)
			assert fixture.register.output.val == fixture.reset_value

		self.run(fixture, test)

	def test_output_is_latched_after_inactive_clock_edge_when_write_enable_is_active(self, fixture):
		fixture.input = Signal(intbv(val=0, min=0, max=2**32))
		fixture.reset_value = fixture.any_reset_value()

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			fixture.enable_writes()
			latched_value = fixture.any_input_value_except(fixture.reset_value)
			fixture.input.next = latched_value
			yield fixture.clock_active()
			fixture.input.next = fixture.any_input_value_except(latched_value)
			yield fixture.clock_inactive()
			assert fixture.register.output.val == latched_value

		self.run(fixture, test)

	def test_output_is_not_latched_on_active_clock_edge_when_write_enable_is_inactive(self, fixture):
		fixture.input = Signal(intbv(val=0, min=0, max=2**32))
		fixture.reset_value = fixture.any_reset_value()

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			fixture.disable_writes()
			fixture.input.next = fixture.any_input_value_except(fixture.reset_value)
			yield fixture.clock_active()
			assert fixture.register.output.val == fixture.reset_value

		self.run(fixture, test)

	@pytest.mark.parametrize("reset_active,write_enable", [
		[bool(0), bool(0)],
		[bool(0), bool(1)],
		[bool(1), bool(0)],
		[bool(1), bool(1)]])
	def test_output_is_reset_value_on_active_clock_edge_when_synchronous_reset_is_active(self, fixture, reset_active, write_enable):
		fixture.input = Signal(intbv(val=0, min=0, max=2**32))
		fixture.reset = ResetSignal(val=not reset_active, active=reset_active, isasync=False)
		fixture.reset_value = fixture.any_reset_value()

		@instance
		def test():
			nonlocal fixture, reset_active, write_enable
			yield fixture.ensure_initial_state()
			fixture.enable_writes(write_enable)
			fixture.input.next = fixture.any_input_value_except(fixture.reset_value)
			yield fixture.clock_pulse()

			fixture.reset.next = reset_active
			yield fixture.clock_active()
			assert fixture.register.output.val == fixture.reset_value

		self.run(fixture, test)

	@pytest.mark.parametrize("reset_active,write_enable", [
		[bool(0), bool(0)],
		[bool(0), bool(1)],
		[bool(1), bool(0)],
		[bool(1), bool(1)]])
	def test_output_is_latched_value_on_inactive_clock_edge_when_synchronous_reset_is_active(self, fixture, reset_active, write_enable):
		fixture.input = Signal(intbv(val=0, min=0, max=2**32))
		fixture.reset = ResetSignal(val=not reset_active, active=reset_active, isasync=False)
		fixture.reset_value = fixture.any_reset_value()

		@instance
		def test():
			nonlocal fixture, reset_active, write_enable
			yield fixture.ensure_initial_state()
			fixture.enable_writes()
			latched_value = fixture.any_input_value_except(fixture.reset_value)
			fixture.input.next = latched_value
			yield fixture.clock_active()

			fixture.enable_writes(write_enable)
			fixture.reset.next = reset_active
			yield fixture.clock_inactive()

			assert fixture.register.output.val == latched_value

		self.run(fixture, test)

	@pytest.mark.parametrize("reset_active,write_enable", [
		[bool(0), bool(0)],
		[bool(0), bool(1)],
		[bool(1), bool(0)],
		[bool(1), bool(1)]])
	def test_output_is_reset_value_on_active_clock_edge_when_asynchronous_reset_is_active(self, fixture, reset_active, write_enable):
		fixture.input = Signal(intbv(val=0, min=0, max=2**32))
		fixture.reset = ResetSignal(val=not reset_active, active=reset_active, isasync=True)
		fixture.reset_value = fixture.any_reset_value()

		@instance
		def test():
			nonlocal fixture, reset_active, write_enable
			yield fixture.ensure_initial_state()
			fixture.enable_writes(write_enable)
			fixture.input.next = fixture.any_input_value_except(fixture.reset_value)
			fixture.reset.next = reset_active
			yield fixture.clock_active()
			assert fixture.register.output.val == fixture.reset_value

		self.run(fixture, test)

	@pytest.mark.parametrize("reset_active,write_enable", [
		[bool(0), bool(0)],
		[bool(0), bool(1)],
		[bool(1), bool(0)],
		[bool(1), bool(1)]])
	def test_output_is_reset_value_on_inactive_clock_edge_when_asynchronous_reset_is_active(self, fixture, reset_active, write_enable):
		fixture.input = Signal(intbv(val=0, min=0, max=2**32))
		fixture.reset = ResetSignal(val=not reset_active, active=reset_active, isasync=True)
		fixture.reset_value = fixture.any_reset_value()

		@instance
		def test():
			nonlocal fixture, reset_active, write_enable
			yield fixture.ensure_initial_state()
			fixture.enable_writes()
			fixture.input.next = fixture.any_input_value_except(fixture.reset_value)
			yield fixture.clock_active()

			fixture.enable_writes(write_enable)
			fixture.reset.next = reset_active
			yield fixture.clock_inactive()

			assert fixture.register.output.val == fixture.reset_value

		self.run(fixture, test)

	@pytest.mark.parametrize("reset_active,write_enable", [
		[bool(0), bool(0)],
		[bool(0), bool(1)],
		[bool(1), bool(0)],
		[bool(1), bool(1)]])
	def test_output_is_reset_value_without_clock_edge_when_asynchronous_reset_is_active(self, fixture, reset_active, write_enable):
		fixture.input = Signal(intbv(val=0, min=0, max=2**32))
		fixture.reset = ResetSignal(val=not reset_active, active=reset_active, isasync=True)
		fixture.reset_value = fixture.any_reset_value()

		@instance
		def test():
			nonlocal fixture, reset_active, write_enable
			yield fixture.ensure_initial_state()
			fixture.enable_writes()
			fixture.input.next = fixture.any_input_value_except(fixture.reset_value)
			yield fixture.clock_active()

			fixture.enable_writes(write_enable)
			fixture.reset.next = reset_active
			yield delay(1)

			assert fixture.register.output.val == fixture.reset_value

		self.run(fixture, test)

class TestRegisterForPositiveEdgesAndActiveHighWriteEnable(RegisterTestSuite):
	@pytest.fixture(scope="function")
	def fixture(self):
		return RegisterFixture(is_write_enable_active_high=True, negedge=False)

class TestRegisterForNegativeEdgesAndActiveHighWriteEnable(RegisterTestSuite):
	@pytest.fixture(scope="function")
	def fixture(self):
		return RegisterFixture(is_write_enable_active_high=True, negedge=True)

class TestRegisterForPositiveEdgesAndActiveLowWriteEnable(RegisterTestSuite):
	@pytest.fixture(scope="function")
	def fixture(self):
		return RegisterFixture(is_write_enable_active_high=False, negedge=False)

class TestRegisterForNegativeEdgesAndActiveLowWriteEnable(RegisterTestSuite):
	@pytest.fixture(scope="function")
	def fixture(self):
		return RegisterFixture(is_write_enable_active_high=False, negedge=True)
