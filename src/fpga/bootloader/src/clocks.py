from myhdl import *
from src.primitives.counter import Counter

class Clocks:
	def __init__(self, clk_100MHz, reset):
		if clk_100MHz is None:
			raise TypeError("Master Clock (100MHz) must be specified")

		if reset is None:
			raise TypeError("Reset input must be specified; tie it inactive if it is not required")

		self._counter = Counter(clk_100MHz, reset, 0, 7)

	def generators(self):
		return self._counter.generators()

	@property
	def clk_50MHz(self):
		return self._counter.output(0)

	@property
	def clk_25MHz(self):
		return self._counter.output(1)

	@property
	def clk_12_5MHz(self):
		return self._counter.output(2)
