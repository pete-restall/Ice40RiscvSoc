package uk.co.lophtware.msfreference.tests.simulation

import spinal.sim.SimThread

class SimulationWaitForForkedState(private val thread: SimThread, private val nextState: Sampling) extends Sampling {// TODO: NULL CHECKS FOR thread AND nextState
	override def onSampling(): Sampling = if (thread.isDone) nextState.onSampling() else this
}
