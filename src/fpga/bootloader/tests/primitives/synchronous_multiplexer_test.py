import pytest
import random
from abc import ABC, abstractmethod
from myhdl import *
from src.primitives.synchronous_multiplexer import SynchronousMultiplexer
from tests.cosimulatable_dut import CosimulatableDut, cycles

class SynchronousMultiplexerFixture:
	def __init__(self, negedge):
		seed = random.getrandbits(32)
		print(f"Using non-determinism seed: {seed}")
		random.seed(seed)

		self._clk = Signal(negedge)
		self.index = Signal(intbv(val=0, min=0, max=4))
		self.inputs = [Signal(intbv(val=self.any_int_in_range(0, 2**32), min=0, max=2**32)) for _ in range(0, 4)]
		self.default_index = 0
		self.negedge = negedge
		self._multiplexer = None
		self._dut = None

	def any_int_in_range(self, min, half_open_max):
		return random.randint(min, half_open_max - 1)

	def any_input_value(self):
		return self.any_int_in_range(self.inputs[0].min, self.inputs[0].max)

	def any_input_value_except(self, excluded):
		val = self.any_int_in_range(self.inputs[0].min, self.inputs[0].max)
		return val if val != excluded else self.any_input_value_except(excluded)

	def any_index(self):
		return self.any_int_in_range(0, len(self.inputs))

	def any_index_except(self, excluded):
		val = self.any_int_in_range(self.index.min, self.index.max)
		return val if val != excluded else self.any_index_except(excluded)

	@property
	def clk(self):
		return self._clk

	@property
	def multiplexer(self):
		if self._multiplexer is None:
			self._multiplexer = SynchronousMultiplexer(
				self._clk,
				self.index,
				self.inputs,
				self.default_index,
				self.negedge)

		return self._multiplexer

	def ensure_initial_state(self):
		yield self.clock_pulse()

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
		yield delay(cycles(1))

	def clock_fall(self):
		self._clk.next = bool(0)
		yield delay(cycles(1))

	def clock_inactive(self):
		if self.negedge:
			yield self.clock_rise()
		else:
			yield self.clock_fall()

	@property
	def selected_input(self):
		return self.inputs[self.index.val]

	def generators(self):
		if self._dut is None:
			self._dut = CosimulatableDut(self.multiplexer.generators()).dut

		return self._dut

class SynchronousMultiplexerTestSuite(ABC):
	@pytest.fixture(scope="function")
	@abstractmethod
	def fixture(self):
		...

	def run(self, fixture, *generators):
		sim = Simulation(fixture.generators(), *generators)
		sim.run()

	def test_constructor_with_no_clk_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)clock must be specified"):
			SynchronousMultiplexer(None, fixture.index, fixture.inputs, fixture.default_index, fixture.negedge)

	def test_constructor_with_no_index_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)index signal must be specified"):
			SynchronousMultiplexer(fixture.clk, None, fixture.inputs, fixture.default_index, fixture.negedge)

	def test_constructor_with_no_inputs_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)signals must be specified"):
			SynchronousMultiplexer(fixture.clk, fixture.index, None, fixture.default_index, fixture.negedge)

	def test_constructor_with_empty_inputs_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)must be at least two input"):
			SynchronousMultiplexer(fixture.clk, fixture.index, [], fixture.default_index, fixture.negedge)

	def test_constructor_with_signal_as_inputs_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)must be a list"):
			SynchronousMultiplexer(fixture.clk, fixture.index, Signal(bool(0)), fixture.default_index, fixture.negedge)

	def test_constructor_with_single_signal_inputs_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)must be at least two input"):
			SynchronousMultiplexer(fixture.clk, fixture.index, [Signal(bool(0))], fixture.default_index, fixture.negedge)

	def test_constructor_with_non_signal_inputs_raises_type_error(self, fixture):
		invalid_inputs = [Signal(bool(0)) for _ in range(0, 10)] + [bool(0)]
		random.shuffle(invalid_inputs)
		fixture.index = Signal(intbv(val=0, min=0, max=len(invalid_inputs)))
		with pytest.raises(TypeError, match=r"(?i)all inputs must be signals"):
			SynchronousMultiplexer(fixture.clk, fixture.index, invalid_inputs, fixture.default_index, fixture.negedge)

	def test_constructor_with_mixture_of_intbv_and_modbv_signals_raises_type_error(self, fixture):
		invalid_inputs = [Signal(intbv(val=0, min=0, max=32)) for _ in range(0, 10)] + [Signal(modbv(32))]
		random.shuffle(invalid_inputs)
		fixture.index = Signal(intbv(val=0, min=0, max=len(invalid_inputs)))
		with pytest.raises(TypeError, match=r"(?i)must be of the same type"):
			SynchronousMultiplexer(fixture.clk, fixture.index, invalid_inputs, fixture.default_index, fixture.negedge)

	def test_constructor_with_mixture_of_intbv_and_bool_signals_raises_type_error(self, fixture):
		invalid_inputs = [Signal(intbv(val=0, min=0, max=2)) for _ in range(0, 10)] + [Signal(bool(0))]
		random.shuffle(invalid_inputs)
		fixture.index = Signal(intbv(val=0, min=0, max=len(invalid_inputs)))
		with pytest.raises(TypeError, match=r"(?i)must be of the same type"):
			SynchronousMultiplexer(fixture.clk, fixture.index, invalid_inputs, fixture.default_index, fixture.negedge)

	def test_constructor_with_mixture_of_modbv_and_bool_signals_raises_type_error(self, fixture):
		invalid_inputs = [Signal(modbv(val=0, min=0, max=2)) for _ in range(0, 10)] + [Signal(bool(0))]
		random.shuffle(invalid_inputs)
		fixture.index = Signal(intbv(val=0, min=0, max=len(invalid_inputs)))
		with pytest.raises(TypeError, match=r"(?i)must be of the same type"):
			SynchronousMultiplexer(fixture.clk, fixture.index, invalid_inputs, fixture.default_index, fixture.negedge)

	@pytest.mark.parametrize("input_width", [1, 2, 3, 64])
	def test_constructor_with_different_width_intbv_signals_raises_type_error(self, fixture, input_width):
		invalid_inputs = [Signal(intbv(val=0, min=0, max=2**input_width)) for _ in range(0, 10)] + [Signal(intbv(input_width + 1))]
		random.shuffle(invalid_inputs)
		fixture.index = Signal(intbv(val=0, min=0, max=len(invalid_inputs)))
		with pytest.raises(TypeError, match=r"(?i)must be the same width"):
			SynchronousMultiplexer(fixture.clk, fixture.index, invalid_inputs, fixture.default_index, fixture.negedge)

	@pytest.mark.parametrize("input_width", [1, 2, 3, 64])
	def test_constructor_with_different_width_modbv_signals_raises_type_error(self, fixture, input_width):
		invalid_inputs = [Signal(modbv(val=0, min=0, max=2**input_width)) for _ in range(0, 10)] + [Signal(modbv(input_width + 1))]
		random.shuffle(invalid_inputs)
		fixture.index = Signal(intbv(val=0, min=0, max=len(invalid_inputs)))
		with pytest.raises(TypeError, match=r"(?i)must be the same width"):
			SynchronousMultiplexer(fixture.clk, fixture.index, invalid_inputs, fixture.default_index, fixture.negedge)

	@pytest.mark.parametrize("num_indexable,num_inputs", [
		[2, 3],
		[2, 4],
		[3, 4],
		[100, 107]])
	def test_constructor_with_more_inputs_than_can_be_indexed_raises_value_error(self, fixture, num_indexable, num_inputs):
		too_many_inputs = [Signal(intbv(val=0, min=0, max=12)) for _ in range(0, num_inputs)]
		too_small_index = Signal(intbv(val=0, min=0, max=num_indexable))
		with pytest.raises(ValueError, match=r"(?i)too many input signals"):
			SynchronousMultiplexer(fixture.clk, too_small_index, too_many_inputs, fixture.default_index, fixture.negedge)

	def test_constructor_with_no_default_index_raises_type_error(self, fixture):
		with pytest.raises(TypeError, match=r"(?i)default index must be specified"):
			SynchronousMultiplexer(fixture.clk, fixture.index, fixture.inputs, None, fixture.negedge)

	@pytest.mark.parametrize("default_index", [-1, -2, -321])
	def test_constructor_when_default_index_is_less_than_zero_raises_value_error(self, fixture, default_index):
		fixture.default_index = default_index
		with pytest.raises(ValueError, match=r"(?i)default index must be in the range \[0, " + str(len(fixture.inputs) - 1) + "\\]"):
			SynchronousMultiplexer(fixture.clk, fixture.index, fixture.inputs, fixture.default_index, fixture.negedge)

	@pytest.mark.parametrize("default_index_oob", [1, 2, 321])
	def test_constructor_when_default_index_is_greater_than_number_of_inputs_raises_value_error(self, fixture, default_index_oob):
		fixture.inputs = [Signal(bool(0)) for _ in range(0, random.randint(2, 10))]
		fixture.index = Signal(intbv(val=0, min=0, max=len(fixture.inputs)))
		fixture.default_index = len(fixture.inputs) - 1 + default_index_oob
		with pytest.raises(ValueError, match=r"(?i)default index must be in the range \[0, " + str(len(fixture.inputs) - 1) + "\\]"):
			SynchronousMultiplexer(fixture.clk, fixture.index, fixture.inputs, fixture.default_index, fixture.negedge)

	@pytest.mark.parametrize("default_index", [0, 2])
	def test_constructor_when_default_index_is_at_boundary_then_no_value_error_is_raised(self, fixture, default_index):
		if default_index != 0:
			fixture.inputs = [Signal(bool(0)) for _ in range(0, default_index + 1)]

		fixture.default_index = default_index
		assert SynchronousMultiplexer(fixture.clk, fixture.index, fixture.inputs, fixture.default_index, fixture.negedge) is not None

	def test_output_is_intbv_signal_when_inputs_are_intbv(self, fixture):
		expected = Signal(intbv(val=123))
		fixture.inputs = [Signal(intbv(val=456)) for _ in range(0, 2)]
		fixture.index = Signal(intbv(val=0, min=0, max=len(fixture.inputs)))
		assert isinstance(fixture.multiplexer.output, expected.__class__)
		assert isinstance(fixture.multiplexer.output.val, expected.val.__class__)

	@pytest.mark.parametrize("input_width", [1, 2, 3, 64])
	def test_output_is_intbv_signal_of_same_width_when_inputs_are_intbv(self, fixture, input_width):
		expected = Signal(intbv(val=0, min=0, max=2**input_width))
		fixture.inputs = [Signal(intbv(val=0, min=0, max=2**input_width)) for _ in range(0, 2)]
		fixture.index = Signal(intbv(val=0, min=0, max=len(fixture.inputs)))
		assert fixture.multiplexer.output.min == expected.min and fixture.multiplexer.output.max == expected.max

	def test_output_is_initially_value_of_default_index_when_inputs_are_intbv(self, fixture):
		fixture.inputs = [Signal(intbv(val=fixture.any_int_in_range(0, 2**16), min=0, max=2**16)) for _ in range(0, 10)]
		fixture.index = Signal(intbv(val=0, min=0, max=len(fixture.inputs)))
		fixture.default_index = fixture.any_index()
		assert fixture.multiplexer.output.val == fixture.inputs[fixture.default_index].val

	def test_output_is_modbv_signal_when_inputs_are_modbv(self, fixture):
		expected = Signal(modbv(val=123))
		fixture.inputs = [Signal(modbv(val=456)) for _ in range(0, 2)]
		fixture.index = Signal(intbv(val=0, min=0, max=len(fixture.inputs)))
		assert isinstance(fixture.multiplexer.output, expected.__class__)
		assert isinstance(fixture.multiplexer.output.val, expected.val.__class__)

	@pytest.mark.parametrize("input_width", [1, 2, 3, 64])
	def test_output_is_modbv_signal_of_same_width_when_inputs_are_modbv(self, fixture, input_width):
		expected = Signal(modbv(val=0, min=0, max=2**input_width))
		fixture.inputs = [Signal(modbv(val=0, min=0, max=2**input_width)) for _ in range(0, 2)]
		fixture.index = Signal(intbv(val=0, min=0, max=len(fixture.inputs)))
		assert fixture.multiplexer.output.min == expected.min and fixture.multiplexer.output.max == expected.max

	def test_output_is_initially_value_of_default_index_when_inputs_are_modbv(self, fixture):
		fixture.inputs = [Signal(modbv(val=fixture.any_int_in_range(0, 2**16), min=0, max=2**16)) for _ in range(0, 10)]
		fixture.index = Signal(intbv(val=0, min=0, max=len(fixture.inputs)))
		fixture.default_index = fixture.any_index()
		assert fixture.multiplexer.output.val == fixture.inputs[fixture.default_index].val

	def test_output_is_bool_signal_when_inputs_are_bool(self, fixture):
		expected = Signal(bool(1))
		fixture.inputs = [Signal(bool(0)) for _ in range(0, 2)]
		fixture.index = Signal(intbv(val=0, min=0, max=len(fixture.inputs)))
		assert isinstance(fixture.multiplexer.output, expected.__class__)
		assert isinstance(fixture.multiplexer.output.val, expected.val.__class__)

	@pytest.mark.parametrize("value", [True, False])
	def test_output_is_initially_value_of_default_index_when_inputs_are_bool(self, fixture, value):
		fixture.index = Signal(intbv(val=0, min=0, max=10))
		fixture.default_index = fixture.any_index()
		fixture.inputs = [Signal(bool(value) if i == fixture.default_index else bool(not value)) for i in range(0, 10)]
		assert fixture.multiplexer.output.val == value

	def test_output_is_not_same_instance_as_any_input(self, fixture):
		for input in fixture.inputs:
			assert fixture.multiplexer.output is not input

	def test_output_is_updated_on_active_clock_edge_when_signal_changes_but_index_is_unchanged(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			fixture.selected_input.next = fixture.any_input_value_except(fixture.selected_input.val)
			yield delay(cycles(1))
			yield fixture.clock_active()
			assert fixture.multiplexer.output.val == fixture.selected_input.val

		self.run(fixture, test)

	def test_output_is_updated_to_new_indexed_signal_on_active_clock_edge_when_index_is_changed(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			for input in fixture.inputs:
				input.next = fixture.any_input_value()

			fixture.index.next = fixture.any_index_except(fixture.index.val)
			yield delay(cycles(1))
			yield fixture.clock_active()
			assert fixture.multiplexer.output.val == fixture.selected_input.val

		self.run(fixture, test)

	def test_output_is_not_updated_when_signal_changes_without_active_clock_edge(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			value_before_change = int(fixture.multiplexer.output.val)
			fixture.selected_input.next = fixture.any_input_value_except(fixture.selected_input.val)
			yield delay(cycles(1))
			assert fixture.multiplexer.output.val == value_before_change

		self.run(fixture, test)

	def test_output_is_not_updated_when_index_changes_without_active_clock_edge(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			value_before_change = int(fixture.multiplexer.output.val)
			fixture.selected_input.next = fixture.any_input_value_except(fixture.selected_input.val)
			fixture.index.next = fixture.any_index_except(fixture.index.val)
			yield delay(cycles(1))
			assert fixture.multiplexer.output.val == value_before_change

		self.run(fixture, test)

	def test_output_is_not_updated_when_signal_changes_on_inactive_clock_edge(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			value_before_change = int(fixture.multiplexer.output)
			fixture.selected_input.next = fixture.any_input_value_except(fixture.selected_input.val)
			yield delay(cycles(1))
			yield fixture.clock_inactive()
			assert fixture.multiplexer.output.val == value_before_change

		self.run(fixture, test)

	def test_output_is_not_updated_when_index_changes_on_inactive_clock_edge(self, fixture):
		@instance
		def test():
			nonlocal fixture
			yield fixture.ensure_initial_state()
			value_before_changes = int(fixture.multiplexer.output.val)
			fixture.selected_input.next = fixture.any_input_value_except(fixture.selected_input.val)
			fixture.index.next = fixture.any_index_except(fixture.index.val)
			yield delay(cycles(1))
			yield fixture.clock_inactive()
			assert fixture.multiplexer.output.val == value_before_changes

		self.run(fixture, test)

	@pytest.mark.parametrize("too_high", [1, 2, 3, 11])
	def test_output_is_value_of_default_signal_when_index_is_out_of_range(self, fixture, too_high):
		default_signal_value = fixture.any_int_in_range(1, 128)
		fixture.default_index = fixture.any_int_in_range(0, 4)
		fixture.index = Signal(intbv(val=(fixture.default_index + 1) & 3, min=0, max=4 + too_high))
		fixture.inputs = [Signal(intbv(val=0 if i != fixture.default_index else default_signal_value, min=0, max=128)) for i in range(0, 4)]
		out_of_range = len(fixture.inputs) - 1 + too_high

		@instance
		def test():
			nonlocal fixture
			nonlocal out_of_range
			nonlocal default_signal_value
			yield fixture.ensure_initial_state()
			fixture.index.next = out_of_range
			yield delay(cycles(1))
			yield fixture.clock_active()
			assert int(fixture.multiplexer.output.val) == default_signal_value

		self.run(fixture, test)

class TestSynchronousMultiplexerForPositiveEdges(SynchronousMultiplexerTestSuite):
	@pytest.fixture(scope="function")
	def fixture(self):
		return SynchronousMultiplexerFixture(negedge=False)

class TestSynchronousMultiplexerForNegativeEdges(SynchronousMultiplexerTestSuite):
	@pytest.fixture(scope="function")
	def fixture(self):
		return SynchronousMultiplexerFixture(negedge=True)
