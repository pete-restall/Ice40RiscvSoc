from myhdl import *

class InferredMemoryBlock:
	def __init__(self, clk, bus, num_words, contents_filename=None, is_rom=False):
		if is_rom and not contents_filename:
			raise ValueError("ROM needs to be initialised as part of the bitstream")

		self._cells = [Signal(intbv(min=0, max=2**32)) for _ in range(0, num_words)]
		self._cells[0].driven = 'reg'
		self._clk = clk
		self._bus = bus
		self._is_rom = is_rom
		self._contents_filename = contents_filename

	def generators(self):
		contents_filename = self._contents_filename

		@block
		def encapsulated(clk, bus_rstrb, bus_rdata, bus_wmask, bus_wdata, is_rom, cells, bus_address):
			word_address = Signal(modbv(min=0, max=2**(len(bus_address) - 2)))

			@always_comb
			def address_slicer():
				nonlocal bus_address
				nonlocal word_address
				word_address.next = bus_address[:2]

			@block
			def initial(cells):
				nonlocal clk

				@always(cells[0])
				def unused():
					pass

				return unused

			nonlocal contents_filename
			initial.verilog_code = 'initial begin\n\t$$readmemh("' + contents_filename + '", $cells);\nend\n'

			@always(clk.posedge)
			def read():
				nonlocal bus_rstrb
				nonlocal bus_rdata
				nonlocal cells
				nonlocal word_address
				if bus_rstrb:
					bus_rdata.next = cells[word_address] # The :2 doesn't work - still synthesises to :0

			@always(clk.posedge)
			def write():
				nonlocal bus_wmask
				nonlocal bus_wdata
				nonlocal cells
				nonlocal bus_address
				if bus_wmask & 1:
					cells[bus_address[:2]](8, 0).next = bus_wdata(8, 0)
				if bus_wmask & 2:
					cells[bus_address[:2]](16, 8).next = bus_wdata(16, 8)
				if bus_wmask & 4:
					cells[bus_address[:2]](24, 16).next = bus_wdata(24, 16)
				if bus_wmask & 8:
					cells[bus_address[:2]](32, 24).next = bus_wdata(32, 24)

			if is_rom:
				return initial(self._cells), read, address_slicer

			if contents_filename:
				return initial(self._cells), read, write, address_slicer

			return read, write, address_slicer

		return encapsulated(self._clk, self._bus.rstrb, self._bus.rdata, self._bus.wmask, self._bus.wdata, self._is_rom, self._cells, self._bus.address)

# TODO !
"""
We want this block to generate something like the following to infer EBR correctly.  The write with mask is important to infer the word size of the cells
(Synplify appears to ignore the reg declaration).  The cells must be reg.  The rdata must be reg (EBR registered output).

---

reg [31:0] rom0_InferredMemoryBlock0_generators0_encapsulated0_cells [0:2047];
wire [23:0] Femtorv32Processor0_Femtorv32ProcessorExplicit0_mem_addr;
reg [31:0] Femtorv32Processor0_Femtorv32ProcessorExplicit0_mem_rdata;
wire Femtorv32Processor0_Femtorv32ProcessorExplicit0_mem_rstrb;

wire[10:0] word_address = Femtorv32Processor0_Femtorv32ProcessorExplicit0_mem_addr[12:2];
always @(posedge clk_core) begin
	if (Femtorv32Processor0_Femtorv32ProcessorExplicit0_mem_rstrb) begin
		Femtorv32Processor0_Femtorv32ProcessorExplicit0_mem_rdata <= rom0_InferredMemoryBlock0_generators0_encapsulated0_cells[word_address];
	end

	if (core_bus_wmask) begin
		rom0_InferredMemoryBlock0_generators0_encapsulated0_cells[word_address] <= core_bus_wdata & {
			{8{core_bus_wmask[3]}},
			{8{core_bus_wmask[2]}},
			{8{core_bus_wmask[1]}},
			{8{core_bus_wmask[0]}}};
	end
end

---

A simple program for blinking the RGB LEDs:

.global _boot
.text

_boot:
	lui x8, 0x00400
	addi x8, x8, 0x004
	li x9, 1

ledFlash:
	lui x11, (10000 & ((1 << 20) - 1) << 12) >> 12

delay:
	nop
	addi x11, x11, -1
	bne x11, x0, delay

shift:
	slli x9, x9, 1
	andi x9, x9, 7
	bne x9, x0, write
	ori x9, x9, 1

write:
	xori x10, x9, 7
	sw x10, 0(x8)
	j ledFlash
"""
