from myhdl import *

class ResetController:
	def __init__(self, clk, reset_in, reset_out, num_assertion_cycles):
		if clk is None:
			raise TypeError("Clock must be specified")

		if reset_in is None:
			raise TypeError("Reset input must be specified; tie it inactive if it is not required")

		if reset_out is None:
			raise TypeError("Reset output must be specified")

		if num_assertion_cycles < 1:
			raise ValueError("Number of assertion cycles must be at least 1")

		self._clk = clk
		self._reset_in = reset_in
		self._reset_out = reset_out
		self._num_assertion_cycles = num_assertion_cycles

	def generators(self):
		@block
		def encapsulated(clk, reset_in, reset_out, num_assertion_cycles):
			reset_counter = Signal(intbv(val=0, min=0, max=num_assertion_cycles))
			reset_edge = reset_in.posedge if reset_in.active == bool(1) else reset_in.negedge

			@always(clk.posedge, reset_edge)
			def reset_counter_increment():
				nonlocal reset_in
				nonlocal reset_counter
				if reset_in != reset_in.active:
					if reset_counter != (reset_counter.max - 1):
						reset_counter.next = reset_counter + 1
				else:
					reset_counter.next = 0

			@always(clk.posedge)
			def synchronous_reset_out():
				nonlocal reset_out
				nonlocal reset_counter
				reset_out.next = reset_out.active if reset_counter != (reset_counter.max - 1) else not reset_out.active

			return reset_counter_increment, synchronous_reset_out

		return encapsulated(self._clk, self._reset_in, self._reset_out, self._num_assertion_cycles)
