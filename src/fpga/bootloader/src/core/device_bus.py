from myhdl import *

class DeviceBus:
	def __init__(self, bus, base_address, num_addressable_words, num_address_bits=-1):
		if bus is None:
			raise TypeError("Bus must be specified")

		if num_addressable_words < 1:
			raise ValueError("Number of addressable words must be at least 1")

		if num_address_bits < 2:
			for i in range(2, len(bus.address) + 1):
				if 2**i >= num_addressable_words * 4:
					num_address_bits = i
					break

		self._address_space = 2**num_address_bits 
		self._num_addressable_bytes = num_addressable_words * 4
		if self._address_space < self._num_addressable_bytes:
			raise ValueError(
				f"Requested address space is not enough to cover addressable words; "
				f"num_addressable_words={num_addressable_words}, num_address_bits={num_address_bits}")

		if num_address_bits > len(bus.address):
			raise ValueError(
				f"Requested address space exceeds address bus width; "
				f"num_address_bits={num_address_bits}, address_bus_width={len(bus.address)}")

		if base_address < 0 or base_address + self._address_space > bus.address.max:
			raise ValueError(
				f"Base address must be in the range [0, {self._address_space - self._num_addressable_bytes}] for the requested address space; "
				f"base_address={base_address}, num_addressable_words={num_addressable_words}, num_address_bits={num_address_bits}")

		if base_address & (self._address_space - 1):
			raise ValueError(
				f"Base address must be aligned on a {hex(self._address_space)} boundary to enable the LSbs to be used as a relative address; "
				f"base_address={base_address}, num_addressable_words={num_addressable_words}, num_address_bits={num_address_bits}")

		self._bus = bus
		self._base_address = base_address
		self._address = self._bus.address(num_address_bits, 0)
		self._rstrb = Signal(bool(0))
		self._wmask = Signal(intbv(val=0, min=0, max=16))

	@block
	def generators(self):
		en = Signal(bool(0))

		@always_comb
		def decode_en():
			nonlocal self
			en.next = self._bus.address >= self._base_address and self._bus.address < self._base_address + self._num_addressable_bytes

		@always_comb
		def decode_rstrb():
			nonlocal en
			nonlocal self
			self._rstrb.next = en and self._bus.rstrb

		@always_comb
		def decode_wmask():
			nonlocal en
			nonlocal self
			self._wmask.next = self._bus.wmask if en else 0

		return decode_en, decode_rstrb, decode_wmask

	@property
	def address(self):
		return self._address

	@property
	def wdata(self):
		return self._bus.wdata

	@property
	def wmask(self):
		return self._wmask

	@property
	def wbusy(self):
		return self._bus.wbusy

	@property
	def rdata(self):
		return self._bus.rdata

	@property
	def rstrb(self):
		return self._rstrb

	@property
	def rbusy(self):
		return self._bus.rbusy
