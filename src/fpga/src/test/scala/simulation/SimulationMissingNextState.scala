package uk.co.lophtware.msfreference.tests.simulation

import spinal.core.sim._

object SimulationMissingNextState extends Sampling {
	private val failed = new SimulationFailureState("Simulation state machine requires an explicit next state but one was not set")

	override def onSampling(): Sampling = failed
}
