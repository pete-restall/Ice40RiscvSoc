package uk.co.lophtware.msfreference.tests.simulation

class SimulationBranchState(
	private val condition: () => Boolean,
	private val whenTrue: WithNextSampling,
	private val whenFalse: WithNextSampling) extends WithNextSampling {

	override def onSampling(): Sampling = if (condition()) whenTrue.onSampling() else whenFalse.onSampling()

	override def withNext(nextState: Sampling): WithNextSampling = new SimulationBranchState(// TODO: NULL CHECKS FOR nextState
		condition,
		whenTrue.withNext(nextState),
		whenFalse.withNext(nextState))
}
