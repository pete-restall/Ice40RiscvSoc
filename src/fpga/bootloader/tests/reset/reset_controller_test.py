import pytest
import random
from myhdl import *
from src.reset.reset_controller import ResetController
from tests.cosimulatable_dut import CosimulatableDut

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
		yield delay(1)

	def generators(self):
		if self._dut is None:
			self._dut = CosimulatableDut(self.reset_controller.generators()).dut

		return self._dut

class TestResetController:
	@pytest.fixture(scope="function")
	def fixture(self):
		return ResetControllerFixture()

	def run(self, fixture, *generators):
		sim = Simulation(fixture.generators(), *generators)
		sim.run()

	def test_constructor_with_no_clk_raises_type_error(self, fixture):
		with pytest.raises(TypeError):
			ResetController(None, fixture.reset_in, fixture.reset_out, fixture.num_assertion_cycles)

	def test_constructor_with_no_reset_in_raises_type_error(self, fixture):
		with pytest.raises(TypeError):
			ResetController(fixture.clk, None, fixture.reset_out, fixture.num_assertion_cycles)

	def test_constructor_with_no_reset_out_raises_type_error(self, fixture):
		with pytest.raises(TypeError):
			ResetController(fixture.clk, fixture.reset_in, None, fixture.num_assertion_cycles)

	@pytest.mark.parametrize("invalid_num_assertion_cycles", [
		0,
		-1,
		-2,
		-10
	])
	def test_constructor_with_invalid_num_assertion_cycles_raises_value_error(self, fixture, invalid_num_assertion_cycles):
		with pytest.raises(ValueError):
			ResetController(fixture.clk, fixture.reset_in, fixture.reset_out, invalid_num_assertion_cycles)

	@pytest.mark.parametrize("active_in,active_out", [
		[bool(0), bool(0)],
		[bool(0), bool(1)],
		[bool(1), bool(0)],
		[bool(1), bool(1)]])
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

	@pytest.mark.parametrize("active_in,active_out", [
		[bool(0), bool(0)],
		[bool(0), bool(1)],
		[bool(1), bool(0)],
		[bool(1), bool(1)]])
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
