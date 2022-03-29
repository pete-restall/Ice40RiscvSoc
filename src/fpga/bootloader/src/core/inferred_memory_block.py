from myhdl import *

@block
def InferredMemoryBlock(clk, bus, num_words, is_rom=False, contents=None, fill_value=0):
	if is_rom and not contents:
		raise ValueError("ROM needs to be initialised as part of the bitstream")

	cells = []
	if contents is not None:
		cells = [Signal(intbv(val=int(value), min=0, max=2**32)) for value in contents]

	cells.extend([Signal(intbv(val=fill_value, min=0, max=2**32)) for _ in range(0, num_words - len(contents))])
	cells[0].driven = 'reg'
	word_address = bus.address(len(bus.address), 2)

	@always(clk.posedge)
	def read():
		nonlocal bus
		nonlocal cells
		if bus.rstrb:
			bus.rdata.next = cells[word_address.val]

	@always(clk.posedge)
	def write():
		nonlocal bus
		nonlocal cells
		if bus.wmask & 1:
			cells[word_address](8, 0).next = bus.wdata(8, 0)
		if bus.wmask & 2:
			cells[word_address](16, 8).next = bus.wdata(16, 8)
		if bus.wmask & 4:
			cells[word_address](24, 16).next = bus.wdata(24, 16)
		if bus.wmask & 8:
			cells[word_address](32, 24).next = bus.wdata(32, 24)

	if is_rom:
		return read

	return read, write
