package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.wishbone

import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Spram16k16

class SpramAccessMethodState(
	private val isSpramDirectSelector: Bool,
	private val isSpramDirect: Boolean,
	private val nextState: Sampling) extends WithNextSampling {

	override def onSampling(): Sampling = {
		isSpramDirectSelector #= isSpramDirect
		nextState.onSampling()
	}

	override def withNext(nextState: Sampling) = new SpramAccessMethodState(isSpramDirectSelector, isSpramDirect, nextState)
}

object SpramAccessMethodState {
	def direct(isSpramDirect: Bool, nextState: Sampling = SimulationMissingNextState): WithNextSampling =
		new SpramAccessMethodState(isSpramDirect, true, nextState)

	def wishbone(isSpramDirect: Bool, nextState: Sampling = SimulationMissingNextState): WithNextSampling =
		new SpramAccessMethodState(isSpramDirect, false, nextState)
}
