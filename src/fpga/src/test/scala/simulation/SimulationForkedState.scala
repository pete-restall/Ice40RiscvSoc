package uk.co.lophtware.msfreference.tests.simulation

import spinal.core.sim._

class SimulationForkedState(nextState: Sampling, action: () => Unit = null) extends Sampling {// TODO: NULL CHECKS FOR nextState AND action
	override def onSampling(): Sampling = new SimulationWaitForForkedState(fork { onForked() }, nextState)

	protected def onForked(): Unit = if (action != null) action()
}
