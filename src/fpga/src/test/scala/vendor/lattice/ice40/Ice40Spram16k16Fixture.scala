package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40

import spinal.core._
import spinal.core.sim._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.tests.simulation._
import net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.spram.direct.{SpramGiven, SpramStateMachineBuilder}
import net.restall.ice40riscvsoc.vendor.lattice.ice40.Ice40Spram16k16

class Ice40Spram16k16Fixture extends Component {
	val io = new Ice40Spram16k16.IoBundle()
	noIoPrefix()

	private val dut = new Ice40Spram16k16
	io <> dut.io
	dut.io.CK.allowOverride()

	def given = new SpramGiven(new SpramStateMachineBuilder(io, List[Sampling => WithNextSampling]()))

	def wireStimuliUsing(initialState: Sampling) = {
		initialState.mustNotBeNull("initialState")

		var state = initialState
		val dutClockDomain = clockDomain.withoutReset
		dutClockDomain.withRevertedClockEdge.onSamplings { state = state.onSampling() }
		dutClockDomain.forkStimulus(period=10)
	}
}
