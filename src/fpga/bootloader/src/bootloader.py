import sys
from myhdl import *
from src.clocks import Clocks
from src.core.device_bus import DeviceBus
from src.core.femtorv32_processor import Femtorv32Processor
from src.core.femtorv32_bus import Femtorv32Bus
from src.core.inferred_memory_block import InferredMemoryBlock
from src.reset.reset_controller import ResetController

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
	_flash_ss,

	_led_r,
	_led_g,
	_led_b):

	_reset = ResetSignal(val=bool(1), active=bool(0), isasync=True) # TODO: should be tied to something
	irq = Signal(bool(0)) # TODO: should be tied to peripherals to enable IRQ

	clk_100MHz = Signal(bool(0))

	@block
	def pll_for_ice_sugar(clk_master, _reset, clk_100MHz):
		@always(clk_master.posedge)
		def unused():
			pass

		clk_master.read = True
		clk_100MHz.driven = "wire"
		pll_for_ice_sugar.verilog_code = "wire unused; Pll100MHz pll_100MHz (.ref_clk_i($clk_master), .rst_n_i($_reset), .outcore_o(unused), .outglobal_o($clk_100MHz));"

		return unused

	clocks = Clocks(clk_100MHz, ResetSignal(val=bool(0), active=bool(1), isasync=True))

	@block
	def clocks_generator():
		return clocks.generators().subs

	# TODO - THESE SHOULD BE ENCAPSULATED FURTHER (INTO A 'Core' MODULE) BUT FOR NOW, USE FEMTORV32 DIRECTLY...
	clk_core = clocks.clk_20MHz
	core_bus = Femtorv32Bus()
	core = Femtorv32Processor(
		_reset,
		clk_core,
		core_bus,
		irq,
		reset_address=0x000000)

	reset_controller = ResetController(
		clk_core,
		reset_in=ResetSignal(val=bool(0), active=bool(1), isasync=True),
		reset_out=_reset,
		num_assertion_cycles=65536)


	# TODO: TEMPORARY BLOCK TO KEEP THE SYNTHESISER HAPPY WHILST STANDING UP THE APPLICATION - NEEDS TO BE BETTER THOUGHT OUT / PARTITIONED / TESTED
	@block
	def rom():
		nonlocal clk_core
		nonlocal core_bus
		rom_num_words = 16 * (4096 // 32)
		rom_bus = DeviceBus(core_bus, 0, rom_num_words - 4)
		rom = InferredMemoryBlock(
			clk_core,
			rom_bus,
			rom_num_words,
			is_rom=True,
			contents_filename="../out/firmware.mem")

		@block
		def rom_block():
			nonlocal rom
			return rom.generators().subs

		@block
		def rom_bus_block():
			nonlocal rom_bus
			return rom_bus.generators().subs

		return rom_block(), rom_bus_block()

	# TODO: TEMPORARY BLOCK TO KEEP THE SYNTHESISER HAPPY WHILST STANDING UP THE APPLICATION
	@always(clk_core.posedge)
	def whatevs():
		nonlocal flash_io0
		nonlocal core_bus
		nonlocal irq
		irq.next = 0
		core_bus.rbusy.next = 0
		core_bus.wbusy.next = 0

		if core_bus.wmask:
			if core_bus.wdata & 1:
				flash_io0.next = 1
				flash_io1.next = 1
				flash_io2.next = 1
				flash_io3.next = 1
			else:
				flash_io0.next = 0
				flash_io1.next = 1
				flash_io2.next = 1
				flash_io3.next = 1

	# TODO: TEMPORARY BLOCK TO KEEP THE SYNTHESISER HAPPY WHILST STANDING UP THE APPLICATION
	@block
	def leds(address, wmask, wdata):
		@always(clk_core.posedge)
		def led_writer():
			nonlocal _led_r
			nonlocal _led_g
			nonlocal _led_b
			nonlocal address
			nonlocal wmask
			nonlocal wdata
			if wmask != 0 and address == 0x400004:
				_led_r.next = wdata[0]
				_led_g.next = wdata[1]
				_led_b.next = wdata[2]

		return led_writer

	# TODO: TEMPORARY BLOCK TO KEEP THE SYNTHESISER HAPPY WHILST STANDING UP THE APPLICATION
	@always_comb
	def outputs():
		nonlocal _spi_ss

		nonlocal spi_miso
		if _spi_ss:
			spi_mosi.next = spi_miso
		else:
			spi_mosi.next = spi_miso

		nonlocal spi_sclk
		spi_sclk.next = 0

		nonlocal flash_sclk
		flash_sclk.next = 0

		nonlocal _flash_ss
		_flash_ss.next = 1

	return (
		clocks_generator(),
		core,
		rom(),
		reset_controller.generators().subs,
		pll_for_ice_sugar(clk_master, _reset, clk_100MHz),  # TODO: REMOVE WHEN ACTUAL HARDWARE AVAILABLE
		outputs, # TODO: REMOVE WHEN MORE OF THE DESIGN IS COMPLETE
		whatevs,  # TODO: REMOVE WHEN MORE OF THE DESIGN IS COMPLETE
		leds(core_bus.address, core_bus.wmask, core_bus.wdata))  # TODO: REMOVE WHEN MORE OF THE DESIGN IS COMPLETE

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

		_led_r = Signal(bool(1))
		_led_g = Signal(bool(1))
		_led_b = Signal(bool(1))

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
			_flash_ss,

			_led_r,
			_led_g,
			_led_b)

	def to_verilog(self, dirname):
		self.main.convert(
			hdl="Verilog",
			name=self.__class__.__name__,
			directory=dirname,
			testbench=False,
			initial_values=False)

if __name__ == "__main__":
	if len(sys.argv) != 2:
		print("Specify an output directory name for the generated Verilog artefact(s)")
		exit(1)

	Bootloader().to_verilog(sys.argv[1])
