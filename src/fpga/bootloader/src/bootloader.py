import sys
from myhdl import *
from src.core.device_bus import DeviceBus
from src.core.femtorv32_processor import Femtorv32Processor
from src.core.femtorv32_bus import Femtorv32Bus
from src.core.inferred_memory_block import InferredMemoryBlock

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
	clk_core = clk_master # TODO: SHOULD BE A DIVIDED CLOCK
	core_bus = Femtorv32Bus()
	core = Femtorv32Processor(
		_reset,
		clk_core,
		core_bus,
		irq,
		reset_address=0x000000)

	@block
	def rom():
		nonlocal clk_core
		nonlocal core_bus
		rom_num_words = 12 * (4096 // 32)
		rom_bus = DeviceBus(core_bus, 0, rom_num_words - 4)
		rom = InferredMemoryBlock(
			clk_core,
			rom_bus,
			rom_num_words,
			is_rom=True,
			contents_filename='../out/firmware.mem')

		return rom.generators(), rom_bus.generators().subs # TODO: BIT OF A MESS - BECAUSE THE 'generators()' FUNCTION IS NOT MARKED @block; BUT IF IT'S MARKED @block THEN THE SIGNALS CANNOT BE INFERRED FOR THE CO-SIMULATION TESTS.  THE TESTS ARE FINE (SHOULDN'T MARK generator() WITH @block) SO NEED A BETTER STRUCTURE; NEEDS TO BE IN @block AS WELL TO ALLOW NAMESPACING, OTHERWISE THE NAMES WILL COLLIDE IN THE VERILOG...

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

		nonlocal spi_sclk
		spi_sclk.next = 0

		nonlocal flash_sclk
		flash_sclk.next = 0

		nonlocal _flash_ss
		_flash_ss.next = 1

	return core, rom(), outputs, whatevs # TODO: REMOVE 'outputs' WHEN MORE OF THE DESIGN IS COMPLETE...

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
