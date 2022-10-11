import pytest
import random
from myhdl import *
from src.reset.reset_controller import ResetController
from tests.cosimulatable_dut import CosimulatableDut, cycles

class ResetControllerFixture:
	def __init__(self):
		seed = random.getrandbits(32)
		print(f"Using non-determinism seed: {seed}")
		random.seed(seed)

		self._clk = Signal(val=bool(0))
		self.reset_in = ResetSignal(val=bool(0), active=bool(0), isasync=True)
		self.reset_out = ResetSignal(val=bool(0), active=bool(0), isasync=False)
		self.num_assertion_cycles = self.any_reasonable_num_assertion_cycles()
		self._reset_controller = None
		self._dut = None

	def any_reasonable_num_assertion_cycles(self, min=1):
		return random.randint(min, max(63, min + 10))

	@property
	def clk(self):
		return self._clk

	@property
	def reset_controller(self):
		if self._reset_controller is None:
			self._reset_controller = ResetController(
				self._clk,
				self.reset_in,
				self.reset_out,
				self.num_assertion_cycles)

		return self._reset_controller

	def clock_rise(self):
		self.clk.next = bool(1)
		yield delay(cycles(1))

	def clock_fall(self):
		self.clk.next = bool(0)
		yield delay(cycles(1))

	def generators(self):
		if self._dut is None:
			self._dut = CosimulatableDut(self.reset_controller.generators()).dut

		return self._dut

class TestResetController:
	ACTIVE_IN_OUT_SCENARIOS = [
		"active_in,active_out",
		[
			[bool(0), bool(0)],
			[bool(0), bool(1)],
			[bool(1), bool(0)],
			[bool(1), bool(1)]
		]
	]

	@pytest.fixture(scope="function")
	def fixture(self):
		return ResetControllerFixture()

	def run(self, fixture, *generators):
		sim = Simulation(fixture.generators(), *generators)
		sim.run()

	def test_constructor_with_no_clk_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)clock must be specified"):
			ResetController(None, fixture.reset_in, fixture.reset_out, fixture.num_assertion_cycles)

	def test_constructor_with_no_reset_in_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)reset input signal must be specified"):
			ResetController(fixture.clk, None, fixture.reset_out, fixture.num_assertion_cycles)

	def test_constructor_with_no_reset_out_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)reset output signal must be specified"):
			ResetController(fixture.clk, fixture.reset_in, None, fixture.num_assertion_cycles)

	@pytest.mark.parametrize("invalid_num_assertion_cycles", [
		0,
		-1,
		-2,
		-10
	])
	def test_constructor_with_invalid_num_assertion_cycles_raises_value_error(self, fixture, invalid_num_assertion_cycles):
		with pytest.raises(ValueError, match=r"(?i)assertion cycles must be at least 1"):
			ResetController(fixture.clk, fixture.reset_in, fixture.reset_out, invalid_num_assertion_cycles)

	@pytest.mark.parametrize(*ACTIVE_IN_OUT_SCENARIOS)
	def test_reset_out_is_active_after_first_clock_edge_when_inactive_reset_in(self, fixture, active_in, active_out):
		fixture.num_assertion_cycles = fixture.any_reasonable_num_assertion_cycles(min=2)
		fixture.reset_in = ResetSignal(val=not active_in, active=active_in, isasync=True)
		fixture.reset_out = ResetSignal(val=not active_out, active=active_out, isasync=False)

		@instance
		def test():
			nonlocal fixture
			yield fixture.clock_rise()
			assert fixture.reset_out == fixture.reset_out.active

		self.run(fixture, test)

	@pytest.mark.parametrize(*ACTIVE_IN_OUT_SCENARIOS)
	def test_reset_out_is_active_after_first_clock_edge_when_active_reset_in(self, fixture, active_in, active_out):
		fixture.num_assertion_cycles = fixture.any_reasonable_num_assertion_cycles(min=2)
		fixture.reset_in = ResetSignal(val=active_in, active=active_in, isasync=True)
		fixture.reset_out = ResetSignal(val=not active_out, active=active_out, isasync=False)

		@instance
		def test():
			nonlocal fixture
			yield fixture.clock_rise()
			assert fixture.reset_out == fixture.reset_out.active

		self.run(fixture, test)

	@pytest.mark.parametrize(*ACTIVE_IN_OUT_SCENARIOS)
	def test_reset_out_is_deactived_on_rising_edge_after_num_assertion_cycles_when_inactive_reset_in(self, fixture, active_in, active_out):
		fixture.reset_in = ResetSignal(val=not active_in, active=active_in, isasync=True)
		fixture.reset_out = ResetSignal(val=not active_out, active=active_out, isasync=False)

		@instance
		def test():
			nonlocal fixture
			for _ in range(0, fixture.num_assertion_cycles):
				yield fixture.clock_rise()
				assert fixture.reset_out == fixture.reset_out.active
				yield fixture.clock_fall()
				assert fixture.reset_out == fixture.reset_out.active

			yield fixture.clock_rise()
			assert fixture.reset_out != fixture.reset_out.active

		self.run(fixture, test)

	@pytest.mark.parametrize(*ACTIVE_IN_OUT_SCENARIOS)
	def test_reset_out_is_not_deactived_after_num_assertion_cycles_when_active_reset_in(self, fixture, active_in, active_out):
		fixture.reset_in = ResetSignal(val=active_in, active=active_in, isasync=True)
		fixture.reset_out = ResetSignal(val=not active_out, active=active_out, isasync=False)

		@instance
		def test():
			nonlocal fixture
			for _ in range(0, fixture.num_assertion_cycles):
				yield fixture.clock_rise()
				assert fixture.reset_out == fixture.reset_out.active
				yield fixture.clock_fall()
				assert fixture.reset_out == fixture.reset_out.active

		self.run(fixture, test)

	@pytest.mark.parametrize(*ACTIVE_IN_OUT_SCENARIOS)
	def test_reset_out_is_not_deactived_after_num_assertion_cycles_when_inactive_reset_in_becomes_active_during_clock_high(self, fixture, active_in, active_out):
		fixture.reset_in = ResetSignal(val=not active_in, active=active_in, isasync=True)
		fixture.reset_out = ResetSignal(val=not active_out, active=active_out, isasync=False)
		fixture.num_assertion_cycles = fixture.any_reasonable_num_assertion_cycles(min=2)
		trigger_after = random.randint(0, fixture.num_assertion_cycles - 2)

		@instance
		def test():
			nonlocal fixture
			for i in range(0, fixture.num_assertion_cycles):
				yield fixture.clock_rise()

				if i == trigger_after:
					fixture.reset_in.next = fixture.reset_in.active
					yield delay(cycles(1))

				yield fixture.clock_fall()

			assert fixture.reset_out == fixture.reset_out.active

		self.run(fixture, test)

	@pytest.mark.parametrize(*ACTIVE_IN_OUT_SCENARIOS)
	def test_reset_out_is_not_deactived_after_num_assertion_cycles_when_inactive_reset_in_becomes_active_during_clock_low(self, fixture, active_in, active_out):
		fixture.reset_in = ResetSignal(val=not active_in, active=active_in, isasync=True)
		fixture.reset_out = ResetSignal(val=not active_out, active=active_out, isasync=False)
		fixture.num_assertion_cycles = fixture.any_reasonable_num_assertion_cycles(min=2)
		trigger_after = random.randint(0, fixture.num_assertion_cycles - 2)

		@instance
		def test():
			nonlocal fixture
			for i in range(0, fixture.num_assertion_cycles):
				if i == trigger_after:
					fixture.reset_in.next = fixture.reset_in.active
					yield delay(cycles(1))

				yield fixture.clock_rise()
				yield fixture.clock_fall()

			assert fixture.reset_out == fixture.reset_out.active

		self.run(fixture, test)

	@pytest.mark.parametrize(*ACTIVE_IN_OUT_SCENARIOS)
	def test_reset_out_is_not_deactivated_after_num_assertion_cycles_when_reset_in_stays_active(self, fixture, active_in, active_out):
		fixture.reset_in = ResetSignal(val=active_in, active=active_in, isasync=True)
		fixture.reset_out = ResetSignal(val=not active_out, active=active_out, isasync=False)

		@instance
		def test():
			nonlocal fixture
			for i in range(0, fixture.num_assertion_cycles):
				yield fixture.clock_rise()
				yield fixture.clock_fall()

			assert fixture.reset_out == fixture.reset_out.active

		self.run(fixture, test)

	@pytest.mark.parametrize(*ACTIVE_IN_OUT_SCENARIOS)
	def test_reset_out_is_deactivated_after_num_assertion_cycles_when_reset_in_goes_inactive_during_clock_low(self, fixture, active_in, active_out):
		fixture.reset_in = ResetSignal(val=active_in, active=active_in, isasync=True)
		fixture.reset_out = ResetSignal(val=not active_out, active=active_out, isasync=False)
		fixture.num_assertion_cycles = fixture.any_reasonable_num_assertion_cycles(min=3)
		deactivate_after = random.randint(1, fixture.num_assertion_cycles - 2)

		@instance
		def test():
			nonlocal fixture
			for i in range(0, fixture.num_assertion_cycles + deactivate_after):
				if i == deactivate_after:
					fixture.reset_in.next = not fixture.reset_in.active
					yield delay(cycles(1))

				yield fixture.clock_rise()
				yield fixture.clock_fall()
				assert fixture.reset_out == fixture.reset_out.active

			yield fixture.clock_rise()
			assert fixture.reset_out != fixture.reset_out.active

		self.run(fixture, test)

	@pytest.mark.parametrize(*ACTIVE_IN_OUT_SCENARIOS)
	def test_reset_out_is_deactivated_after_num_assertion_cycles_when_reset_in_goes_inactive_during_clock_high(self, fixture, active_in, active_out):
		fixture.reset_in = ResetSignal(val=active_in, active=active_in, isasync=True)
		fixture.reset_out = ResetSignal(val=not active_out, active=active_out, isasync=False)
		fixture.num_assertion_cycles = fixture.any_reasonable_num_assertion_cycles(min=3)
		deactivate_after = random.randint(1, fixture.num_assertion_cycles - 2)

		@instance
		def test():
			nonlocal fixture
			for i in range(0, fixture.num_assertion_cycles + deactivate_after + 1):
				yield fixture.clock_rise()

				if i == deactivate_after:
					fixture.reset_in.next = not fixture.reset_in.active
					yield delay(cycles(1))

				yield fixture.clock_fall()
				assert fixture.reset_out == fixture.reset_out.active

			yield fixture.clock_rise()
			yield delay(cycles(1))
			assert fixture.reset_out != fixture.reset_out.active

		self.run(fixture, test)
