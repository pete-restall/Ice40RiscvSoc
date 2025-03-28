package uk.co.lophtware.msfreference.tests.simulation

import spinal.sim.SimThread

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._

class SimulationWaitForForkedState(thread: SimThread, nextState: Sampling) extends Sampling {
	thread.mustNotBeNull("thread")
	nextState.mustNotBeNull("nextState")

	override def onSampling(): Sampling = if (thread.isDone) nextState.onSampling() else this
}
