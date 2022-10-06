import random
import pytest
from myhdl import *
from src.core.femtorv32_bus import Femtorv32Bus
from src.core.unaligned_read_monitor import UnalignedReadMonitor

class UnalignedReadMonitorFixture:
	def __init__(self):
		seed = random.getrandbits(32)
		print(f"Using non-determinism seed: {seed}")
		random.seed(seed)

		self._bus = Femtorv32Bus()
		self.__reset = ResetSignal(val=bool(0), active=bool(0), isasync=True)
		self._monitor = UnalignedReadMonitor(self.__reset, self._bus)

	@property
	def bus(self):
		return self._bus

	@property
	def _reset(self):
		return self.__reset

	@property
	def monitor(self):
		return self._monitor

	def generators(self):
		return self._monitor.generators()

	def hold_in_reset(self):
		self._reset.next = self._reset.active

	def release_from_reset(self):
		self._reset.next = not self._reset.active

	def set_address(self, address):
		self._bus.address.next = address

	def any_address_without_two_lsbs(self):
		return random.randint(self._bus.address.min, self._bus.address.max - 1) & ~3

	def strobe(self):
		yield self.strobe_rise()
		yield self.strobe_fall()

	def strobe_rise(self):
		self._bus.rstrb.next = 1
		yield delay(1)

	def strobe_fall(self):
		self._bus.rstrb.next = 0
		yield delay(1)

class TestUnalignedReadMonitor:
	@pytest.fixture(scope="function")
	def fixture(self):
		return UnalignedReadMonitorFixture()

	def run(self, fixture, *generators):
		sim = Simulation(fixture.generators(), *generators)
		sim.run()

	def test_constructor_with_no_reset_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)reset signal must be specified"):
			UnalignedReadMonitor(None, fixture.bus)

	def test_constructor_with_no_bus_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)bus must be specified"):
			UnalignedReadMonitor(fixture._reset, None)

	def test_unaligned_flag_is_false_after_strobe_rising_edge_when_both_address_lsbs_are_clear(self, fixture):
		@instance
		def test():
			nonlocal fixture
			fixture.release_from_reset()
			fixture.set_address(fixture.any_address_without_two_lsbs())
			yield fixture.strobe_rise()
			assert not fixture.monitor.unaligned

		self.run(fixture, test)

	def test_unaligned_flag_is_false_after_strobe_falling_edge_when_both_address_lsbs_are_clear(self, fixture):
		@instance
		def test():
			nonlocal fixture
			fixture.release_from_reset()
			fixture.set_address(fixture.any_address_without_two_lsbs())
			yield fixture.strobe()
			assert not fixture.monitor.unaligned

		self.run(fixture, test)

	def test_unaligned_flag_is_true_after_strobe_rising_edge_when_first_address_lsb_is_set(self, fixture):
		@instance
		def test():
			nonlocal fixture
			fixture.release_from_reset()
			fixture.set_address(fixture.any_address_without_two_lsbs() | 1)
			yield fixture.strobe_rise()
			assert fixture.monitor.unaligned

		self.run(fixture, test)

	def test_unaligned_flag_is_false_after_strobe_falling_edge_when_first_address_lsb_is_set(self, fixture):
		@instance
		def test():
			nonlocal fixture
			fixture.release_from_reset()
			fixture.set_address(fixture.any_address_without_two_lsbs() | 1)
			yield fixture.strobe()
			assert not fixture.monitor.unaligned

		self.run(fixture, test)

	def test_unaligned_flag_is_true_after_strobe_rising_edge_when_second_address_lsb_is_set(self, fixture):
		@instance
		def test():
			nonlocal fixture
			fixture.release_from_reset()
			fixture.set_address(fixture.any_address_without_two_lsbs() | 2)
			yield fixture.strobe_rise()
			assert fixture.monitor.unaligned

		self.run(fixture, test)

	def test_unaligned_flag_is_false_after_strobe_falling_edge_when_second_address_lsb_is_set(self, fixture):
		@instance
		def test():
			nonlocal fixture
			fixture.release_from_reset()
			fixture.set_address(fixture.any_address_without_two_lsbs() | 2)
			yield fixture.strobe()
			assert not fixture.monitor.unaligned

		self.run(fixture, test)

	def test_unaligned_flag_is_true_after_strobe_rising_edge_when_both_address_lsbs_are_set(self, fixture):
		@instance
		def test():
			nonlocal fixture
			fixture.release_from_reset()
			fixture.set_address(fixture.any_address_without_two_lsbs() | 3)
			yield fixture.strobe_rise()
			assert fixture.monitor.unaligned

		self.run(fixture, test)

	def test_unaligned_flag_is_false_after_strobe_falling_edge_when_both_address_lsbs_iares_set(self, fixture):
		@instance
		def test():
			nonlocal fixture
			fixture.release_from_reset()
			fixture.set_address(fixture.any_address_without_two_lsbs() | 3)
			yield fixture.strobe()
			assert not fixture.monitor.unaligned

		self.run(fixture, test)

	def test_unaligned_flag_is_false_after_strobe_rising_edge_when_first_address_lsb_is_set_but_monitor_held_in_reset(self, fixture):
		@instance
		def test():
			nonlocal fixture
			fixture.hold_in_reset()
			fixture.set_address(fixture.any_address_without_two_lsbs() | 1)
			yield fixture.strobe_rise()
			assert not fixture.monitor.unaligned

		self.run(fixture, test)

	def test_unaligned_flag_is_false_after_strobe_rising_edge_when_second_address_lsb_is_set_but_monitor_held_in_reset(self, fixture):
		@instance
		def test():
			nonlocal fixture
			fixture.hold_in_reset()
			fixture.set_address(fixture.any_address_without_two_lsbs() | 2)
			yield fixture.strobe_rise()
			assert not fixture.monitor.unaligned

		self.run(fixture, test)

	def test_unaligned_flag_is_false_after_strobe_rising_edge_when_both_address_lsbs_are_set_but_monitor_held_in_reset(self, fixture):
		@instance
		def test():
			nonlocal fixture
			fixture.hold_in_reset()
			fixture.set_address(fixture.any_address_without_two_lsbs() | 3)
			yield fixture.strobe_rise()
			assert not fixture.monitor.unaligned

		self.run(fixture, test)
