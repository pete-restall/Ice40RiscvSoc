package net.restall.ice40riscvsoc.tests.simulation

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._

class SimulationBranchOnActiveEdgeState(condition: () => Boolean, whenTrue: WithNextSampling, whenFalse: WithNextSampling) extends WithNextSampling {
	condition.mustNotBeNull("condition")
	whenTrue.mustNotBeNull("whenTrue")
	whenFalse.mustNotBeNull("whenFalse")

	override def onActiveEdge(): Sampling = if (condition()) whenTrue.onActiveEdge() else whenFalse.onActiveEdge()

	override def withNext(nextState: Sampling): WithNextSampling = new SimulationBranchOnActiveEdgeState(
		condition,
		whenTrue.withNext(nextState),
		whenFalse.withNext(nextState))
}
