package uk.co.lophtware.msfreference.core

import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneAdapter, WishboneConfig}
import spinal.lib.{Counter, slave, StreamArbiterFactory}
import vexriscv._
import vexriscv.plugin._

import uk.co.lophtware.msfreference.bus._
import uk.co.lophtware.msfreference.bus.wishbone._
import uk.co.lophtware.msfreference.multiplexing._
import uk.co.lophtware.msfreference.SeqPairingExtensions._
import uk.co.lophtware.msfreference.vendor.lattice.ice40._

class Core extends Component {
	val io = new Core.IoBundle()
	noIoPrefix()

/*
	private val instructionBus = new IBusSimplePlugin(
		//resetVector=0x00866000, // Bit 23 = flash (0x00800000), plus next 4KiB boundary after the four bitstreams; just over 1KiB wasted.  SPI flash sectors (minimum erasure blocks) are 4KiB in size.
		resetVector=0x00000000, // TEMPORARY - START OF THE EBRAM BLOCK...
		busLatencyMin=1, // From 1 -> 2 adds 100 LUTs
		injectorStage=true,
		cmdForkOnSecondStage=true, // might be able to relax this with 'false', if timings are still good (and save 136 LUTs, PLUS decent Fmax improvement)
		cmdForkPersistence=true, // saves 116 LUTs if this can be set to 'false'; also adds a little extra slack
		prediction=NONE,
		catchAccessFault=false,
		compressedGen=false)

	private val instructionBus = new IBusSimplePlugin(
		//resetVector=0x00866000, // Bit 23 = flash (0x00800000), plus next 4KiB boundary after the four bitstreams; just over 1KiB wasted.  SPI flash sectors (minimum erasure blocks) are 4KiB in size.
		//resetVector=0x00000000, // TEMPORARY - START OF THE EBRAM BLOCK...
		resetVector=resetVector,
		busLatencyMin=1, // From 1 -> 2 adds 100 LUTs
		pendingMax=1, // what effect does this have in terms of LUTs ?  Makes it have better Fmax when =2, but executes instructions it shouldn't; =1 is the only value that works at the moment (for non-compact instructions), so dig into it...
		injectorStage=true,
		cmdForkOnSecondStage=false, // TODO: might be able to relax this with 'false', if timings are still good (and save 136 LUTs)
		cmdForkPersistence=true, // if this is 'true' when fork-on-second-stage is 'false' then we get about 2MHz extra Fmax for an extra 31 gates
		prediction=NONE,
		catchAccessFault=false,
		compressedGen=false) // adds another 310 LUTs

	private val dataBus = new DBusSimplePlugin(
		catchAddressMisaligned=false,
		catchAccessFault=false)

	private val csr = new CsrPlugin(new CsrPluginConfig(
		catchIllegalAccess=false,
		mvendorid=null,
		marchid=null,
		mimpid=null,
		mhartid=null,
		misaExtensionsInit=66,
		misaAccess=CsrAccess.NONE,
		mtvecAccess=CsrAccess.NONE,
		mtvecInit=0x00000201,
		mepcAccess=CsrAccess.NONE,
		mscratchGen=false,
		mcauseAccess=CsrAccess.READ_ONLY,
		mbadaddrAccess=CsrAccess.NONE,
		mcycleAccess=CsrAccess.NONE,
		minstretAccess=CsrAccess.NONE,
		ecallGen=false,
		wfiGenAsWait=false,
		ucycleAccess=CsrAccess.NONE,
		uinstretAccess=CsrAccess.NONE))

	private val cpu = new VexRiscv(
		config=VexRiscvConfig(
			withMemoryStage=true, // extra 65 LUTs used when true; Fmax waaay off when false
			withWriteBackStage=true,
			plugins=List(
				instructionBus,
				dataBus,
				csr,
 				new DecoderSimplePlugin(
					catchIllegalInstruction=false),
				new RegFilePlugin(
					regFileReadyKind=plugin.SYNC,
					zeroBoot=false), // TODO: USES 4x EBRAM - WOULD BE BETTER IF IT COULD USE ONLY 2x EBRAM; INVESTIGATE WHY
				new IntAluPlugin,
				new SrcPlugin(
					separatedAddSub=false,
					executeInsertion=false),
				new LightShifterPlugin,
				new HazardSimplePlugin(
					bypassExecute=false,
					bypassMemory=false,
					bypassWriteBack=false,
					bypassWriteBackBuffer=false,
					pessimisticUseSrc=false,
					pessimisticWriteRegFile=false,
					pessimisticAddressMatch=false),
				new BranchPlugin(
					earlyBranch=false,
					catchAddressMisaligned=false),
				new YamlPlugin("src/radiant/cpu.yaml"))))

	csr.externalInterrupt := False
	csr.timerInterrupt := False

*/

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

	private def riscvToEbramSelMapper(sel: Bits) = B((31 downto 24) -> sel(3), (23 downto 16) -> sel(2), (15 downto 8) -> sel(1), (7 downto 0) -> sel(0))

	private val lowInstructionEbram = Ice40Ebram4k16WishboneBusAdapter(Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits, Some(instructions.map(x => (x >> 0) & 0x0000ffff))))
	private val highInstructionEbram = Ice40Ebram4k16WishboneBusAdapter(Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits, Some(instructions.map(x => (x >> 16) & 0x0000ffff))))

	private val instructionEbramBlocks = Seq(lowInstructionEbram, highInstructionEbram)
	private val wideInstructionEbramBlock = WishboneBusSelMappingAdapter(4 bits, WishboneBusDataExpander(instructionEbramBlocks(0).io.wishbone, instructionEbramBlocks(1).io.wishbone).io.master, riscvToEbramSelMapper)

	private val ledDeviceWidthAdjusted = WishboneBusAddressMappingAdapter(30 bits, ledDevice.io.wishbone, adr => adr.resize(1 bit))
	private val instructionEbramBlockWidthAdjusted = WishboneBusAddressMappingAdapter(30 bits, wideInstructionEbramBlock.io.master, adr => adr.resize(8 bits))

	val ibusAdapter = new WishboneAdapter(cpu.io.ibus.config, cpu.io.ibus.config.copy(useERR=false, useCTI=false, useBTE=false))
	ibusAdapter.io.wbm <> cpu.io.ibus
	val ibus = ibusAdapter.io.wbs

	val dbusAdapter = new WishboneAdapter(cpu.io.dbus.config, cpu.io.dbus.config.copy(useERR=false, useCTI=false, useBTE=false))
	dbusAdapter.io.wbm <> cpu.io.dbus
	val dbus = dbusAdapter.io.wbs

	private val sharedSlaveMap = WishboneBusMasterSlaveMap(
		(dbus, m => !m.ADR(14), instructionEbramBlockWidthAdjusted.io.master),
		(ibus, m => !m.ADR(14), instructionEbramBlockWidthAdjusted.io.master),
		(dbus, m => m.ADR(14), ledDeviceWidthAdjusted.io.master),
		(ibus, m => m.ADR(14), ledDeviceWidthAdjusted.io.master), // should not be present for the ibus...
		/*(cpu.io.dbus, m => m.ADR(14), wideInstructionEbramBlock2WidthAdjusted.io.master),
		(cpu.io.ibus, m => m.ADR(14), wideInstructionEbramBlock2WidthAdjusted.io.master)*/)

	private val ibusMasterIndex = sharedSlaveMap.masters.indexOf(ibus) // TODO: THIS LOOKUP WANTS PUTTING ON THE WishboneBusMasterSlaveMap CLASS; def masterIoFor(wb): MasterIoBundle
	private val dbusMasterIndex = sharedSlaveMap.masters.indexOf(dbus) // TODO: WishboneBusMasterSlaveMap CLASS ALSO WANTS; def indexOfMaster(wb): Int && def indexOfSlave(wb): Int

	private val ibusSlaveSelectorX = sharedSlaveMap.io.masters(ibusMasterIndex)
	private val dbusSlaveSelectorX = sharedSlaveMap.io.masters(dbusMasterIndex)

	val sharedSlaveArbiters = for (slaveIndex <- 0 until sharedSlaveMap.slaves.length) yield {
		val encoder = new PriorityEncoder(numberOfInputs=sharedSlaveMap.masters.length)
		val sharedSlaveArbiter = new MultiMasterSingleSlaveArbiter(numberOfMasters=sharedSlaveMap.masters.length)
		encoder.io.inputs := sharedSlaveArbiter.io.encoder.inputs
		sharedSlaveArbiter.io.encoder.isValid := encoder.io.isValid
		sharedSlaveArbiter.io.encoder.output := encoder.io.output

		sharedSlaveMap.masters.zipWithIndex.foreach { case (master, masterIndex) =>
			sharedSlaveArbiter.io.masters(masterIndex).request := master.CYC && ibusSlaveSelectorX.index === slaveIndex
		}

		sharedSlaveArbiter
	}

//	private val ibusSharedSlaves = WishboneBusSlaveMultiplexer(ibusSlaveSelectorX.index, instructionEbramBlockWidthAdjusted.io.master, wideInstructionEbramBlock2WidthAdjusted.io.master)
	private val masterMux = WishboneBusMasterMultiplexer(sharedSlaveArbiters(0).io.grantedMasterIndex, sharedSlaveMap.masters.head, sharedSlaveMap.masters.tail:_*)
	masterMux.io.slave <> instructionEbramBlockWidthAdjusted.io.master
	// TODO: OH DEAR...NO LED ASSIGNMENT, BECAUSE WE'RE OPERATING ON A SINGLE SLAVE...
	ledDeviceWidthAdjusted.io.master.ADR := dbus.ADR
	ledDeviceWidthAdjusted.io.master.SEL := dbus.SEL
	ledDeviceWidthAdjusted.io.master.DAT_MOSI := dbus.DAT_MOSI
	ledDeviceWidthAdjusted.io.master.CYC := dbus.CYC && dbus.ADR(14)
	ledDeviceWidthAdjusted.io.master.STB := dbus.STB
	ledDeviceWidthAdjusted.io.master.WE := dbus.WE
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
