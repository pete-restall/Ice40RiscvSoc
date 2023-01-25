package uk.co.lophtware.msfreference.tests.simulation

import spinal.core.sim._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._

class SimulationFailureState(errorMessage: String) extends Sampling {
	errorMessage.mustBeSpecified("errorMessage")

	override def onSampling(): Sampling = simFailure(errorMessage)
}
