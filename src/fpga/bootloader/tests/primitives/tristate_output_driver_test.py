import pytest
from myhdl import *
from tests.cosimulatable_dut import *
from tests.expose_tristate_pin_state import *
from src.primitives.tristate_output_driver import *

class TristateOutputDriverFixture:
	def __init__(self):
		self.pin_state = TristatePinStateMonitor()
		self.rst = ResetSignal(False, False, True)
		self.en = ResetSignal(False, False, True)
		self.val = Signal(bool(0))

		@block
		def dut_not_passing_tristate_signal_into_cosim(
			pin_state,
			reset,
			en,
			val):

			pin = TristateSignal(bool(0))
			return (
				TristateOutputDriver(pin=pin, reset=reset, en=en, val=val),
				ExposeTristatePinState(pin, pin_state))

		self.dut = CosimulatableDut(
			dut_not_passing_tristate_signal_into_cosim(
				pin_state=self.pin_state,
				reset=self.rst,
				en=self.en,
				val=self.val)
			).dut

	def reset(self, r):
		self.rst.next = self.rst.active if r else not self.rst.active

	def enable(self):
		self.en.next = self.en.active

	def disable(self):
		self.en.next = not self.en.active

	def output(self, v):
		self.val.next = v

	@property
	def pin(self):
		if self.pin_state.is_low:
			return False
		elif self.pin_state.is_high:
			return True

		return None

	def generators(self):
		return self.dut

class TestOutputTristateDriver:
	@pytest.fixture(scope="function")
	def fixture(self):
		return TristateOutputDriverFixture()

	def run(self, fixture, generators):
		sim = Simulation(fixture.generators(), generators)
		sim.run()

	def test_pin_can_be_driven_high_when_enabled(self, fixture):
		@instance
		def test():
			nonlocal fixture
			fixture.reset(False)
			fixture.enable()
			fixture.output(True)
			yield delay(cycles(1))
			assert fixture.pin == True

		self.run(fixture, test)

	def test_pin_can_be_driven_low_when_enabled(self, fixture):
		@instance
		def test():
			nonlocal fixture
			fixture.reset(False)
			fixture.enable()
			fixture.output(False)
			yield delay(cycles(1))
			assert fixture.pin == False

		self.run(fixture, test)

	def test_pin_is_tristated_when_disabled(self, fixture):
		@instance
		def test():
			nonlocal fixture
			fixture.reset(False)
			fixture.disable()
			fixture.output(True)
			yield delay(cycles(1))
			assert fixture.pin is None

		self.run(fixture, test)

	def test_pin_is_tristated_when_reset(self, fixture):
		@instance
		def test():
			nonlocal fixture
			fixture.reset(True)
			fixture.enable()
			fixture.output(True)
			yield delay(cycles(1))
			assert fixture.pin is None

		self.run(fixture, test)
