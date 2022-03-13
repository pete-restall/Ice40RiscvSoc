from myhdl import *

class UnalignedWriteMonitor():
	def __init__(self, reset, bus):
		if reset is None:
			raise TypeError("Reset signal must be specified")

		if bus is None:
			raise TypeError("Bus must be specified")

		self._reset = reset
		self._bus = bus
		self._unaligned = Signal(val=bool(0))

	@property
	def unaligned(self):
		return self._unaligned

	@block
	def generators(self):
		@always_comb
		def monitor():
			self._unaligned.next = (
				self._reset != self._reset.active and (
					(self._bus.wmask[3] and (self._bus.address[1] or self._bus.address[0])) or
					(self._bus.wmask[2] and self._bus.address[1]) or
					(self._bus.wmask[1] and self._bus.address[1] and self._bus.address[0])))

		return monitor
