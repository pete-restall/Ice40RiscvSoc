package net.restall.ice40riscvsoc.tests.simulation

import spinal.core.sim._

object SimulationEndsSuccessfullyState extends Sampling {
	override def onSampling(): Sampling = simSuccess()
}
