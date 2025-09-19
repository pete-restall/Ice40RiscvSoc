package net.restall.ice40riscvsoc.tests.simulation

import spinal.core.sim._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._

class SimulationForkedState(nextState: Sampling, action: () => Unit = null) extends Sampling {
	nextState.mustNotBeNull("nextState")

	override def onSampling(): Sampling = new SimulationWaitForForkedState(fork { onForked() }, nextState)

	protected def onForked(): Unit = if (action != null) action()
}
