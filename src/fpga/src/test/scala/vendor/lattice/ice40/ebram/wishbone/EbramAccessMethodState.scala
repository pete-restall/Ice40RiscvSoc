package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram.wishbone

import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation._

class EbramAccessMethodState( // TODO: NULL CHECKS FOR ALL THESE CONSTRUCTOR ARGS
	private val isEbramDirectSelector: Bool,
	private val isEbramDirect: Boolean,
	private val nextState: Sampling) extends WithNextSampling {

	override def onSampling(): Sampling = {
		isEbramDirectSelector #= isEbramDirect
		nextState
	}

	override def withNext(nextState: Sampling) = new EbramAccessMethodState(isEbramDirectSelector, isEbramDirect, nextState)
}

object EbramAccessMethodState {
	def direct(isEbramDirect: Bool, nextState: Sampling = SimulationMissingNextState): WithNextSampling =
		new EbramAccessMethodState(isEbramDirect, true, nextState)

	def wishbone(isEbramDirect: Bool, nextState: Sampling = SimulationMissingNextState): WithNextSampling =
		new EbramAccessMethodState(isEbramDirect, false, nextState)
}
