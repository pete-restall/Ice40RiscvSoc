from myhdl import *

class SequentialLogicControlBus:
	def __init__(self, clk, reset, en, is_clk_posedge=True, is_en_active_high=True):
		if clk is None:
			raise TypeError("Clock must be specified")

		if reset is None:
			raise TypeError("Reset signal must be specified; tie it inactive if it is not required")

		if en is None:
			raise TypeError("Enable signal must be specified; tie it active if it is not required")

		if is_clk_posedge is None:
			raise TypeError("Active clock edge must be specified; the default is active high")

		if is_en_active_high is None:
			raise TypeError("Enable signal active level must be specified; the default is active high")

		self._clk = clk
		self._is_clk_posedge = bool(is_clk_posedge)
		self._reset = reset
		self._en = en
		self._is_en_active_high = bool(is_en_active_high)

	@property
	def clk(self):
		return self._clk

	@property
	def is_clk_posedge(self):
		return self._is_clk_posedge

	@property
	def reset(self):
		return self._reset

	@property
	def en(self):
		return self._en

	@property
	def is_en_active_high(self):
		return self._is_en_active_high

	def with_clk(self, clk):
		return SequentialLogicControlBus(clk, self._reset, self._en, self._is_clk_posedge, self._is_en_active_high)

	def with_reset(self, reset):
		return SequentialLogicControlBus(self._clk, reset, self._en, self._is_clk_posedge, self._is_en_active_high)

	def with_en(self, en):
		return SequentialLogicControlBus(self._clk, self._reset, en, self._is_clk_posedge, self._is_en_active_high)

	def with_inverted_clk(self):
		return SequentialLogicControlBus(self._clk, self._reset, self._en, not self._is_clk_posedge, self._is_en_active_high)

	def with_inverted_en(self):
		return SequentialLogicControlBus(self._clk, self._reset, self._en, self._is_clk_posedge, not self._is_en_active_high)
