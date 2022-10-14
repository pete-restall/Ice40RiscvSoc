from myhdl import *

_SIGNAL_CLASS = Signal(bool(0)).__class__

class Accumulator:
	def __init__(self, clk, reset, en, width, addend, is_en_active_high=True, negedge=False):
		if clk is None:
			raise TypeError("Clock must be specified")

		if reset is None:
			raise TypeError("Reset signal must be specified; tie it inactive if it is not required")

		if en is None:
			raise TypeError("Enable signal must be specified; tie it active if it is not required")

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

		self._clk = clk
		self._clk_edge = clk.negedge if negedge else clk.posedge
		self._reset = reset
		self._en = en
		self._en_level = is_en_active_high
		self._addend = addend
		self._output = Signal(modbv(val=0, min=0, max=2**width))

	def generators(self):
		en_level = self._en_level

		@block
		def encapsulated(clk_edge, reset, en, acc, addend):
			@always_seq(clk_edge, reset)
			def accumulate():
				nonlocal acc, addend
				nonlocal en, en_level
				if en == en_level:
					acc.next = acc + addend

			return accumulate

		return encapsulated(self._clk_edge, self._reset, self._en, self._output, self._addend)

	@property
	def output(self):
		return self._output
