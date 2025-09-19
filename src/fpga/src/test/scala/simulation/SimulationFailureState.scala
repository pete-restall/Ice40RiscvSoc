package net.restall.ice40riscvsoc.tests.simulation

import spinal.core.sim._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._

class SimulationFailureState(errorMessage: String) extends Sampling {
	errorMessage.mustBeSpecified("errorMessage")

	override def onSampling(): Sampling = simFailure(errorMessage)
}
