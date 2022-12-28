package uk.co.lophtware.msfreference.tests.simulation

import spinal.sim.SimThread

class SimulationWaitForForkedState(thread: SimThread, nextState: Sampling) extends Sampling {// TODO: NULL CHECKS FOR thread AND nextState
	override def onSampling(): Sampling = if (thread.isDone) nextState.onSampling() else this
}
