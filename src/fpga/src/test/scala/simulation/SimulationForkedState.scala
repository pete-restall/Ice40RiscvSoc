package uk.co.lophtware.msfreference.tests.simulation

import spinal.core.sim._

class SimulationForkedState(private val nextState: Sampling, private val action: () => Unit = null) extends Sampling {
	override def onSampling(): Sampling = new SimulationWaitForForkedState(fork { onForked() }, nextState)

	protected def onForked(): Unit = if (action != null) action()
}
