package net.restall.ice40riscvsoc.core

import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneAdapter, WishboneConfig}
import spinal.lib.master
import vexriscv._
import vexriscv.plugin._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._

class Cpu(resetVector: Long, mtvecInit: Long, yamlOutFilename: Option[String]) extends Component {
	yamlOutFilename.mustNotBeNull("yamlOutFilename")

	val io = new Cpu.IoBundle()
	noIoPrefix()

	// TODO: Currently a simple non-pipelined instruction bus, but with pipeline (and the extra LUTS that involves), we could gain greater throughput.  For example:
	// - With a 96MHz flash clock and quad fast-read, we could read sequential 32-bit words at 12MHz; branches or dbus reads from flash obviously break the timing, but
	//   the latter dbus reads shouldn't invalidate the pipeline
	//
	// - Each VexRiscV instruction takes 5 clocks, with Fmax in the region of 32MHz for the ICE5UP device and Radiant synthesis
	//
	// - With a 96MHz master flash clock, we could run the CPU at 12MHz (divide-by-8) and get one instruction retired every cycle using a pipeline depth of 5
	//
	// - With a 96MHz master flash clock, we could run the CPU at 24MHz (divide-by-4) and get one *16-bit compact* instruction retired every cycle using a pipeline depth of 5
	//   and some interleaved 32-bit instructions could also execute at 24MHz until the pipeline was exhausted; compact instruction decoding requires roughly 200 extra LUTS
	//
	// - With a 96MHz master flash clock and 24MHz instruction clock, a jump would cause a random-access flash read of 24 clocks (6 CPU clocks), plus 5 further CPU clocks to
	//   execute the instruction (equating to 20 flash clocks, which would only refill 2 pipeline slots - there's going to be a long tail here).  Alternatively we can fill
	//   the entire pipeline in 24 + 4 * 8 = 56 flash clocks (14 CPU clocks) for more predictable instruction timing (less jitter, too)
	//
	// - With a small instruction cache, we could even do even better but it's no longer going to be a small / simple CPU.  Pipelining is definitely worth looking at though
	//
	// - The EBRAM and SPRAM ought to be able to run around 48MHz for time-division multiplexing with other peripherals (like DMA) so executing at 24MHz ought not to be
	//   'a waste' like wait states would be if we ran higher than the maximum flash throughput
	private val instructionBus = new IBusSimplePlugin(
		resetVector=resetVector,
		busLatencyMin=1, // From 1 -> 2 adds 100 LUTs; latency=1 is only if iBus can ACK before the next rising edge
		pendingMax=1, // what effect does this have in terms of LUTs ?  Makes it have better Fmax when =2, but executes instructions it shouldn't; =1 is the only value that works at the moment, so dig into it...
		injectorStage=true,
		cmdForkOnSecondStage=false, // TODO: might be able to relax this with 'false', if timings are still good (and save 136 LUTs)
		cmdForkPersistence=true, // if this is 'true' when fork-on-second-stage is 'false' then we get about 2MHz extra Fmax for an extra 31 gates
		prediction=NONE,
		catchAccessFault=false,
		compressedGen=false) // adds another 310 LUTs

	private val dataBus = new DBusSimplePlugin(
		catchAddressMisaligned=false,
		catchAccessFault=false,
		earlyInjection=false,
		emitCmdInMemoryStage=false,
		onlyLoadWords=false,
		withLrSc=false,
		bigEndian=false)

	private val csr = new CsrPlugin(new CsrPluginConfig(
		catchIllegalAccess=false,
		mvendorid=null,
		marchid=null,
		mimpid=null,
		mhartid=null,
		misaExtensionsInit=66,
		misaAccess=CsrAccess.NONE,
		mtvecAccess=CsrAccess.NONE,
		mtvecInit=mtvecInit,
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
					zeroBoot=false,
					x0Init=true,
					writeRfInMemoryStage=false,
					readInExecute=false,
					syncUpdateOnStall=true,
					rv32e=false,
					withShadow=false),
				new IntAluPlugin,
				new SrcPlugin(
					separatedAddSub=false,
					executeInsertion=false,
					decodeAddSub=false),
				new LightShifterPlugin,
				new HazardSimplePlugin(
					bypassExecute=false, // Briey uses true
					bypassMemory=false, // Briey uses
					bypassWriteBack=false, // Briey uses
					bypassWriteBackBuffer=false, // Briey uses
					pessimisticUseSrc=false,
					pessimisticWriteRegFile=false,
					pessimisticAddressMatch=false),
				new BranchPlugin(
					earlyBranch=false,
					catchAddressMisaligned=false,
					fenceiGenAsAJump=false,
					fenceiGenAsANop=false,
					decodeBranchSrc2=false)) ++
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
