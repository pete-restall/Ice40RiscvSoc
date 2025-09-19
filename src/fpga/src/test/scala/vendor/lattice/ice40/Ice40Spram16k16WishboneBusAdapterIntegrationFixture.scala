package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40

import scala.util.Random

import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.Wishbone
import spinal.lib.slave

import net.restall.ice40riscvsoc.tests.simulation._
import net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.spram.wishbone.{SpramGiven, SpramStateMachineBuilder}
import net.restall.ice40riscvsoc.vendor.lattice.ice40.{Ice40Spram16k16, Ice40Spram16k16WishboneBusAdapter}

class Ice40Spram16k16WishboneBusAdapterIntegrationFixture extends Component {
	val io = new Bundle {
		val wishbone = slave(new Wishbone(Ice40Spram16k16WishboneBusAdapter.wishboneConfig))
		val spramDirect = new Ice40Spram16k16.IoBundle()
		val isSpramDirect = in Bool()
	}

	private val spram = new Ice40Spram16k16()
	private val dut = new Ice40Spram16k16WishboneBusAdapter()
	io.wishbone <> dut.io.wishbone
	spram.io.AD := Mux(io.isSpramDirect, io.spramDirect.AD, dut.io.spram.AD)
	spram.io.CS := Mux(io.isSpramDirect, io.spramDirect.CS, dut.io.spram.CS)
	spram.io.DI := Mux(io.isSpramDirect, io.spramDirect.DI, dut.io.spram.DI)
	spram.io.MASKWE := Mux(io.isSpramDirect, io.spramDirect.MASKWE, dut.io.spram.MASKWE)
	spram.io.PWROFF_N := io.spramDirect.PWROFF_N
	spram.io.SLEEP := io.spramDirect.SLEEP
	spram.io.STDBY := io.spramDirect.STDBY
	spram.io.WE := Mux(io.isSpramDirect, io.spramDirect.WE, dut.io.spram.WE)
	dut.io.spram.DO := spram.io.DO
	io.spramDirect.DO := spram.io.DO

	def given = new SpramGiven(new SpramStateMachineBuilder(clockDomain, io.wishbone, io.spramDirect, io.isSpramDirect, List[Sampling => WithNextSampling]()))

	def wireStimuliUsing(initialState: Sampling) = {
		var state = initialState
		clockDomain.withRevertedClockEdge.onSamplings { state = state.onSampling() }
		clockDomain.forkStimulus(period=10)
		SimulationFixtureBase.waitForExplicitSimulationTermination
	}

	def anyAddress(): Int = Random.nextInt(1 << io.wishbone.ADR.getWidth)

	def anyData(): Int = Random.nextInt(1 << io.wishbone.DAT_MOSI.getWidth)
}
