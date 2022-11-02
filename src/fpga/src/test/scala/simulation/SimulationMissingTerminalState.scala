package uk.co.lophtware.msfreference.tests.simulation

import spinal.core.sim._

object SimulationMissingTerminalState extends Sampling {
	override def onSampling(): Sampling = simFailure("Simulation state machine is missing a terminal state")
}
