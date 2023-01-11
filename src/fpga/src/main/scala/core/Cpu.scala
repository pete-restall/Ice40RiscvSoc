package uk.co.lophtware.msfreference.core

import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneAdapter, WishboneConfig}
import spinal.lib.master
import vexriscv._
import vexriscv.plugin._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._

class Cpu(resetVector: Long, mtvecInit: Long, yamlOutFilename: Option[String]) extends Component {
	yamlOutFilename.mustNotBeNull("yamlOutFilename")

	val io = new Cpu.IoBundle()
	noIoPrefix()

	private val instructionBus = new IBusSimplePlugin(
		resetVector=resetVector,
		busLatencyMin=1, // From 1 -> 2 adds 100 LUTs
		pendingMax=1, // what effect does this have in terms of LUTs ?  Makes it have better Fmax when =2, but executes instructions it shouldn't; =1 is the only value that works at the moment, so dig into it...
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
			withMemoryStage=true,
			withWriteBackStage=true,
			plugins=List(
				instructionBus,
				dataBus,
				csr,
 				new DecoderSimplePlugin(
					catchIllegalInstruction=false),
				new RegFilePlugin(
					regFileReadyKind=plugin.SYNC,
					zeroBoot=false),
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
					catchAddressMisaligned=false)) ++
				yamlOutFilename.map(new YamlPlugin(_))))

	csr.externalInterrupt := False // TODO: io.interrupts.external
	csr.timerInterrupt := False // TODO: io.interrupts.timer

	io.ibus <> instructionBus.iBus.toWishbone()
	io.dbus <> dataBus.dBus.toWishbone()
}

object Cpu {
	case class IoBundle() extends Bundle {
		val ibus = master(new Wishbone(IBusSimpleBus.getWishboneConfig()))
		val dbus = master(new Wishbone(DBusSimpleBus.getWishboneConfig()))
		val interrupts = new Bundle {
			val external = in Bool()
			val timer = in Bool()
		}
	}
}
