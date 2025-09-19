package net.restall.ice40riscvsoc.tests.simulation

abstract trait Sampling {
	def onSampling(): Sampling = ???
}

abstract trait WithNextSampling extends Sampling {
	def withNext(nextState: Sampling): WithNextSampling = ???
}
