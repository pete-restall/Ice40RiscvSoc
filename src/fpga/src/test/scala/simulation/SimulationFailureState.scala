package uk.co.lophtware.msfreference.tests.simulation

import spinal.core.sim._

class SimulationFailureState(errorMessage: String) extends Sampling {
	override def onSampling(): Sampling = simFailure(errorMessage)
}
