from myhdl import *

class Register:
	def __init__(self, bus, input, reset_value=0):
		if bus is None:
			raise TypeError("Sequential Logic Control Bus (ie. clk, reset, en) must be specified")

		if input is None:
			raise TypeError("Input signal(s) must be specified")

		if reset_value is None:
			raise TypeError("Reset Value must not be None if it has been specified (defaults to 0 if unspecified)")

		self._bus = bus
		self._input = input
		self._output = Signal(
			modbv(val=reset_value, min=input.min, max=input.max) if isinstance(input.val, modbv)
			else intbv(val=reset_value, min=input.min, max=input.max))

	def generators(self):
		is_clk_posedge = self._bus.is_clk_posedge
		we_active_level = self._bus.is_en_active_high

		@block
		def encapsulated(clk, reset, we, d, q):
			nonlocal is_clk_posedge

			@always_seq(clk.posedge if is_clk_posedge else clk.negedge, reset)
			def latching():
				nonlocal we, we_active_level
				nonlocal d, q
				if we == we_active_level:
					q.next = d

			return latching

		return encapsulated(self._bus.clk, self._bus.reset, self._bus.en, self._input, self._output)

	@property
	def output(self):
		return self._output
