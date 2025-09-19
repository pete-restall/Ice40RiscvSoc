package net.restall.ice40riscvsoc.tests.simulation

import spinal.core.sim._

object SimulationMissingTerminalState extends Sampling {
	private val failed = new SimulationFailureState("Simulation state machine is missing a terminal state")

	override def onSampling(): Sampling = failed
}
