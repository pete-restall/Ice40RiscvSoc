package uk.co.lophtware.msfreference.tests.simulation

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._

class ConcurrentSamplingState(samplings: Sampling*) extends Sampling {
	samplings.mustNotBeNull("samplings")

	private var states = samplings.toSeq

	override def onSampling(): Sampling = {
		states = states.map(_.onSampling())
		this
	}
}
