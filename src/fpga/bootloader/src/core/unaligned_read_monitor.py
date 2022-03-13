from myhdl import *

class UnalignedReadMonitor():
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
				self._reset != self._reset.active and
				self._bus.rstrb and
				(self._bus.address[1] or self._bus.address[0]))

		return monitor
