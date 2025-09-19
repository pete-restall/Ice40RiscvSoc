package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.ebram.wishbone

import spinal.core._
import spinal.core.sim._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.tests.simulation._

class EbramAccessMethodState(isEbramDirectSelector: Bool, isEbramDirect: Boolean, nextState: Sampling) extends WithNextSampling {
	nextState.mustNotBeNull("nextState")

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
