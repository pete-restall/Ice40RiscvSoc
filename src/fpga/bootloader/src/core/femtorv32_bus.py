from myhdl import *

class Femtorv32Bus():
	def __init__(self):
		self._address = Signal(modbv(val=0, min=0, max=2**24))
		self._wdata = Signal(intbv(val=0, min=0, max=2**32))
		self._wmask = Signal(intbv(val=0, min=0, max=2**4))
		self._wbusy = Signal(val=bool(0))
		self._rdata = Signal(intbv(val=0, min=0, max=2**32))
		self._rstrb = Signal(val=bool(0))
		self._rbusy = Signal(val=bool(0))

	@property
	def address(self):
		return self._address

	@property
	def wdata(self):
		return self._wdata

	@property
	def wmask(self):
		return self._wmask

	@property
	def wbusy(self):
		return self._wbusy

	@property
	def rdata(self):
		return self._rdata

	@property
	def rstrb(self):
		return self._rstrb

	@property
	def rbusy(self):
		return self._rbusy
