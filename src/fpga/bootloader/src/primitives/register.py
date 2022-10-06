from myhdl import *

class Register:
	def __init__(self, clk, reset, input, write_enable, reset_value=0, is_write_enable_active_high=True, negedge=False):
		if clk is None:
			raise TypeError("Clock must be specified")

		if reset is None:
			raise TypeError("Reset input must be specified; tie it inactive if it is not required")

		if input is None:
			raise TypeError("Input signal(s) must be specified")

		if write_enable is None:
			raise TypeError("Write Enable must be specified; tie it active if it is not required")

		if reset_value is None:
			raise TypeError("Reset Value must not be None if it has been specified (defaults to 0 if unspecified)")

		self._clk = clk
		self._clk_edge = clk.negedge if negedge else clk.posedge
		self._reset = reset
		self._write_enable = write_enable
		self._write_enable_level = is_write_enable_active_high
		self._input = input
		self._output = Signal(
			modbv(val=reset_value, min=input.min, max=input.max) if isinstance(input.val, modbv)
			else intbv(val=reset_value, min=input.min, max=input.max))

	def generators(self):
		we_active_level = self._write_enable_level

		@block
		def encapsulated(clk_edge, reset, we, d, q):
			@always_seq(clk_edge, reset)
			def latching():
				nonlocal we, we_active_level
				nonlocal d, q
				if we == we_active_level:
					q.next = d

			return latching

		return encapsulated(self._clk_edge, self._reset, self._write_enable, self._input, self._output)

	@property
	def output(self):
		return self._output
