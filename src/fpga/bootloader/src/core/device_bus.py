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
		self._address = Signal(modbv(val=0, min=0, max=2**num_address_bits))#self._bus.address(num_address_bits, 0)
		self._rstrb = Signal(bool(0))
		self._wmask = Signal(intbv(val=0, min=0, max=16))

	def generators(self):
		@block
		def encapsulated(bus_address, device_address, base_address, num_addressable_bytes, bus_rstrb, rstrb, bus_wmask, wmask):
			en = Signal(bool(0))

			@always_comb
			def address_slicer():
				nonlocal bus_address
				nonlocal device_address
				device_address.next = bus_address[len(device_address):]

			@always_comb
			def decode_en():
				nonlocal self
				nonlocal bus_address
				nonlocal base_address
				nonlocal num_addressable_bytes
				en.next = bus_address >= base_address and bus_address < base_address + num_addressable_bytes

			@always_comb
			def decode_rstrb():
				nonlocal en
				nonlocal rstrb
				nonlocal bus_rstrb
				rstrb.next = en and bus_rstrb

			@always_comb
			def decode_wmask():
				nonlocal en
				nonlocal wmask
				nonlocal bus_wmask
				wmask.next = bus_wmask if en else 0

			return address_slicer, decode_en, decode_rstrb, decode_wmask

		return encapsulated(self._bus.address, self._address, self._base_address, self._num_addressable_bytes, self._bus.rstrb, self._rstrb, self._bus.wmask, self._wmask)

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
