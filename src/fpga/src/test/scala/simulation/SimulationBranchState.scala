package net.restall.ice40riscvsoc.tests.simulation

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._

class SimulationBranchState(condition: () => Boolean, whenTrue: WithNextSampling, whenFalse: WithNextSampling) extends WithNextSampling {
	condition.mustNotBeNull("condition")
	whenTrue.mustNotBeNull("whenTrue")
	whenFalse.mustNotBeNull("whenFalse")

	override def onSampling(): Sampling = if (condition()) whenTrue.onSampling() else whenFalse.onSampling()

	override def withNext(nextState: Sampling): WithNextSampling = new SimulationBranchState(
		condition,
		whenTrue.withNext(nextState),
		whenFalse.withNext(nextState))
}
