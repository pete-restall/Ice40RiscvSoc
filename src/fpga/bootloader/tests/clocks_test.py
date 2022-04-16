import pytest
import random
from myhdl import *
from src.clocks import Clocks
from tests.cosimulatable_dut import CosimulatableDut

class ClocksFixture:
	def __init__(self):
		seed = random.getrandbits(32)
		print(f"Using non-determinism seed: {seed}")
		random.seed(seed)

		self._clk_100MHz = Signal(bool(0))
		self.reset = ResetSignal(val=bool(0), active=bool(0), isasync=False)
		self._clocks = None
		self._dut = None

	@property
	def clocks(self):
		if self._clocks is None:
			self._clocks = Clocks(
				self._clk_100MHz,
				self.reset)

		return self._clocks

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
		yield self.clock_rise()
		yield self.clock_fall()

	def clock_rise(self):
		self._clk_100MHz.next = bool(1)
		yield delay(1)

	def clock_fall(self):
		self._clk_100MHz.next = bool(0)
		yield delay(1)

	def clock_toggle(self):
		self._clk_100MHz.next = not self._clk_100MHz.val
		yield delay(1)

	@property
	def clk_100MHz(self):
		return self._clk_100MHz

	def generators(self):
		if self._dut is None:
			self._dut = CosimulatableDut(self.clocks.generators()).dut

		return self._dut

class TestClocks:
	@pytest.fixture(scope="function")
	def fixture(self):
		return ClocksFixture()

	def run(self, fixture, *generators):
		sim = Simulation(fixture.generators(), *generators)
		sim.run()

	def test_constructor_with_no_clk_raises_type_error(self, fixture):
		with pytest.raises(TypeError):
			Clocks(None, fixture.reset)

	def test_constructor_with_no_reset_raises_type_error(self, fixture):
		with pytest.raises(TypeError):
			Clocks(fixture.clk_100MHz, None)

	def test_clk_50MHz_is_low_on_initial_state(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			assert fixture.clocks.clk_50MHz == bool(0)

		self.run(fixture, test)

	def test_clk_25MHz_is_low_on_initial_state(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			assert fixture.clocks.clk_50MHz == bool(0)

		self.run(fixture, test)

	def test_clk_50MHz_toggles_at_half_of_100MHz_on_rising_edges(self, fixture):
		@instance
		def test():
			edge_expectations = [1, 1, 0, 0] * 10

			nonlocal fixture
			yield fixture.ensure_initial_state()
			for expected in edge_expectations:
				yield fixture.clock_toggle()
				assert fixture.clocks.clk_50MHz == expected

		self.run(fixture, test)

	def test_clk_25MHz_toggles_at_quarter_of_100MHz_on_rising_edges(self, fixture):
		@instance
		def test():
			edge_expectations = [0, 0, 1, 1, 1, 1, 0, 0] * 10

			nonlocal fixture
			yield fixture.ensure_initial_state()
			for expected in edge_expectations:
				yield fixture.clock_toggle()
				assert fixture.clocks.clk_25MHz == expected

		self.run(fixture, test)

	def test_clk_20MHz_toggles_at_a_fifth_of_100MHz_on_rising_edges_with_50_percent_duty_cycle(self, fixture):
		@instance
		def test():
			edge_expectations = [0, 0, 1, 1, 1, 1, 1, 0, 0, 0] * 10

			nonlocal fixture
			yield fixture.ensure_initial_state()
			for expected in edge_expectations:
				yield fixture.clock_toggle()
				assert fixture.clocks.clk_20MHz == expected

		self.run(fixture, test)

	def test_clk_12_5MHz_toggles_at_an_eigth_of_100MHz_on_rising_edges(self, fixture):
		@instance
		def test():
			edge_expectations = [0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0] * 10

			nonlocal fixture
			yield fixture.ensure_initial_state()
			for expected in edge_expectations:
				yield fixture.clock_toggle()
				assert fixture.clocks.clk_12_5MHz == expected

		self.run(fixture, test)
