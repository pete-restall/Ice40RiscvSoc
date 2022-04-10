from myhdl import *

class Counter:
	def __init__(self, clk, reset, min, max):
		if clk is None:
			raise TypeError("Clock must be specified")

		if reset is None:
			raise TypeError("Reset input must be specified; tie it inactive if it is not required")

		if max <= min:
			raise ValueError(f"Maximum must be greater than minimum; min={min}, max={max}")

		self._clk = clk
		self._reset = reset
		self._output = Signal(
			modbv(val=min, min=min, max=max + 1) if Counter.is_binary_counter(min, max)
			else intbv(val=min, min=min, max=max + 1))

	@staticmethod
	def is_binary_counter(min, max):
		return min == 0 and ((max + 1) & max) == 0

	def generators(self):
		@block
		def encapsulated(clk, reset, counter, is_counter_intbv):
			@always_seq(clk.posedge, reset)
			def counter_intbv_increment():
				nonlocal counter
				if counter == counter.max - 1:
					counter.next = counter.min
				else:
					counter.next = counter + 1

			@always_seq(clk.posedge, reset)
			def counter_modbv_increment():
				nonlocal counter
				counter.next = counter + 1

			return counter_intbv_increment if is_counter_intbv else counter_modbv_increment

		return encapsulated(self._clk, self._reset, self._output, isinstance(self._output.val, intbv))

	@property
	def output(self):
		return self._output
