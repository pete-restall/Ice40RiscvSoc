package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.raw.{SpramGiven, SpramStateMachineBuilder}
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Spram16k16

class Ice40Spram16k16Fixture extends Component {
	val io = new Ice40Spram16k16.IoBundle()
	noIoPrefix()

	private val dut = new Ice40Spram16k16
	io <> dut.io
	dut.io.CK.allowOverride()

	def given = new SpramGiven(new SpramStateMachineBuilder(io, List[Sampling => WithNextSampling]()))

	def wireStimuliUsing(initialState: Sampling) = {
		var state = initialState
		val dutClockDomain = clockDomain.withoutReset
		dutClockDomain.withRevertedClockEdge.onSamplings { state = state.onSampling() }
		dutClockDomain.forkStimulus(period=10)
	}
}
