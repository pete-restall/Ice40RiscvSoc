package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.spram.wishbone

import spinal.core._
import spinal.core.sim._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.tests.simulation._

class SpramAccessMethodState(isSpramDirectSelector: Bool, isSpramDirect: Boolean, nextState: Sampling) extends WithNextSampling {
	nextState.mustNotBeNull("nextState")

	override def onSampling(): Sampling = {
		isSpramDirectSelector #= isSpramDirect
		nextState
	}

	override def withNext(nextState: Sampling) = new SpramAccessMethodState(isSpramDirectSelector, isSpramDirect, nextState)
}

object SpramAccessMethodState {
	def direct(isSpramDirect: Bool, nextState: Sampling = SimulationMissingNextState): WithNextSampling =
		new SpramAccessMethodState(isSpramDirect, true, nextState)

	def wishbone(isSpramDirect: Bool, nextState: Sampling = SimulationMissingNextState): WithNextSampling =
		new SpramAccessMethodState(isSpramDirect, false, nextState)
}
