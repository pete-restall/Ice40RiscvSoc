package net.restall.ice40riscvsoc.tests.simulation

abstract trait Sampling {
	def onInactiveEdge(): Sampling = this
	def onActiveEdge(): Sampling = ???
}

abstract trait WithNextSampling extends Sampling {
	def withNext(nextState: Sampling): WithNextSampling = ???
}
