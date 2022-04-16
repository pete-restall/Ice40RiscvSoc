from myhdl import *
from src.primitives.counter import Counter

class Clocks:
	def __init__(self, clk_100MHz, reset):
		if clk_100MHz is None:
			raise TypeError("Master Clock (100MHz) must be specified")

		if reset is None:
			raise TypeError("Reset input must be specified; tie it inactive if it is not required")

		self._clk_100MHz = clk_100MHz
		self._reset = reset
		self._counter = Counter(clk_100MHz, reset, 0, 7)
		self._counter_5 = Counter(clk_100MHz, reset, 0, 4)
		self._divide_by_5 = Signal(bool(0))

	def generators(self):
		@block
		def encapsulated(clk, reset, counter, counter_5, out):
			latched_negedge_2 = Signal(bool(0))

			@always_seq(clk.negedge, reset)
			def latch_negedge_2():
				nonlocal counter_5
				nonlocal latched_negedge_2
				latched_negedge_2.next = counter_5.output[1]

			@always_comb
			def divide_by_5():
				nonlocal counter_5
				nonlocal latched_negedge_2
				out.next = counter_5.output[1] | latched_negedge_2

			@block
			def namespaced(sub_block):
				return sub_block.generators().subs

			return namespaced(counter), namespaced(counter_5), latch_negedge_2, divide_by_5

		return encapsulated(self._clk_100MHz, self._reset, self._counter, self._counter_5, self._divide_by_5)

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
