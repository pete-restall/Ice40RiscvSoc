import pytest
import random
from abc import ABC, abstractmethod
from myhdl import *
from src.primitives.counter import Counter
from src.primitives.sequential_logic_control_bus import SequentialLogicControlBus
from tests.cosimulatable_dut import CosimulatableDut, cycles

class CounterFixture:
	def __init__(self, is_clk_posedge):
		seed = random.getrandbits(32)
		print(f"Using non-determinism seed: {seed}")
		random.seed(seed)

		self.bus = SequentialLogicControlBus(
			clk=Signal(not is_clk_posedge),
			reset=ResetSignal(val=bool(0), active=bool(0), isasync=False),
			en=Signal(bool(1)),
			is_clk_posedge=is_clk_posedge,
			is_en_active_high=True)

		self.min = self.any_min()
		self.max = self.any_max_over(self.min)
		self._counter = None
		self._dut = None

	def any_min(self):
		return self.any_int_in_range(1, 257)

	def any_int_in_range(self, min, half_open_max):
		return random.randint(min, half_open_max - 1)

	def any_max_over(self, min):
		return self.any_int_in_range(min + 1, min + 101)

	@property
	def counter(self):
		if self._counter is None:
			self._counter = Counter(self.bus, self.min, self.max)

		return self._counter

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
			self._dut = CosimulatableDut(self.counter.generators()).dut

		return self._dut

class CounterTestSuite(ABC):
	@pytest.fixture(scope="function")
	@abstractmethod
	def fixture(self):
		...

	def run(self, fixture, *generators):
		sim = Simulation(fixture.generators(), *generators)
		sim.run()

	def test_constructor_with_no_bus_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)bus .+ must be specified"):
			Counter(None, fixture.min, fixture.max)

	@pytest.mark.parametrize("relative_max", [-1, -2, -3, -10])
	def test_constructor_with_max_greater_than_min_raises_value_error(self, fixture, relative_max):
		with pytest.raises(ValueError, match=r"(?i)maximum must be greater than minimum"):
			Counter(fixture.bus, fixture.min, fixture.min + relative_max)

	def test_constructor_with_min_and_max_the_same_raises_value_error(self, fixture):
		with pytest.raises(ValueError, match=r"(?i)maximum must be greater than minimum"):
			Counter(fixture.bus, fixture.min, fixture.min)

	def test_output_is_modbv_signal_when_min_is_zero_and_max_is_power_of_two_take_one(self, fixture):
		expected = Signal(modbv())
		for i in range(1, 32):
			max = (1 << i) - 1
			counter = Counter(fixture.bus, 0, max)
			assert isinstance(counter.output, expected.__class__)
			assert isinstance(counter.output.val, expected.val.__class__)

	def test_output_min_is_set_to_zero_when_it_is_modbv(self, fixture):
		for i in range(1, 32):
			max = (1 << i) - 1
			counter = Counter(fixture.bus, 0, max)
			assert counter.output.min == 0

	def test_output_max_is_set_to_incremented_max_as_passed_to_constructor_when_it_is_modbv(self, fixture):
		for i in range(1, 32):
			max = (1 << i) - 1
			counter = Counter(fixture.bus, 0, max)
			assert counter.output.max == max + 1

	def test_output_min_is_set_to_same_as_passed_to_constructor_when_it_is_intbv(self, fixture):
		assert fixture.counter.output.min == fixture.min

	def test_output_max_is_set_to_incremented_max_passed_to_constructor_when_it_is_intbv(self, fixture):
		assert fixture.counter.output.max == fixture.max + 1

	@pytest.mark.parametrize("min,max", [
		[1, 3],
		[2, 3],
		[3, 15],
		[10, 127]])
	def test_output_is_intbv_signal_when_min_is_not_zero_and_max_is_power_of_two_take_one(self, fixture, min, max):
		expected = Signal(intbv())
		counter = Counter(fixture.bus, min, max)
		assert isinstance(counter.output, expected.__class__)
		assert isinstance(counter.output.val, expected.val.__class__)

	def test_output_is_min_on_initial_state(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			assert fixture.counter.output == fixture.min

		self.run(fixture, test)

	def test_output_is_min_incremented_on_first_active_edge(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_active()
			assert fixture.counter.output == fixture.min + 1

		self.run(fixture, test)

	def test_output_is_min_incremented_twice_on_second_active_edge(self, fixture):
		fixture.max = fixture.min + 3

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			yield fixture.clock_active()
			assert fixture.counter.output == fixture.min + 2

		self.run(fixture, test)

	@pytest.mark.parametrize("max", [1, 3, 7, 15])
	def test_output_wraps_to_zero_on_active_edge_when_incremented_past_max_and_it_is_modbv(self, fixture, max):
		fixture.min = 0
		fixture.max = max

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			for _ in range(0, fixture.max):
				yield fixture.clock_pulse()

			assert fixture.counter.output == fixture.max
			yield fixture.clock_active()
			assert fixture.counter.output == 0

		self.run(fixture, test)

	@pytest.mark.parametrize("min,max", [
		[1, 3],
		[2, 3],
		[5, 10],
		[2, 13]])
	def test_output_wraps_to_min_on_active_edge_when_incremented_past_max_and_it_is_intbv(self, fixture, min, max):
		fixture.min = min
		fixture.max = max

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			for _ in range(fixture.min, fixture.max):
				yield fixture.clock_pulse()

			assert fixture.counter.output == fixture.max
			yield fixture.clock_active()
			assert fixture.counter.output == fixture.min

		self.run(fixture, test)

	@pytest.mark.parametrize("reset_active,min,max", [
		[bool(0), 0, 10],
		[bool(0), 1, 10],
		[bool(0), 0, 7],
		[bool(1), 0, 2],
		[bool(1), 1, 12],
		[bool(1), 0, 15]])
	def test_output_is_reset_to_min_before_clock_inactive_edge_when_asynchronous_reset_is_active_when_clock_active(self, fixture, reset_active, min, max):
		fixture.bus = fixture.bus.with_reset(ResetSignal(val=reset_active, active=reset_active, isasync=True))
		fixture.min = min
		fixture.max = max

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()

			yield fixture.clock_active()
			fixture.bus.reset.next = fixture.bus.reset.active
			yield delay(cycles(1))
			assert fixture.counter.output == fixture.min

		self.run(fixture, test)

	@pytest.mark.parametrize("reset_active,min,max", [
		[bool(0), 0, 8],
		[bool(0), 3, 15],
		[bool(0), 0, 3],
		[bool(1), 0, 20],
		[bool(1), 1, 15],
		[bool(1), 0, 15]])
	def test_output_is_reset_to_min_before_clock_inactive_edge_when_asynchronous_reset_is_active_when_clock_active(self, fixture, reset_active, min, max):
		fixture.bus = fixture.bus.with_reset(ResetSignal(val=reset_active, active=reset_active, isasync=True))
		fixture.min = min
		fixture.max = max

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()

			fixture.bus.reset.next = fixture.bus.reset.active
			yield delay(cycles(1))
			assert fixture.counter.output == fixture.min

		self.run(fixture, test)

	@pytest.mark.parametrize("reset_active,min,max", [
		[bool(0), 0, 10],
		[bool(0), 1, 10],
		[bool(0), 0, 7],
		[bool(1), 0, 2],
		[bool(1), 1, 12],
		[bool(1), 0, 15]])
	def test_output_is_not_reset_to_min_before_clock_inactive_edge_when_synchronous_reset_is_active_during_clock_active(self, fixture, reset_active, min, max):
		fixture.bus = fixture.bus.with_reset(ResetSignal(val=reset_active, active=reset_active, isasync=False))
		fixture.min = min
		fixture.max = max

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()

			yield fixture.clock_active()
			fixture.bus.reset.next = fixture.bus.reset.active
			yield delay(cycles(1))
			yield fixture.clock_inactive()
			assert fixture.counter.output == fixture.min + 2

		self.run(fixture, test)

	@pytest.mark.parametrize("reset_active,min,max", [
		[bool(0), 0, 8],
		[bool(0), 3, 15],
		[bool(0), 0, 3],
		[bool(1), 0, 20],
		[bool(1), 1, 15],
		[bool(1), 0, 15]])
	def test_output_is_not_reset_to_min_before_active_clock_edge_when_synchronous_reset_is_active_during_clock_inactive(self, fixture, reset_active, min, max):
		fixture.bus = fixture.bus.with_reset(ResetSignal(val=reset_active, active=reset_active, isasync=False))
		fixture.min = min
		fixture.max = max

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()

			fixture.bus.reset.next = fixture.bus.reset.active
			yield delay(cycles(1))
			assert fixture.counter.output == fixture.min + 1

		self.run(fixture, test)

	@pytest.mark.parametrize("reset_active,min,max", [
		[bool(0), 0, 8],
		[bool(0), 3, 15],
		[bool(0), 0, 3],
		[bool(1), 0, 20],
		[bool(1), 1, 15],
		[bool(1), 0, 15]])
	def test_output_is_reset_to_min_on_clock_active_edge_when_synchronous_reset_is_active_during_clock_active(self, fixture, reset_active, min, max):
		fixture.bus = fixture.bus.with_reset(ResetSignal(val=reset_active, active=reset_active, isasync=False))
		fixture.min = min
		fixture.max = max

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()

			yield fixture.clock_active()
			fixture.bus.reset.next = fixture.bus.reset.active
			yield delay(cycles(1))
			yield fixture.clock_inactive()
			yield fixture.clock_active()
			assert fixture.counter.output == fixture.min

		self.run(fixture, test)

	@pytest.mark.parametrize("reset_active,min,max", [
		[bool(0), 0, 8],
		[bool(0), 3, 15],
		[bool(0), 0, 3],
		[bool(1), 0, 20],
		[bool(1), 1, 15],
		[bool(1), 0, 15]])
	def test_output_is_reset_to_min_on_clock_active_edge_when_synchronous_reset_is_active_during_clock_inactive(self, fixture, reset_active, min, max):
		fixture.bus = fixture.bus.with_reset(ResetSignal(val=reset_active, active=reset_active, isasync=False))
		fixture.min = min
		fixture.max = max

		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()

			fixture.bus.reset.next = fixture.bus.reset.active
			yield fixture.clock_active()
			assert fixture.counter.output == fixture.min

		self.run(fixture, test)

	@pytest.mark.parametrize("is_en_active_high", [False, True])
	def test_intbv_counter_does_not_increment_when_en_is_inactive(self, fixture, is_en_active_high):
		fixture.bus = fixture.bus.with_en_active_high(is_en_active_high)
		fixture.min = 0
		fixture.max = 133
		self._test_counter_does_not_increment_when_en_is_inactive(fixture, is_en_active_high)

	def _test_counter_does_not_increment_when_en_is_inactive(self, fixture, is_en_active_high):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.at_least_one_clock_pulse()
			value_before_increment = int(fixture.counter.output)

			fixture.bus.en.next = not is_en_active_high
			yield fixture.clock_active()
			assert fixture.counter.output == value_before_increment

		self.run(fixture, test)

	@pytest.mark.parametrize("is_en_active_high", [False, True])
	def test_modbv_counter_does_not_increment_when_en_is_inactive(self, fixture, is_en_active_high):
		fixture.bus = fixture.bus.with_en_active_high(is_en_active_high)
		fixture.min = 0
		fixture.max = 127
		self._test_counter_does_not_increment_when_en_is_inactive(fixture, is_en_active_high)

	@pytest.mark.parametrize("is_en_active_high", [False, True])
	def test_intbv_counter_resumes_when_en_returns_to_active(self, fixture, is_en_active_high):
		fixture.bus = fixture.bus.with_en_active_high(is_en_active_high)
		fixture.min = 0
		fixture.max = 13
		self._test_counter_resumes_when_en_returns_to_active(fixture, is_en_active_high)

	def _test_counter_resumes_when_en_returns_to_active(self, fixture, is_en_active_high):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.at_least_one_clock_pulse()
			value_before_increment = int(fixture.counter.output)
			fixture.bus.en.next = not is_en_active_high
			yield fixture.at_least_one_clock_pulse()
			fixture.bus.en.next = is_en_active_high
			yield fixture.clock_active()

			assert fixture.counter.output == (value_before_increment + 1) % fixture.counter.output.max

		self.run(fixture, test)

	@pytest.mark.parametrize("is_en_active_high", [False, True])
	def test_modbv_counter_resumes_when_en_returns_to_active(self, fixture, is_en_active_high):
		fixture.bus = fixture.bus.with_en_active_high(is_en_active_high)
		fixture.min = 0
		fixture.max = 15
		self._test_counter_resumes_when_en_returns_to_active(fixture, is_en_active_high)

	@pytest.mark.parametrize("is_en_active_high", [False, True])
	def test_intbv_counter_can_still_be_reset_when_en_is_inactive(self, fixture, is_en_active_high):
		fixture.bus = fixture.bus.with_en_active_high(is_en_active_high)
		fixture.min = 0
		fixture.max = 62
		self._test_counter_can_still_be_reset_when_en_is_inactive(fixture, is_en_active_high)

	def _test_counter_can_still_be_reset_when_en_is_inactive(self, fixture, is_en_active_high):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			yield fixture.clock_pulse()
			fixture.bus.en.next = not is_en_active_high
			yield delay(cycles(1))
			fixture.bus.reset.next = fixture.bus.reset.active
			yield fixture.clock_active()
			assert fixture.counter.output == fixture.counter.output.min

		self.run(fixture, test)

	@pytest.mark.parametrize("is_en_active_high", [False, True])
	def test_modbv_counter_can_still_be_reset_when_en_is_inactive(self, fixture, is_en_active_high):
		fixture.bus = fixture.bus.with_en_active_high(is_en_active_high)
		fixture.min = 0
		fixture.max = 63
		self._test_counter_can_still_be_reset_when_en_is_inactive(fixture, is_en_active_high)

class TestCounterForPositiveEdges(CounterTestSuite):
	@pytest.fixture(scope="function")
	def fixture(self):
		return CounterFixture(is_clk_posedge=True)

class TestCounterForNegativeEdges(CounterTestSuite):
	@pytest.fixture(scope="function")
	def fixture(self):
		return CounterFixture(is_clk_posedge=False)
