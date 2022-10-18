import pytest
import random
from abc import ABC, abstractmethod
from myhdl import *
from src.primitives.accumulator import Accumulator
from src.primitives.sequential_logic_control_bus import SequentialLogicControlBus
from tests.cosimulatable_dut import CosimulatableDut, cycles

class AccumulatorFixture:
	def __init__(self, is_en_active_high, is_clk_posedge):
		seed = random.getrandbits(32)
		print(f"Using non-determinism seed: {seed}")
		random.seed(seed)

		self.bus = SequentialLogicControlBus(
			clk=Signal(not is_clk_posedge),
			reset=ResetSignal(val=bool(0), active=bool(0), isasync=False),
			en=Signal(val=bool(is_en_active_high)),
			is_clk_posedge=is_clk_posedge,
			is_en_active_high=is_en_active_high)

		self.width = self.any_width()
		self.addend = self.any_addend_value()
		self._accumulator = None
		self._dut = None

	def any_width(self):
		return self.any_int_in_range(1, 65)

	def any_int_in_range(self, min, half_open_max):
		return random.randint(min, half_open_max - 1)

	def any_addend_value(self):
		return self.any_int_in_range(1, 2**self.width)

	def any_addend_value_except(self, excluded):
		value = self.any_addend_value()
		if value != excluded:
			return value

		return self.any_addend_value_except(excluded)

	@property
	def accumulator(self):
		if self._accumulator is None:
			self._accumulator = Accumulator(self.bus, self.width, self.addend)

		return self._accumulator

	def ensure_initial_state(self):
		if self.bus.reset.isasync:
			yield self._reset_async()
		else:
			yield self._reset_sync()

	def _reset_async(self):
		self.bus.reset.next = self.bus.reset.active
		yield delay(cycles(1))
		self.bus.reset.next = not self.bus.reset.active
		yield delay(cycles(1))

	def _reset_sync(self):
		self.bus.reset.next = self.bus.reset.active
		yield self.clock_pulse()
		self.bus.reset.next = not self.bus.reset.active

	def clock_pulse(self):
		yield self.clock_active()
		yield self.clock_inactive()

	def clock_active(self):
		if self.bus.is_clk_posedge:
			yield self.clock_rise()
		else:
			yield self.clock_fall()

	def clock_rise(self):
		self.bus.clk.next = bool(1)
		yield delay(cycles(1))

	def clock_fall(self):
		self.bus.clk.next = bool(0)
		yield delay(cycles(1))

	def clock_inactive(self):
		if self.bus.is_clk_posedge:
			yield self.clock_fall()
		else:
			yield self.clock_rise()

	def at_least_one_clock_pulse(self):
		for _ in range(0, self.any_int_in_range(1, 10)):
			yield self.clock_pulse()

	def generators(self):
		if self._dut is None:
			self._dut = CosimulatableDut(self.accumulator.generators()).dut

		return self._dut

class AccumulatorTestSuite(ABC):
	@pytest.fixture(scope="function")
	@abstractmethod
	def fixture(self):
		...

	def run(self, fixture, *generators):
		sim = Simulation(fixture.generators(), *generators)
		sim.run()

	def test_constructor_with_no_bus_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)bus .+ must be specified"):
			Accumulator(None, fixture.width, fixture.addend)

	def test_constructor_with_no_width_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)accumulator width must be specified"):
			Accumulator(fixture.bus, None, fixture.addend)

	@pytest.mark.parametrize("invalid_width", [-1, -2, -3, 0])
	def test_constructor_with_invalid_width_raises_value_error(self, fixture, invalid_width):
		with pytest.raises(ValueError, match=r"(?i)width must be greater than zero"):
			Accumulator(fixture.bus, invalid_width, fixture.addend)

	def test_constructor_with_no_addend_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)addend must be specified"):
			Accumulator(fixture.bus, fixture.width, None)

	def test_constructor_with_zero_constant_addend_raises_value_error(self, fixture):
		with pytest.raises(ValueError, match=r"(?i)constant addend must not be zero"):
			Accumulator(fixture.bus, fixture.width, 0)

	@pytest.mark.parametrize("width", [1, 2, 10, 100])
	def test_constructor_with_constant_addend_equal_to_maximum_accumulator_value_then_no_value_error_is_raised(self, fixture, width):
		addend = 2**width - 1
		assert Accumulator(fixture.bus, width, addend) is not None

	@pytest.mark.parametrize("width,addend", [
		[1, 2],
		[1, 10],
		[9, 2**9],
		[10, 2**10 + 1]])
	def test_constructor_with_constant_addend_greater_than_maximum_accumulator_value_raises_value_error(self, fixture, width, addend):
		with pytest.raises(ValueError, match=r"(?i)addend is too wide for the accumulator"):
			Accumulator(fixture.bus, width, addend)

	@pytest.mark.parametrize("width", [1, 2, 10, 100])
	def test_constructor_with_signal_addend_equal_to_maximum_accumulator_value_then_no_value_error_is_raised(self, fixture, width):
		addend = Signal(modbv(val=0, min=0, max=2**width))
		assert Accumulator(fixture.bus, width, addend) is not None

	@pytest.mark.parametrize("accumulator_width,addend_width", [
		[1, 2],
		[1, 10],
		[4, 5],
		[11, 13]])
	def test_constructor_with_signal_addend_greater_than_accumulator_width_raises_value_error(self, fixture, accumulator_width, addend_width):
		addend = Signal(modbv(val=0, min=0, max=2**addend_width))
		with pytest.raises(ValueError, match=r"(?i)addend is too wide for the accumulator"):
			Accumulator(fixture.bus, accumulator_width, addend)

	def test_output_is_modbv_signal(self, fixture):
		expected = Signal(modbv())
		accumulator = Accumulator(fixture.bus, fixture.width, fixture.addend)
		assert isinstance(accumulator.output, expected.__class__)
		assert isinstance(accumulator.output.val, expected.val.__class__)

	def test_output_is_same_width_as_passed_to_constructor(self, fixture):
		for width in range(1, 64):
			accumulator = Accumulator(fixture.bus, width, 1)
			assert len(accumulator.output) == width

	def test_output_min_is_set_to_zero(self, fixture):
		for width in range(1, 64):
			accumulator = Accumulator(fixture.bus, width, 1)
			assert accumulator.output.min == 0

	def test_output_max_is_set_to_maximum_power_of_two_for_given_width(self, fixture):
		for width in range(1, 64):
			accumulator = Accumulator(fixture.bus, width, 1)
			assert accumulator.output.max == 2**width

	def test_output_is_zero_on_initial_state_for_constant_addend(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			assert fixture.accumulator.output == 0

		self.run(fixture, test)

	def test_output_is_zero_on_initial_state_for_signal_addend(self, fixture):
		fixture.addend = Signal(intbv(val=1, min=0, max=2**fixture.width))

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			assert fixture.accumulator.output == 0

		self.run(fixture, test)

	def test_output_is_addend_on_first_active_clock_edge_for_constant_addend(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_active()
			assert fixture.accumulator.output == fixture.addend

		self.run(fixture, test)

	def test_output_is_addend_on_first_active_clock_edge_for_non_zero_signal_addend(self, fixture):
		fixture.addend = Signal(intbv(val=fixture.any_addend_value(), min=0, max=2**fixture.width))

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			fixture.addend.next = fixture.any_addend_value()
			yield fixture.clock_active()
			assert fixture.accumulator.output == fixture.addend

		self.run(fixture, test)

	def test_output_is_still_zero_on_first_active_clock_edge_for_zero_signal_addend(self, fixture):
		fixture.addend = Signal(intbv(val=fixture.any_addend_value(), min=0, max=2**fixture.width))

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			fixture.addend.next = 0
			yield fixture.clock_active()
			assert fixture.accumulator.output == 0

		self.run(fixture, test)

	def test_output_is_not_changed_on_inactive_clock_edge_for_constant_addend(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_active()
			yield fixture.clock_inactive()
			assert fixture.accumulator.output == fixture.addend

		self.run(fixture, test)

	def test_output_is_not_changed_on_inactive_clock_edge_for_signal_addend(self, fixture):
		fixture.addend = Signal(intbv(val=fixture.any_addend_value(), min=0, max=2**fixture.width))

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_active()
			value_before_inactive_edge = int(fixture.accumulator.output)
			yield fixture.clock_inactive()
			assert fixture.accumulator.output == value_before_inactive_edge

		self.run(fixture, test)

	def test_output_is_twice_constant_addend_on_second_active_clock_edge(self, fixture):
		fixture.width = fixture.any_int_in_range(8, 16)
		fixture.addend = fixture.any_int_in_range(1, 128)

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			yield fixture.clock_active()
			assert fixture.accumulator.output == fixture.addend * 2

		self.run(fixture, test)

	def test_output_is_twice_signal_addend_on_second_active_clock_edge(self, fixture):
		fixture.width = fixture.any_int_in_range(8, 16)
		fixture.addend = Signal(intbv(val=fixture.any_int_in_range(1, 128), min=0, max=2**7))

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			yield fixture.clock_active()
			assert fixture.accumulator.output == fixture.addend * 2

		self.run(fixture, test)

	def test_output_is_accumulation_of_signal_addend_on_active_clock_edge_when_addend_changes(self, fixture):
		fixture.width = fixture.any_int_in_range(8, 16)
		fixture.addend = Signal(intbv(val=0, min=0, max=2**7))
		addend_values = [fixture.any_int_in_range(1, 64) for _ in range(0, 2)]

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()

			nonlocal addend_values
			fixture.addend.next = addend_values[0]
			yield fixture.clock_pulse()
			fixture.addend.next = addend_values[1]
			yield fixture.clock_active()
			assert fixture.accumulator.output == addend_values[0] + addend_values[1]

		self.run(fixture, test)

	@pytest.mark.parametrize("accumulator_width,addend,num_clocks,residual", [
		[1, 1, 2, 0],
		[2, 3, 2, 2],
		[2, 3, 3, 1],
		[2, 3, 4, 0],
		[14, 1024, 16, 0],
		[14, 1023, 17, 1007]])
	def test_output_is_residual_when_overflowed_by_constant_addend(self, fixture, accumulator_width, addend, num_clocks, residual):
		fixture.width = accumulator_width
		fixture.addend = addend
		self._test_output_is_residual_when_overflowed(fixture, num_clocks, residual)

	def _test_output_is_residual_when_overflowed(self, fixture, num_clocks, residual):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()

			nonlocal num_clocks
			for _ in range(0, num_clocks):
				yield fixture.clock_pulse()

			nonlocal residual
			assert fixture.accumulator.output == residual

		self.run(fixture, test)

	@pytest.mark.parametrize("accumulator_width,addend,num_clocks,residual", [
		[1, 1, 2, 0],
		[2, 3, 2, 2],
		[2, 3, 3, 1],
		[2, 3, 4, 0],
		[14, 1024, 16, 0],
		[14, 1023, 17, 1007]])
	def test_output_is_residual_when_overflowed_by_signal_addend(self, fixture, accumulator_width, addend, num_clocks, residual):
		fixture.width = accumulator_width
		fixture.addend = Signal(intbv(val=addend, min=0, max=2**accumulator_width))
		self._test_output_is_residual_when_overflowed(fixture, num_clocks, residual)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_output_is_not_zeroed_before_active_clock_edge_when_synchronous_reset_is_active(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=False))

		@instance
		def test():
			nonlocal fixture
			nonlocal is_reset_active_high
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			fixture.bus.reset.next = is_reset_active_high
			yield delay(1)
			assert fixture.accumulator.output == fixture.addend

		self.run(fixture, test)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_output_is_zeroed_on_active_clock_edge_when_synchronous_reset_is_active(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=False))

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			nonlocal is_reset_active_high
			fixture.bus.reset.next = is_reset_active_high
			yield fixture.clock_active()
			assert fixture.accumulator.output == 0

		self.run(fixture, test)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_output_remains_zero_on_active_clock_edge_when_synchronous_reset_is_active(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=False))

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			nonlocal is_reset_active_high
			fixture.bus.reset.next = is_reset_active_high
			yield fixture.clock_pulse()
			yield fixture.clock_active()
			assert fixture.accumulator.output == 0

		self.run(fixture, test)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_output_is_not_zeroed_on_inactive_clock_edge_when_synchronous_reset_is_active(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=False))

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			nonlocal is_reset_active_high
			fixture.bus.reset.next = is_reset_active_high
			yield fixture.clock_pulse()
			yield fixture.clock_active()
			yield fixture.clock_inactive()
			assert fixture.accumulator.output == 0

		self.run(fixture, test)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_output_is_not_zeroed_on_inactive_clock_edge_when_synchronous_reset_is_active(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=False))

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_active()
			nonlocal is_reset_active_high
			fixture.bus.reset.next = is_reset_active_high
			yield fixture.clock_inactive()
			assert fixture.accumulator.output == fixture.addend

		self.run(fixture, test)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_output_is_zeroed_immediately_when_asynchronous_reset_is_active_after_active_clock_edge(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=True))

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_active()
			nonlocal is_reset_active_high
			fixture.bus.reset.next = is_reset_active_high
			yield delay(1)
			assert fixture.accumulator.output == 0

		self.run(fixture, test)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_output_is_zeroed_immediately_when_asynchronous_reset_is_active_after_inactive_clock_edge(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=True))

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_active()
			yield fixture.clock_inactive()
			nonlocal is_reset_active_high
			fixture.bus.reset.next = is_reset_active_high
			yield delay(cycles(1))
			assert fixture.accumulator.output == 0

		self.run(fixture, test)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_output_remains_zeroed_when_asynchronous_reset_is_active(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=True))

		@instance
		def test():
			nonlocal fixture
			nonlocal is_reset_active_high
			yield fixture.ensure_initial_state()
			fixture.bus.reset.next = is_reset_active_high
			yield fixture.clock_pulse()
			assert fixture.accumulator.output == 0

		self.run(fixture, test)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_output_remains_zero_on_first_inactive_clock_edge_after_asynchronous_reset_goes_inactive(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=True))

		@instance
		def test():
			nonlocal fixture
			nonlocal is_reset_active_high
			yield fixture.ensure_initial_state()
			fixture.bus.reset.next = is_reset_active_high
			yield fixture.clock_active()
			fixture.bus.reset.next = not is_reset_active_high
			yield fixture.clock_inactive()
			assert fixture.accumulator.output == 0

		self.run(fixture, test)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_output_is_constant_addend_on_first_active_clock_edge_after_asynchronous_reset_goes_inactive(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=True))
		self._test_output_is_constant_addend_on_first_active_clock_edge_after_asynchronous_reset_goes_inactive(fixture, is_reset_active_high)

	def _test_output_is_constant_addend_on_first_active_clock_edge_after_asynchronous_reset_goes_inactive(self, fixture, is_reset_active_high):
		@instance
		def test():
			nonlocal fixture
			nonlocal is_reset_active_high
			yield fixture.ensure_initial_state()
			fixture.bus.reset.next = is_reset_active_high
			yield fixture.clock_active()
			fixture.bus.reset.next = not is_reset_active_high
			yield fixture.clock_inactive()
			yield fixture.clock_active()
			assert fixture.accumulator.output == fixture.addend

		self.run(fixture, test)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_output_is_signal_addend_on_first_active_clock_edge_after_asynchronous_reset_goes_inactive(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=True))
		fixture.addend = Signal(modbv(val=fixture.any_addend_value_except(0), min=0, max=2**fixture.width))
		self._test_output_is_constant_addend_on_first_active_clock_edge_after_asynchronous_reset_goes_inactive(fixture, is_reset_active_high)

	def test_output_is_not_updated_when_en_goes_inactive(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.at_least_one_clock_pulse()
			value_before_disabled = int(fixture.accumulator.output)
			fixture.bus.en.next = not fixture.bus.is_en_active_high
			yield delay(cycles(1))
			assert fixture.accumulator.output == value_before_disabled

		self.run(fixture, test)

	def test_output_is_not_updated_on_active_clock_edge_when_en_goes_inactive(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.at_least_one_clock_pulse()
			value_before_disabled = int(fixture.accumulator.output)
			fixture.bus.en.next = not fixture.bus.is_en_active_high
			yield fixture.clock_active()
			assert fixture.accumulator.output == value_before_disabled

		self.run(fixture, test)

	def test_output_resumes_accumulation_of_constant_addend_on_next_active_clock_edge_after_en_returns_to_active(self, fixture):
		self._test_output_resumes_accumulation_on_next_active_clock_edge_after_en_returns_to_active(fixture)

	def _test_output_resumes_accumulation_on_next_active_clock_edge_after_en_returns_to_active(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.at_least_one_clock_pulse()
			value_before_disabled = int(fixture.accumulator.output)
			fixture.bus.en.next = not fixture.bus.is_en_active_high
			yield fixture.at_least_one_clock_pulse()
			fixture.bus.en.next = fixture.bus.is_en_active_high
			yield fixture.clock_active()
			assert fixture.accumulator.output == (value_before_disabled + fixture.addend) % fixture.accumulator.output.max

		self.run(fixture, test)

	def test_output_resumes_accumulation_of_signal_addend_on_next_active_clock_edge_after_en_returns_to_active(self, fixture):
		fixture.addend = Signal(modbv(val=fixture.any_addend_value_except(0), min=0, max=2**fixture.width))
		self._test_output_resumes_accumulation_on_next_active_clock_edge_after_en_returns_to_active(fixture)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_output_is_zeroed_on_active_clock_edge_when_synchronous_reset_goes_active_and_en_is_inactive(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=False))

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			fixture.bus.en.next = not fixture.bus.is_en_active_high
			yield fixture.clock_pulse()

			nonlocal is_reset_active_high
			fixture.bus.reset.next = is_reset_active_high
			yield fixture.clock_active()
			assert fixture.accumulator.output == 0

		self.run(fixture, test)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_output_is_zeroed_immediately_when_asynchronous_reset_goes_active_and_en_is_inactive(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=True))

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			fixture.bus.en.next = not fixture.bus.is_en_active_high
			yield fixture.clock_active()

			nonlocal is_reset_active_high
			fixture.bus.reset.next = is_reset_active_high
			yield delay(cycles(1))
			assert fixture.accumulator.output == 0

		self.run(fixture, test)

	def test_overflow_is_boolean_signal(self, fixture):
		expected = Signal(bool(0))
		accumulator = Accumulator(fixture.bus, fixture.width, fixture.addend)
		assert isinstance(accumulator.overflow, expected.__class__)
		assert isinstance(accumulator.overflow.val, expected.val.__class__)

	def test_overflow_is_false_on_initial_state(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			assert not fixture.accumulator.overflow

		self.run(fixture, test)

	def test_overflow_is_false_on_first_active_clock_edge(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_active()
			assert not fixture.accumulator.overflow

		self.run(fixture, test)

	def test_overflow_is_false_on_first_inactive_clock_edge(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			assert not fixture.accumulator.overflow

		self.run(fixture, test)

	@pytest.mark.parametrize("accumulator_width,addend,num_clocks", [
		[1, 1, 2],
		[2, 3, 2],
		[2, 3, 3],
		[2, 3, 4],
		[14, 1024, 16],
		[14, 1023, 17]])
	def test_overflow_is_true_on_active_clock_edge_when_accumulator_has_overflowed_with_constant_addend(self, fixture, accumulator_width, addend, num_clocks):
		fixture.width = accumulator_width
		fixture.addend = addend
		self._test_overflow_is_true_on_active_clock_edge_when_accumulator_has_overflowed(fixture, num_clocks)

	def _test_overflow_is_true_on_active_clock_edge_when_accumulator_has_overflowed(self, fixture, num_clocks):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			for _ in range(0, num_clocks - 1):
				yield fixture.clock_pulse()

			yield fixture.clock_active()
			assert fixture.accumulator.overflow

		self.run(fixture, test)

	@pytest.mark.parametrize("accumulator_width,addend,num_clocks", [
		[1, 1, 2],
		[2, 3, 2],
		[2, 3, 3],
		[2, 3, 4],
		[14, 1024, 16],
		[14, 1023, 17]])
	def test_overflow_is_true_on_active_clock_edge_when_accumulator_has_overflowed_with_signal_addend(self, fixture, accumulator_width, addend, num_clocks):
		fixture.width = accumulator_width
		fixture.addend = Signal(modbv(val=addend, min=0, max=2**accumulator_width))
		self._test_overflow_is_true_on_active_clock_edge_when_accumulator_has_overflowed(fixture, num_clocks)

	def test_overflow_is_true_on_inactive_clock_edge_when_accumulator_has_overflowed_with_constant_addend(self, fixture):
		fixture.width = 8
		fixture.addend = 2**8 - 1
		self._test_overflow_is_true_on_inactive_clock_edge_when_accumulator_has_overflowed(fixture)

	def _test_overflow_is_true_on_inactive_clock_edge_when_accumulator_has_overflowed(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			yield fixture.clock_active()
			yield fixture.clock_inactive()
			assert fixture.accumulator.overflow

		self.run(fixture, test)

	def test_overflow_is_true_on_inactive_clock_edge_when_accumulator_has_overflowed_with_signal_addend(self, fixture):
		fixture.width = 8
		fixture.addend = Signal(modbv(val=2**8 - 1, min=0, max=2**fixture.width))
		self._test_overflow_is_true_on_inactive_clock_edge_when_accumulator_has_overflowed(fixture)

	def test_overflow_remains_active_on_active_clock_edge_when_accumulator_has_repeatedly_overflowed_with_constant_addend(self, fixture):
		fixture.width = 8
		fixture.addend = 2**8 - 1
		self._test_overflow_remains_active_on_active_clock_edge_when_accumulator_has_repeatedly_overflowed(fixture)

	def _test_overflow_remains_active_on_active_clock_edge_when_accumulator_has_repeatedly_overflowed(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			yield fixture.clock_pulse()
			yield fixture.clock_active()
			assert fixture.accumulator.overflow

		self.run(fixture, test)

	def test_overflow_remains_active_on_active_clock_edge_when_accumulator_has_repeatedly_overflowed_with_signal_addend(self, fixture):
		fixture.width = 8
		fixture.addend = Signal(modbv(val=2**8 - 1, min=0, max=2**fixture.width))
		self._test_overflow_remains_active_on_active_clock_edge_when_accumulator_has_repeatedly_overflowed(fixture)

	def test_overflow_is_reset_on_active_clock_edge_after_one_clock_cycle_when_accumulator_has_overflowed_with_constant_addend(self, fixture):
		fixture.width = 8
		fixture.addend = 2**7
		self._test_overflow_is_reset_on_active_clock_edge_after_one_clock_cycle_when_accumulator_has_overflowed(fixture)

	def _test_overflow_is_reset_on_active_clock_edge_after_one_clock_cycle_when_accumulator_has_overflowed(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			yield fixture.clock_pulse()
			yield fixture.clock_active()
			assert not fixture.accumulator.overflow

		self.run(fixture, test)

	def test_overflow_is_reset_on_active_clock_edge_after_one_clock_cycle_when_accumulator_has_overflowed_with_signal_addend(self, fixture):
		fixture.width = 8
		fixture.addend = Signal(modbv(val=2**7, min=0, max=2**fixture.width))
		self._test_overflow_is_reset_on_active_clock_edge_after_one_clock_cycle_when_accumulator_has_overflowed(fixture)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_overflow_is_reset_on_active_clock_edge_when_synchronous_reset_goes_active_and_en_is_active(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=False))
		fixture.width = 8
		fixture.addend = 2**8 - 1

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			yield fixture.clock_pulse()

			nonlocal is_reset_active_high
			fixture.bus.reset.next = is_reset_active_high
			yield fixture.clock_active()
			assert not fixture.accumulator.overflow

		self.run(fixture, test)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_overflow_is_reset_on_active_clock_edge_when_synchronous_reset_goes_active_and_en_is_inactive(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=False))
		fixture.width = 8
		fixture.addend = 2**8 - 1

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			yield fixture.clock_pulse()

			nonlocal is_reset_active_high
			fixture.bus.en.next = not fixture.bus.is_en_active_high
			yield delay(cycles(1))

			fixture.bus.reset.next = is_reset_active_high
			yield fixture.clock_active()
			assert not fixture.accumulator.overflow

		self.run(fixture, test)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_overflow_is_reset_immediately_when_asynchronous_reset_goes_active_and_en_is_active(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=True))
		fixture.width = 8
		fixture.addend = 2**8 - 1

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			yield fixture.clock_pulse()

			nonlocal is_reset_active_high
			fixture.bus.reset.next = is_reset_active_high
			yield delay(cycles(1))
			assert not fixture.accumulator.overflow

		self.run(fixture, test)

	@pytest.mark.parametrize("is_reset_active_high", [False, True])
	def test_overflow_is_reset_immediately_when_asynchronous_reset_goes_active_and_en_is_inactive(self, fixture, is_reset_active_high):
		fixture.bus = fixture.bus.with_reset(ResetSignal(bool(is_reset_active_high), active=is_reset_active_high, isasync=True))
		fixture.width = 8
		fixture.addend = 2**8 - 1

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			yield fixture.clock_pulse()

			nonlocal is_reset_active_high
			fixture.bus.en.next = not fixture.bus.is_en_active_high
			yield delay(cycles(1))

			fixture.bus.reset.next = is_reset_active_high
			yield delay(cycles(1))
			assert not fixture.accumulator.overflow

		self.run(fixture, test)

class TestAccumulatorForPositiveEdgesAndActiveHighEnable(AccumulatorTestSuite):
	@pytest.fixture(scope="function")
	def fixture(self):
		return AccumulatorFixture(is_en_active_high=True, is_clk_posedge=True)

class TestAccumulatorForPositiveEdgesAndActiveLowEnable(AccumulatorTestSuite):
	@pytest.fixture(scope="function")
	def fixture(self):
		return AccumulatorFixture(is_en_active_high=False, is_clk_posedge=True)

class TestAccumulatorForNegativeEdgesAndActiveHighEnable(AccumulatorTestSuite):
	@pytest.fixture(scope="function")
	def fixture(self):
		return AccumulatorFixture(is_en_active_high=True, is_clk_posedge=False)

class TestAccumulatorForNegativeEdgesAndActiveLowEnable(AccumulatorTestSuite):
	@pytest.fixture(scope="function")
	def fixture(self):
		return AccumulatorFixture(is_en_active_high=False, is_clk_posedge=False)
