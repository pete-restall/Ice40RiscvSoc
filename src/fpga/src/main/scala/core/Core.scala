package net.restall.ice40riscvsoc.core

import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneAdapter, WishboneConfig}
import spinal.lib.{Counter, slave, StreamArbiterFactory}
import vexriscv._
import vexriscv.plugin._

import net.restall.ice40riscvsoc.bus._
import net.restall.ice40riscvsoc.bus.wishbone._
import net.restall.ice40riscvsoc.multiplexing._
import net.restall.ice40riscvsoc.SeqPairingExtensions._
import net.restall.ice40riscvsoc.vendor.lattice.ice40._

class Core extends Component {
	val io = new Core.IoBundle()
	noIoPrefix()

	private val cpu = new Cpu(resetVector=0x00000000, mtvecInit=0x00000201, yamlOutFilename=Some("src/radiant/cpu.yaml"))

	private val ledDevice = new Component {
		val io = new Bundle {
			val wishbone = slave(new Wishbone(new WishboneConfig(addressWidth=1, dataWidth=32, selWidth=4)))
			val p23 = out Bool()
			val ledR = out Bool()
			val ledG = out Bool()
			val ledB = out Bool()
		}

		private val regP23 = Reg(Bool()) init(False)
		io.p23 := regP23

		private val regLedR = Reg(Bool()) init(False)
		io.ledR := regLedR

		private val regLedG = Reg(Bool()) init(False)
		io.ledG := regLedG

		private val regLedB = Reg(Bool()) init(False)
		io.ledB := regLedB

		io.wishbone.DAT_MISO := 0

		when(io.wishbone.CYC && io.wishbone.WE && io.wishbone.STB) {
			io.wishbone.ACK := True
			regP23 := !regP23
			regLedR := !io.wishbone.DAT_MOSI(0)
			regLedG := !io.wishbone.DAT_MOSI(1)
			regLedB := !io.wishbone.DAT_MOSI(2)
		} otherwise {
			io.wishbone.ACK := False
		}
	}

	io.p23 := ledDevice.io.p23
	io.ledR := ledDevice.io.ledR
	io.ledG := ledDevice.io.ledG
	io.ledB := ledDevice.io.ledB


/*
	// Simple flasher:
	private val instructions = Seq(
		0x00100493,
		0x00010937,
		0x00992023,
		0x0014c493,
		0xff9ff06f)
*/

/*
	// RGB flasher:
	private val instructions = Seq(
		0x00100493,
		0x00200913,
		0x00400993,
		0x00010a37,
		0x009a2023,
		0x012a2023,
		0x013a2023,
		0xff5ff06f)
*/

/*
	// RGB chaser:
	private val instructions = Seq(
		0x00400493,
		0x00149913,
		0x000109b7,
		0x00031a37,
		0xd40a0a1b,
		0x00149493,
		0x01249463,
		0x00100493,
		0x0099a023,
		0x000a0ab3,
		0xfffa8a93,
		0xfe0a9ee3,
		0xfe5ff06f)
*/

	// RGB chaser in another bank:
	private val instructions = Seq(
		0x10000913,
		0x004004b7,
		0x49348493,
		0x00992023,
		0x0014a4b7,
		0x91348493,
		0x00992223,
		0x000114b7,
		0x9b748493,
		0x00992423,
		0x000324b7,
		0xa3748493,
		0x00992623,
		0x000d44b7,
		0x0a148493,
		0x00c49493,
		0xa1b48493,
		0x00992823,
		0x001494b7,
		0x49348493,
		0x00992a23,
		0x012494b7,
		0x46348493,
		0x00992c23,
		0x001004b7,
		0x49348493,
		0x00992e23,
		0x0099a4b7,
		0x02348493,
		0x02992023,
		0x000a14b7,
		0xab348493,
		0x02992223,
		0x001004b7,
		0xfa948493,
		0x00c49493,
		0xa9348493,
		0x02992423,
		0x0007f4b7,
		0x05548493,
		0x00d49493,
		0xee348493,
		0x02992623,
		0x000fe4b7,
		0x5ff48493,
		0x00c49493,
		0x06f48493,
		0x02992823,
		0x100000e7)

	private def riscvToEbramSelMapper(sel: Bits) = B((31 downto 24) -> sel(3), (23 downto 16) -> sel(2), (15 downto 8) -> sel(1), (7 downto 0) -> sel(0))
	private def riscvToSpramSelMapper(sel: Bits) = B((7 downto 6) -> sel(3), (5 downto 4) -> sel(2), (3 downto 2) -> sel(1), (1 downto 0) -> sel(0))

	private val lowInstructionEbram = Ice40Ebram4k16WishboneBusAdapter(Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits, Some(instructions.map(x => ((x >> 0) & 0x0000ffff).toInt))))
	private val highInstructionEbram = Ice40Ebram4k16WishboneBusAdapter(Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits, Some(instructions.map(x => ((x >> 16) & 0x0000ffff).toInt))))

	private val instructionEbramBlocks = Seq(lowInstructionEbram, highInstructionEbram)
	private val wideInstructionEbramBlock = WishboneBusSelMappingAdapter(4 bits, WishboneBusDataExpander(instructionEbramBlocks(0).io.wishbone, instructionEbramBlocks(1).io.wishbone).io.master, riscvToEbramSelMapper)
	private val instructionEbramBlockWidthAdjusted = WishboneBusAddressMappingAdapter(30 bits, wideInstructionEbramBlock.io.master, adr => adr.resize(8 bits))

	private val dataSprams = Seq.fill(2) { new Ice40Spram16k16() }
	private val dataSpramBlocks = dataSprams.map(Ice40Spram16k16WishboneBusAdapter(_))
	private val wideDataSpramBlock = WishboneBusSelMappingAdapter(4 bits, WishboneBusDataExpander(dataSpramBlocks(0).io.wishbone, dataSpramBlocks(1).io.wishbone).io.master, riscvToSpramSelMapper)
	private val dataSpramBlockWidthAdjusted = WishboneBusAddressMappingAdapter(30 bits, wideDataSpramBlock.io.master, adr => adr.resize(14 bits))
	dataSprams.foreach { spram =>
		spram.io.PWROFF_N := True
		spram.io.SLEEP := False
		spram.io.STDBY := False
	}

	private val ledDeviceWidthAdjusted = WishboneBusAddressMappingAdapter(30 bits, ledDevice.io.wishbone, adr => adr.resize(1 bit))

	// Two arbiters driven by two maps drive the two muxes:
	// cpu.ibus -> adapter -> 2:2 mux(ibus, dbusToSharedSlavesBridge : ebram, spram)
	// cpu.dbus -> adapter -> 1:3 mux(dbus : dbusToSharedSlavesBridge, ledDevice)

	private val bridge = new CpuBusBridge(cpu.io.ibus.config, cpu.io.ibus.config.copy(useERR=false, useCTI=false, useBTE=false))
	bridge.io.cpu.ibus <> cpu.io.ibus // TODO: CAN ENCAPSULATE THIS IN CpuBusBridge.apply() INSTEAD
	bridge.io.cpu.dbus <> cpu.io.dbus

	private val dbusSlaveMap = bridge.dbusDeviceMapFor(
		ibus => !ibus.ADR(14),
		(ledDeviceWidthAdjusted.io.master -> (dbus => dbus.ADR(14))))

	private val executableSlaveMap = bridge.executableDeviceMapFor(
		instructionEbramBlockWidthAdjusted.io.master -> (
			dbus => !dbus.ADR(14) && !dbus.ADR(6),
			ibus => !ibus.ADR(14) && !ibus.ADR(6)),
		dataSpramBlockWidthAdjusted.io.master -> (
			dbus => !dbus.ADR(14) && dbus.ADR(6),
			ibus => !ibus.ADR(14) && ibus.ADR(6)))



	private val dbusOnlySlaveMux = WishboneBusSlaveMultiplexer(dbusSlaveMap.io.masters.head.index, dbusSlaveMap.slaves.head, dbusSlaveMap.slaves.tail:_*)
	bridge.io.devices.dbus <> dbusOnlySlaveMux.io.master

	private val crossbarArbiter = WishboneBusCrossbarArbiter(executableSlaveMap, inputs => PriorityEncoder(inputs.head, inputs.tail.toSeq:_*).io)

	private val crossbarMultiplexer = WishboneBusCrossbarMultiplexer(executableSlaveMap, crossbarArbiter)
}

object Core {
	case class IoBundle() extends Bundle {
		val p23 = out Bool()
		val ledR = out Bool()
		val ledG = out Bool()
		val ledB = out Bool()
	}

	def asBlackBox() = new CoreBlackBox()
}

class CoreBlackBox extends BlackBox {
	val io = new Core.IoBundle() {
		val clk = in Bool()
		val reset = in Bool()
	}

	noIoPrefix()
	setDefinitionName("Core")
	mapCurrentClockDomain(clock=io.clk, reset=io.reset)
}

/*
// Simple flasher
.global _boot
.text

.equ LED, 0x10000

_boot:
	li s1, 1
	li s2, LED

 _loop:
	sw s1, 0(s2)
	xori s1, s1, 1
	j _loop

-->

0x00100493
0x00010937
0x00992023
0x0014c493
0xff9ff06f
*/


/*
// RGB flasher
.global _boot
.text

.equ LED, 0x10000

_boot:
	li s1, 1
	li s2, 2
	li s3, 4
	li s4, LED

 _loop:
	sw s1, 0(s4)
	sw s2, 0(s4)
	sw s3, 0(s4)
	j _loop

-->

0x00100493
0x00200913
0x00400993
0x00010a37
0x009a2023
0x012a2023
0x013a2023
0xff5ff06f
*/

/*
// RGB chaser
.global _boot
.text

.equ LED, 0x10000
.equ DELAY, 200000

_boot:
	li s1, 4
	slli s2, s1, 1
	li s3, LED
	li s4, DELAY

loop:
	slli s1, s1, 1
	bne s1, s2, setLeds
	li s1, 1

setLeds:
	sw s1, 0(s3)

	add s5, s4, zero
delay:
	addi s5, s5, -1
	bne s5, zero, delay
	j loop

-->

0x00400493
0x00149913
0x000109b7
0x00031a37
0xd40a0a1b
0x00149493
0x01249463
0x00100493
0x0099a023
0x000a0ab3
0xfffa8a93
0xfe0a9ee3
0xfe5ff06f
*/

/*
// RGB chaser in another bank
.global _boot
.text

.equ OTHER_BANK_ADDR, 256

_boot:
	li s2, OTHER_BANK_ADDR
	li s1, 0x00400493
	sw s1, 0(s2)
	li s1, 0x00149913
	sw s1, 4(s2)
	li s1, 0x000109b7
	sw s1, 8(s2)
	li s1, 0x00031a37
	sw s1, 12(s2)
	li s1, 0xd40a0a1b
	sw s1, 16(s2)
	li s1, 0x00149493
	sw s1, 20(s2)
	li s1, 0x01249463
	sw s1, 24(s2)
	li s1, 0x00100493
	sw s1, 28(s2)
	li s1, 0x0099a023
	sw s1, 32(s2)
	li s1, 0x000a0ab3
	sw s1, 36(s2)
	li s1, 0xfffa8a93
	sw s1, 40(s2)
	li s1, 0xfe0a9ee3
	sw s1, 44(s2)
	li s1, 0xfe5ff06f
	sw s1, 48(s2)

	jalr OTHER_BANK_ADDR(x0)

-->

0x10000913
0x004004b7
0x49348493
0x00992023
0x0014a4b7
0x91348493
0x00992223
0x000114b7
0x9b748493
0x00992423
0x000324b7
0xa3748493
0x00992623
0x000d44b7
0x0a148493
0x00c49493
0xa1b48493
0x00992823
0x001494b7
0x49348493
0x00992a23
0x012494b7
0x46348493
0x00992c23
0x001004b7
0x49348493
0x00992e23
0x0099a4b7
0x02348493
0x02992023
0x000a14b7
0xab348493
0x02992223
0x001004b7
0xfa948493
0x00c49493
0xa9348493
0x02992423
0x0007f4b7
0x05548493
0x00d49493
0xee348493
0x02992623
0x000fe4b7
0x5ff48493
0x00c49493
0x06f48493
0x02992823
0x100000e7
*/
