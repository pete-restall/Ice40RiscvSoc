package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40

import scala.util.Random

import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.Wishbone
import spinal.lib.slave

import net.restall.ice40riscvsoc.tests.simulation._
import net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.ebram.wishbone.{EbramGiven, EbramStateMachineBuilder}
import net.restall.ice40riscvsoc.vendor.lattice.ice40.{Ice40Ebram4k, Ice40Ebram4k16WishboneBusAdapter}

class Ice40Ebram4k16WishboneBusAdapterIntegrationFixture extends Component {
	val io = new Bundle {
		val wishbone = slave(new Wishbone(Ice40Ebram4k16WishboneBusAdapter.wishboneConfig))
		val ebramDirect = new Ice40Ebram4k.IoBundle(readWidth=16 bits, writeWidth=16 bits)
		val isEbramDirect = in Bool()
	}

	private val ebram = new Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits)
	private val dut = new Ice40Ebram4k16WishboneBusAdapter()
	io.wishbone <> dut.io.wishbone
	ebram.io.ADR := Mux(io.isEbramDirect, io.ebramDirect.ADR, dut.io.ebram.ADR)
	ebram.io.ADW := Mux(io.isEbramDirect, io.ebramDirect.ADW, dut.io.ebram.ADW)
	ebram.io.CER := Mux(io.isEbramDirect, io.ebramDirect.CER, dut.io.ebram.CER)
	ebram.io.CEW := Mux(io.isEbramDirect, io.ebramDirect.CEW, dut.io.ebram.CEW)
	ebram.io.RE := Mux(io.isEbramDirect, io.ebramDirect.RE, dut.io.ebram.RE)
	ebram.io.WE := Mux(io.isEbramDirect, io.ebramDirect.WE, dut.io.ebram.WE)
	ebram.io.DI := Mux(io.isEbramDirect, io.ebramDirect.DI, dut.io.ebram.DI)
	ebram.io.MASK_N.map(_ := Mux(io.isEbramDirect, io.ebramDirect.MASK_N.get, dut.io.ebram.MASK_N))
	ebram.io.CKR := clockDomain.readClockWire
	ebram.io.CKW := clockDomain.readClockWire
	dut.io.ebram.DO := ebram.io.DO
	io.ebramDirect.DO := ebram.io.DO

	def given = new EbramGiven(new EbramStateMachineBuilder(clockDomain, io.wishbone, io.ebramDirect, io.isEbramDirect, List[Sampling => WithNextSampling]()))

	def wireStimuliUsing(initialState: Sampling) = {
		var state = initialState
		clockDomain.withRevertedClockEdge.onSamplings { state = state.onSampling() }
		clockDomain.forkStimulus(period=10)
		SimulationFixtureBase.waitForExplicitSimulationTermination
	}

	def anyAddress(): Int = Random.nextInt(1 << io.wishbone.ADR.getWidth)

	def anyData(): Int = Random.nextInt(1 << io.wishbone.DAT_MOSI.getWidth)
}
