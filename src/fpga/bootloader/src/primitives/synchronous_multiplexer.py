from myhdl import *

_SIGNAL_TYPE = Signal(bool(0)).__class__

class SynchronousMultiplexer:
	def __init__(self, clk, index, inputs, default_index=0, negedge=False):
		if clk is None:
			raise TypeError("Clock must be specified")

		if index is None:
			raise TypeError("Index signal must be specified")

		if inputs is None:
			raise TypeError("Input signals must be specified")

		if not isinstance(inputs, list):
			raise TypeError("Inputs must be a list of signals")

		if len(inputs) < 2:
			raise TypeError("There must be at least two input signals")

		if index.max < len(inputs):
			raise ValueError(f"Too many input signals for index; maxIndex={index.max - 1}, numInputs={len(inputs)}")

		for input in inputs:
			if not isinstance(input, _SIGNAL_TYPE):
				raise TypeError("All inputs must be signals")

			[mux_type, mux_min, mux_max] = [type(inputs[0].val), inputs[0].min, inputs[0].max]
			if type(input.val) is not mux_type:
				raise TypeError("All inputs must be of the same type")

			if input.min != mux_min or input.max != mux_max:
				raise TypeError("All inputs must be the same width")

		if default_index is None:
			raise TypeError("Default index must be specified")

		if default_index < 0 or default_index >= len(inputs):
			raise ValueError(f"Default index must be in the range [0, {len(inputs) - 1}]")

		self._clk = clk
		self._is_clk_negedge = bool(negedge)
		self._index = index
		self._default_index = default_index
		self._inputs = inputs
		self._output = Signal(
			modbv(val=inputs[default_index].val, min=inputs[0].min, max=inputs[0].max) if isinstance(inputs[0].val, modbv)
			else intbv(val=inputs[default_index].val, min=inputs[0].min, max=inputs[0].max) if isinstance(inputs[0].val, intbv)
			else bool(inputs[default_index].val))

	def generators(self):
		default_sel = self._default_index
		num_signals = len(self._inputs)
		signal_width = len(self._inputs[0])
		edge = "negedge" if self._is_clk_negedge else "posedge"

		@block
		def encapsulated(clk, sel, d, q):
			sel.read = True
			d.read = True
			q.driven = "reg"

			@always(clk)
			def unused():
				pass

			def bit_range_for(index):
				return f"[{((index + 1) * signal_width) - 1}:{index * signal_width}]"

			nonlocal edge
			encapsulated.verilog_code = "always @(" + edge + " $clk) begin"
			encapsulated.verilog_code += \
"""
	case ($sel)
"""
			nonlocal num_signals, signal_width
			for i in range(0, num_signals):
				encapsulated.verilog_code += f"\t\t'd{i}: $q <= d{bit_range_for(i)};\n"

			nonlocal default_sel
			encapsulated.verilog_code += f"\t\tdefault: $q <= d{bit_range_for(default_sel)};\n"
			encapsulated.verilog_code += \
"""
	endcase
end
"""

			return unused

		return encapsulated(self._clk, self._index, ConcatSignal(*reversed(self._inputs)), self._output)

	@property
	def output(self):
		return self._output
