import pytest
import random
from myhdl import *
from src.primitives.sequential_logic_control_bus import SequentialLogicControlBus

class SequentialLogicControlBusFixture:
	def __init__(self):
		seed = random.getrandbits(32)
		print(f"Using non-determinism seed: {seed}")
		random.seed(seed)

		self.clk = self.dummy_clk()
		self.is_clk_posedge = True
		self.reset = self.dummy_reset()
		self.en = self.dummy_en()
		self.is_en_active_high = True
		self._dut = None

	def dummy_clk(self):
		return Signal(bool(0))

	def dummy_reset(self):
		return ResetSignal(val=bool(0), active=bool(0), isasync=False)

	def dummy_en(self):
		return Signal(bool(0))

	@property
	def bus(self):
		if self._bus is None:
			self._bus = SequentialLogicControlBus(
				self.clk,
				self.reset,
				self.en,
				self.is_clk_posedge,
				self.is_en_active_high)

		return self._bus

class TestSequentialLogicControlBus:
	@pytest.fixture(scope="function")
	def fixture(self):
		return SequentialLogicControlBusFixture()

	def test_constructor_with_no_clk_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)clock must be specified"):
			SequentialLogicControlBus(None, fixture.reset, fixture.en, fixture.is_clk_posedge, fixture.is_en_active_high)

	def test_clk_is_same_instance_passed_to_constructor(self, fixture):
		assert fixture.bus.clk is fixture.clk

	def test_constructor_with_no_reset_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)reset signal must be specified"):
			SequentialLogicControlBus(fixture.clk, None, fixture.en, fixture.is_clk_posedge, fixture.is_en_active_high)

	def test_reset_is_same_instance_passed_to_constructor(self, fixture):
		assert fixture.bus.reset is fixture.reset

	def test_constructor_with_no_enable_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)enable signal must be specified"):
			SequentialLogicControlBus(fixture.clk, fixture.reset, None, fixture.is_clk_posedge, fixture.is_en_active_high)

	def test_en_is_same_instance_passed_to_constructor(self, fixture):
		assert fixture.bus.en is fixture.en

	def test_constructor_with_no_is_clk_posedge_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)active clock edge must be specified"):
			SequentialLogicControlBus(fixture.clk, fixture.reset, fixture.en, None, fixture.is_en_active_high)

	def test_default_is_clk_posedge_is_true(self, fixture):
		bus = SequentialLogicControlBus(fixture.clk, fixture.reset, fixture.en)
		assert bus.is_clk_posedge

	@pytest.mark.parametrize("is_clk_posedge", [True, False])
	def test_is_clk_posedge_is_same_value_as_passed_to_constructor(self, fixture, is_clk_posedge):
		fixture.is_clk_posedge = is_clk_posedge
		assert fixture.bus.is_clk_posedge == is_clk_posedge

	def test_constructor_with_no_is_en_active_high_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)enable signal active level must be specified"):
			SequentialLogicControlBus(fixture.clk, fixture.reset, fixture.en, fixture.is_clk_posedge, None)

	def test_default_is_en_active_high_is_true(self, fixture):
		bus = SequentialLogicControlBus(fixture.clk, fixture.reset, fixture.en)
		assert bus.is_en_active_high

	@pytest.mark.parametrize("is_en_active_high", [True, False])
	def test_is_en_active_high_is_same_value_as_passed_to_constructor(self, fixture, is_en_active_high):
		fixture.is_en_active_high = is_en_active_high
		assert fixture.bus.is_en_active_high == is_en_active_high

	def test_with_clk_given_no_clk_raises_type_error(self, fixture):
		with pytest.raises(TypeError):
			fixture.bus.with_clk(None)

	def test_with_clk_is_not_same_instance(self, fixture):
		assert fixture.bus.with_clk(fixture.dummy_clk()) is not fixture.bus

	def test_with_clk_is_new_instance_with_given_clk(self, fixture):
		new_clk = Signal(bool(0))
		assert fixture.bus.with_clk(new_clk).clk is new_clk

	def test_with_clk_is_new_instance_with_same_non_clk_properties(self, fixture):
		new_bus = fixture.bus.with_clk(Signal(bool(0)))
		self._assert_same_except(fixture.bus, new_bus, new_bus.clk)

	def _assert_same_except(self, old_bus, new_bus, excluded):
		assert old_bus.clk is new_bus.clk if new_bus.clk is not excluded else True
		assert old_bus.reset is new_bus.reset if new_bus.reset is not excluded else True
		assert old_bus.en is new_bus.en if new_bus.en is not excluded else True
		assert old_bus.is_clk_posedge == new_bus.is_clk_posedge if excluded != 'is_clk_posedge' else True
		assert old_bus.is_en_active_high == new_bus.is_en_active_high if excluded != 'is_en_active_high' else True

	def test_with_reset_given_no_reset_raises_type_error(self, fixture):
		with pytest.raises(TypeError):
			fixture.bus.with_reset(None)

	def test_with_reset_is_not_same_instance(self, fixture):
		assert fixture.bus.with_reset(fixture.dummy_reset()) is not fixture.bus

	def test_with_reset_is_new_instance_with_given_reset(self, fixture):
		new_reset = ResetSignal(val=bool(0), active=bool(1), isasync=True)
		assert fixture.bus.with_reset(new_reset).reset is new_reset

	def test_with_reset_is_new_instance_with_same_non_reset_properties(self, fixture):
		new_bus = fixture.bus.with_reset(Signal(bool(0)))
		self._assert_same_except(fixture.bus, new_bus, new_bus.reset)

	def test_with_en_given_no_en_raises_type_error(self, fixture):
		with pytest.raises(TypeError):
			fixture.bus.with_en(None)

	def test_with_en_is_not_same_instance(self, fixture):
		assert fixture.bus.with_en(fixture.dummy_en()) is not fixture.bus

	def test_with_en_is_new_instance_with_given_en(self, fixture):
		new_en = Signal(bool(0))
		assert fixture.bus.with_en(new_en).en is new_en

	def test_with_en_is_new_instance_with_same_non_en_properties(self, fixture):
		new_bus = fixture.bus.with_en(Signal(bool(0)))
		self._assert_same_except(fixture.bus, new_bus, new_bus.en)

	def test_with_inverted_clk_is_not_same_instance(self, fixture):
		assert fixture.bus.with_inverted_clk() is not fixture.bus

	@pytest.mark.parametrize("is_clk_posedge", [False, True])
	def test_with_inverted_clk_is_new_instance_with_inverted_is_clk_posedge(self, fixture, is_clk_posedge):
		fixture.is_clk_posedge = is_clk_posedge
		assert fixture.bus.with_inverted_clk().is_clk_posedge != is_clk_posedge

	def test_with_inverted_clk_is_new_instance_with_same_non_is_clk_posedge_properties(self, fixture):
		new_bus = fixture.bus.with_inverted_clk()
		self._assert_same_except(fixture.bus, new_bus, 'is_clk_posedge')

	def test_with_inverted_en_is_not_same_instance(self, fixture):
		assert fixture.bus.with_inverted_en() is not fixture.bus

	@pytest.mark.parametrize("is_en_active_high", [False, True])
	def test_with_inverted_en_is_new_instance_with_inverted_is_en_active_high(self, fixture, is_en_active_high):
		fixture.is_en_active_high = is_en_active_high
		assert fixture.bus.with_inverted_en().is_en_active_high != is_en_active_high

	def test_with_inverted_en_is_new_instance_with_same_non_is_en_active_high_properties(self, fixture):
		new_bus = fixture.bus.with_inverted_en()
		self._assert_same_except(fixture.bus, new_bus, 'is_en_active_high')
