package net.restall.ice40riscvsoc.tests.simulation

import spinal.sim.SimThread

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._

class SimulationWaitForForkedState(thread: SimThread, nextState: Sampling) extends Sampling {
	thread.mustNotBeNull("thread")
	nextState.mustNotBeNull("nextState")

	override def onSampling(): Sampling = if (thread.isDone) nextState.onSampling() else this
}
