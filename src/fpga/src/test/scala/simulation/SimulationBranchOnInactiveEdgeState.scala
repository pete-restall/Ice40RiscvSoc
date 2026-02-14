package net.restall.ice40riscvsoc.tests.simulation

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._

class SimulationBranchOnInactiveEdgeState(condition: () => Boolean, whenTrue: WithNextSampling, whenFalse: WithNextSampling) extends WithNextSampling {
	condition.mustNotBeNull("condition")
	whenTrue.mustNotBeNull("whenTrue")
	whenFalse.mustNotBeNull("whenFalse")

	override def onInactiveEdge(): Sampling = if (condition()) whenTrue.onInactiveEdge() else whenFalse.onInactiveEdge()

	override def onActiveEdge(): Sampling = this

	override def withNext(nextState: Sampling): WithNextSampling = new SimulationBranchOnInactiveEdgeState(
		condition,
		whenTrue.withNext(nextState),
		whenFalse.withNext(nextState))
}
