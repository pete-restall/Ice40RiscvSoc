import uuid
from myhdl import *

#
# TODO: Inferring the register file uses a BRAM, which is 4Kib on the ICE40.  The register file itself is 1Kib.
# Add a two-bit 'register file bank selector' signal to allow, say, fast context switches during interrupts by
# shadowing the entire register file.  Software should then be able to write to the IO space to select a bank.
#
@block
def Femtorv32Processor(
	_reset,
	clk,
	bus,
	irq,
	reset_address=0x820000):

	@block
	def Femtorv32ProcessorExplicit(
		instance_name,
		clk,
		reset,
		interrupt_request,
		mem_addr,
		mem_wdata,
		mem_wmask,
		mem_rdata,
		mem_rstrb,
		mem_rbusy,
		mem_wbusy,
		addr_width,
		reset_addr):

		clk.read = True
		mem_addr.driven = "wire"
		mem_wdata.driven = "wire"
		mem_wmask.driven = "wire"
		mem_rdata.read = True
		mem_rstrb.driven = "wire"
		mem_rbusy.read = True
		mem_wbusy.read = True
		interrupt_request.read = True
		reset.read = True

		@always(
			clk,
			mem_addr,
			mem_wdata,
			mem_wmask,
			mem_rdata,
			mem_rstrb,
			mem_rbusy,
			mem_wbusy,
			interrupt_request,
			reset)
		def processor():
			pass

		return processor

	Femtorv32ProcessorExplicit.verilog_code = \
"""
wire[31:0] wide_mem_addr_bus_$instance_name;
assign $mem_addr = wide_mem_addr_bus_$instance_name[('h$addr_width - 1):0];
FemtoRV32 #(
	.ADDR_WIDTH ('h$addr_width),
	.RESET_ADDR ('h$reset_addr))
$instance_name (
	.clk ($clk),
	.mem_addr (wide_mem_addr_bus_$instance_name),
	.mem_wdata ($mem_wdata),
	.mem_wmask ($mem_wmask),
	.mem_rdata ($mem_rdata),
	.mem_rstrb ($mem_rstrb),
	.mem_rbusy ($mem_rbusy),
	.mem_wbusy ($mem_wbusy),
	.interrupt_request ($interrupt_request),
	.reset ($reset));
"""

	return Femtorv32ProcessorExplicit(
		instance_name='processor_' + uuid.uuid4().hex,
		clk=clk,
		reset=_reset,
		interrupt_request=irq,
		mem_addr=bus.address,
		mem_wdata=bus.wdata,
		mem_wmask=bus.wmask,
		mem_rdata=bus.rdata,
		mem_rstrb=bus.rstrb,
		mem_rbusy=bus.rbusy,
		mem_wbusy=bus.wbusy,
		addr_width=intbv(len(bus.address)),
		reset_addr=intbv(reset_address))
