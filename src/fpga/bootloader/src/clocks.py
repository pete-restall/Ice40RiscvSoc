from myhdl import *
from src.primitives.counter import Counter

class Clocks:
	def __init__(self, clk_100MHz, reset):
		if clk_100MHz is None:
			raise TypeError("Master Clock (100MHz) must be specified")

		if reset is None:
			raise TypeError("Reset signal must be specified; tie it inactive if it is not required")

		self._clk_100MHz = clk_100MHz
		self._reset = reset
		self._counter = Counter(clk_100MHz, reset, 0, 7)
		self._counter_5_pos = Counter(clk_100MHz, reset, 0, 4, negedge=False)
		self._counter_5_neg = Counter(clk_100MHz, reset, 0, 4, negedge=True)
		self._divide_by_5 = Signal(bool(0))

	def generators(self):
		@block
		def encapsulated(counter, counter_5_pos, counter_5_neg, div_5_with_50pc_duty):
			@always_comb
			def divide_by_5():
				nonlocal div_5_with_50pc_duty
				nonlocal counter_5_pos
				nonlocal counter_5_neg
				div_5_with_50pc_duty.next = counter_5_pos.output[1] | counter_5_neg.output[1]

			@block
			def namespaced(sub_block):
				return sub_block.generators().subs

			return namespaced(counter), namespaced(counter_5_pos), namespaced(counter_5_neg), divide_by_5

		return encapsulated(self._counter, self._counter_5_pos, self._counter_5_neg, self._divide_by_5)

	@property
	def clk_50MHz(self):
		return self._counter.output(0)

	@property
	def clk_25MHz(self):
		return self._counter.output(1)

	@property
	def clk_20MHz(self):
		return self._divide_by_5

	@property
	def clk_12_5MHz(self):
		return self._counter.output(2)
