package net.restall.ice40riscvsoc.tests.simulation

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._

class ConcurrentSamplingState(samplings: Sampling*) extends Sampling {
	samplings.mustNotBeNull("samplings")

	private var states = samplings.toSeq

	override def onSampling(): Sampling = {
		states = states.map(_.onSampling())
		this
	}
}
