import sys
from myhdl import *
from src.core.femtorv32_processor import Femtorv32Processor
from src.core.femtorv32_bus import Femtorv32Bus

@block
def Main(
	clk_master,

	spi_sclk,
	spi_miso,
	spi_mosi,
	_spi_ss,

	flash_sclk,
	flash_io0,
	flash_io1,
	flash_io2,
	flash_io3,
	_flash_ss):

	_reset = ResetSignal(val=bool(1), active=bool(0), isasync=True) # TODO: should be tied to something
	irq = Signal(bool(0)) # TODO: should be tied to peripherals to enable IRQ

	# TODO - THESE SHOULD BE ENCAPSULATED FURTHER (INTO A 'Core' MODULE) BUT FOR NOW, USE FEMTORV32 DIRECTLY...
	core_bus = Femtorv32Bus()
	core = Femtorv32Processor(
		_reset,
		clk_master, # TODO: should be a divided clock
		core_bus,
		irq=irq,
		reset_address=0x000000)

	# TODO - only necessary so that the synthesis tool does not optimise everything away...
	xxx = int('0b001_00000000000_01', base=2) | (1 << 16) # c.jal 0; c.nop
	yyy = 1 | (1 << 16) # c.nop; c.nop

	@always(clk_master.posedge)
	def whatevs():
		nonlocal flash_io0
		nonlocal core_bus
		nonlocal _reset
		nonlocal irq
		_reset.next = 1
		irq.next = 0
		core_bus.rbusy.next = 0
		core_bus.wbusy.next = 0

		nonlocal xxx, yyy
		if core_bus.address == 0 and core_bus.rstrb:
			core_bus.rdata.next = xxx
		elif core_bus.rstrb:
			core_bus.rdata.next = yyy

		if core_bus.wmask:
			if core_bus.wdata & 1:
				flash_io0.next = 1
			else:
				flash_io0.next = 0

	@always_comb
	def outputs():
		nonlocal _spi_ss

		nonlocal spi_miso
		if _spi_ss:
			spi_miso.next = 0
		else:
			spi_miso.next = 0

		nonlocal flash_sclk
		flash_sclk.next = 0

		nonlocal _flash_ss
		_flash_ss.next = 1

	return core, outputs, whatevs # TODO: REMOVE 'outputs' WHEN MORE OF THE DESIGN IS COMPLETE...

class Bootloader:
	def __init__(self):
		clk_master = Signal(bool(0))

		spi_sclk = Signal(bool(0))
		spi_miso = Signal(bool(0))
		spi_mosi = Signal(bool(0))
		_spi_ss = Signal(bool(0))

		flash_sclk = Signal(bool(0))
		flash_io0 = Signal(bool(0))
		flash_io1 = Signal(bool(0))
		flash_io2 = Signal(bool(0))
		flash_io3 = Signal(bool(0))
		_flash_ss = Signal(bool(1))

		self.main = Main(
			clk_master,

			spi_sclk,
			spi_miso,
			spi_mosi,
			_spi_ss,

			flash_sclk,
			flash_io0,
			flash_io1,
			flash_io2,
			flash_io3,
			_flash_ss)

	def to_verilog(self, dirname):
		self.main.convert(
			hdl='Verilog',
			name=self.__class__.__name__,
			directory=dirname,
			testbench=False,
			initial_values=False)

if __name__ == '__main__':
	if len(sys.argv) != 2:
		print("Specify an output directory name for the generated Verilog artefact(s)")
		exit(1)

	Bootloader().to_verilog(sys.argv[1])
