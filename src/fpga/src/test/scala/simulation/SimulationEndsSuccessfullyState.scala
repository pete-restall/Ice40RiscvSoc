package uk.co.lophtware.msfreference.tests.simulation

import spinal.core.sim._

object SimulationEndsSuccessfullyState extends Sampling {
	override def onSampling(): Sampling = simSuccess()
}
