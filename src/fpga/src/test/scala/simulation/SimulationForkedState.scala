package uk.co.lophtware.msfreference.tests.simulation

import spinal.core.sim._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._

class SimulationForkedState(nextState: Sampling, action: () => Unit = null) extends Sampling {
	nextState.mustNotBeNull("nextState")

	override def onSampling(): Sampling = new SimulationWaitForForkedState(fork { onForked() }, nextState)

	protected def onForked(): Unit = if (action != null) action()
}
