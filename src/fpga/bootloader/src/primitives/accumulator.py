from myhdl import *

_SIGNAL_CLASS = Signal(bool(0)).__class__

class Accumulator:
	def __init__(self, bus, width, addend):
		if bus is None:
			raise TypeError("Sequential Logic Control Bus (ie. clk, reset, en) must be specified")

		if width is None:
			raise TypeError("Accumulator width must be specified")

		if width < 1:
			raise ValueError("Accumulator width must be greater than zero")

		if addend is None:
			raise TypeError("Addend must be specified, either as a signal or a constant")

		is_signal_addend = isinstance(addend, _SIGNAL_CLASS)
		addend_max = (addend.max - 1) if is_signal_addend else addend
		if addend_max >= 2**width:
			raise ValueError(f"Addend is too wide for the accumulator; addendMax={addend}, accumulatorMax={2**width - 1}")

		if not is_signal_addend and addend == 0:
			raise ValueError(f"Constant addend must not be zero")

		self._bus = bus
		self._addend = addend
		self._output = Signal(modbv(val=0, min=0, max=2**width))
		self._overflow = Signal(bool(0))

	def generators(self):
		en_level = self._bus.is_en_active_high
		is_clk_posedge = self._bus.is_clk_posedge

		@block
		def encapsulated(clk, reset, en, acc, addend, overflow):
			clk.read = True
			reset.read = True
			en.read = True
			acc.driven = "reg"
			overflow.driven = "reg"
			if isinstance(addend, _SIGNAL_CLASS):
				addend.read = True

			@always(clk)
			def unused():
				pass

			nonlocal is_clk_posedge
			clk_edge = "posedge" if is_clk_posedge else "negedge"

			nonlocal en_level
			en_active = "$en == 0" if en_level == 0 else "$en != 0"

			reset_active = "$reset == 0" if reset.active == 0 else "$reset != 0"
			reset_edge = "negedge" if reset.active == 0 else "posedge"
			reset_sensitivity = f", {reset_edge} $reset" if reset.active != 0 and reset.isasync else ""

			encapsulated.verilog_code = \
f"""
always @({clk_edge} $clk{reset_sensitivity}) begin
	if ({reset_active}) begin
		$overflow <= 0;
		$acc <= 0;
	end else begin
		if ({en_active}) begin
			{{$overflow, $acc}} <= ($acc + $addend);
		end
	end
end
"""

			return unused

		return encapsulated(self._bus.clk, self._bus.reset, self._bus.en, self._output, self._addend, self._overflow)

	@property
	def output(self):
		return self._output

	@property
	def overflow(self):
		return self._overflow
