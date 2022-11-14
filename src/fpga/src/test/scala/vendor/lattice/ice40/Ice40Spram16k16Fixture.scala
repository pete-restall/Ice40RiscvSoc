package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.{SpramGiven, SpramStateMachineBuilder}
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Spram16k16

class Ice40Spram16k16Fixture extends Component {
	val io = Ice40Spram16k16.IoBundle()
	noIoPrefix()

	private val dut = new Ice40Spram16k16
	io <> dut.io

	def given = new SpramGiven(new SpramStateMachineBuilder(io, List[Sampling => WithNextSampling]()))

	def wireStimuliUsing(initialState: Sampling) = {
		var state = initialState
		clockDomain.withRevertedClockEdge.onSamplings { state = state.onSampling() }
		clockDomain.forkStimulus(period=10)
	}

	override val clockDomain = ClockDomain(clock=io.CK)
}
