import random
import pytest
from myhdl import *
from src.core.femtorv32_bus import Femtorv32Bus
from src.core.unaligned_write_monitor import UnalignedWriteMonitor

def cycles(n):
	return n

class UnalignedWriteMonitorFixture:
	def __init__(self):
		seed = random.getrandbits(32)
		print(f"Using non-determinism seed: {seed}")
		random.seed(seed)

		self._bus = Femtorv32Bus()
		self.__reset = ResetSignal(val=bool(0), active=bool(0), isasync=True)
		self._monitor = UnalignedWriteMonitor(self.__reset, self._bus)

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

	def set_wmask(self, wmask):
		self._bus.wmask.next = wmask

	def any_address_without_two_lsbs(self):
		return random.randint(self._bus.address.min, self._bus.address.max - 1) & ~3

class TestUnalignedWriteMonitor:
	@pytest.fixture(scope="function")
	def fixture(self):
		return UnalignedWriteMonitorFixture()

	def run(self, fixture, *generators):
		sim = Simulation(fixture.generators(), *generators)
		sim.run()

	def test_constructor_with_no_reset_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)reset signal must be specified"):
			UnalignedWriteMonitor(None, fixture.bus)

	def test_constructor_with_no_bus_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)bus must be specified"):
			UnalignedWriteMonitor(fixture._reset, None)

	scenarios = [
		(0, 0b0000, False),
		(1, 0b0000, False),
		(2, 0b0000, False),
		(3, 0b0000, False),

		(0, 0b0001, False),
		(1, 0b0001, False),
		(2, 0b0001, False),
		(3, 0b0001, False),

		(0, 0b0010, False),
		(1, 0b0010, False),
		(2, 0b0010, False),
		(3, 0b0010, True),

		(0, 0b0011, False),
		(1, 0b0011, False),
		(2, 0b0011, False),
		(3, 0b0011, True),

		(0, 0b0100, False),
		(1, 0b0100, False),
		(2, 0b0100, True),
		(3, 0b0100, True),

		(0, 0b0101, False),
		(1, 0b0101, False),
		(2, 0b0101, True),
		(3, 0b0101, True),

		(0, 0b0110, False),
		(1, 0b0110, False),
		(2, 0b0110, True),
		(3, 0b0110, True),

		(0, 0b0111, False),
		(1, 0b0111, False),
		(2, 0b0111, True),
		(3, 0b0111, True),

		(0, 0b1000, False),
		(1, 0b1000, True),
		(2, 0b1000, True),
		(3, 0b1000, True),

		(0, 0b1010, False),
		(1, 0b1010, True),
		(2, 0b1010, True),
		(3, 0b1010, True),

		(0, 0b1011, False),
		(1, 0b1011, True),
		(2, 0b1011, True),
		(3, 0b1011, True),

		(0, 0b1001, False),
		(1, 0b1001, True),
		(2, 0b1001, True),
		(3, 0b1001, True),

		(0, 0b1100, False),
		(1, 0b1100, True),
		(2, 0b1100, True),
		(3, 0b1100, True),

		(0, 0b1101, False),
		(1, 0b1101, True),
		(2, 0b1101, True),
		(3, 0b1101, True),

		(0, 0b1110, False),
		(1, 0b1110, True),
		(2, 0b1110, True),
		(3, 0b1110, True),

		(0, 0b1111, False),
		(1, 0b1111, True),
		(2, 0b1111, True),
		(3, 0b1111, True)
	]

	@pytest.mark.parametrize("address_lsbs,wmask,is_unaligned", scenarios)
	def test_unaligned_flag_against_all_scenarios_when_released_from_reset(self, fixture, address_lsbs, wmask, is_unaligned):
		@instance
		def test():
			nonlocal fixture
			fixture.release_from_reset()
			fixture.set_wmask(wmask)
			fixture.set_address(fixture.any_address_without_two_lsbs() | address_lsbs)
			yield delay(cycles(1))
			assert fixture.monitor.unaligned == is_unaligned

		self.run(fixture, test)

	@pytest.mark.parametrize("address_lsbs,wmask,_", scenarios)
	def test_unaligned_flag_is_always_false_when_held_in_reset(self, fixture, address_lsbs, wmask, _):
		@instance
		def test():
			nonlocal fixture
			fixture.hold_in_reset()
			fixture.set_wmask(wmask)
			fixture.set_address(fixture.any_address_without_two_lsbs() | address_lsbs)
			yield delay(cycles(1))
			assert not fixture.monitor.unaligned

		self.run(fixture, test)
