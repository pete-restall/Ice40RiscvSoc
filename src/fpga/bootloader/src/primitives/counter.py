from myhdl import *

class Counter:
	def __init__(self, bus, min, max):
		if bus is None:
			raise TypeError("Sequential Logic Control Bus (ie. clk, reset, en) must be specified")

		if max <= min:
			raise ValueError(f"Maximum must be greater than minimum; min={min}, max={max}")

		self._bus = bus
		self._is_binary_counter = Counter.is_binary_counter(min, max)
		self._output = Signal(
			modbv(val=min, min=min, max=max + 1) if self._is_binary_counter
			else intbv(val=min, min=min, max=max + 1))

	@staticmethod
	def is_binary_counter(min, max):
		return min == 0 and ((max + 1) & max) == 0

	def generators(self):
		is_clk_posedge = self._bus.is_clk_posedge
		en_active_level = self._bus.is_en_active_high

		@block
		def encapsulated(clk, reset, en, counter, is_binary_counter):
			nonlocal is_clk_posedge

			@always_seq(clk.posedge if is_clk_posedge else clk.negedge, reset)
			def counter_intbv_increment():
				nonlocal counter
				nonlocal en, en_active_level
				if en == en_active_level:
					if counter == counter.max - 1:
						counter.next = counter.min
					else:
						counter.next = counter + 1

			@always_seq(clk.posedge if is_clk_posedge else clk.negedge, reset)
			def counter_modbv_increment():
				nonlocal counter
				nonlocal en, en_active_level
				if en == en_active_level:
					counter.next = counter + 1

			return counter_modbv_increment if is_binary_counter else counter_intbv_increment

		return encapsulated(self._bus.clk, self._bus.reset, self._bus.en, self._output, self._is_binary_counter)

	@property
	def output(self):
		return self._output
