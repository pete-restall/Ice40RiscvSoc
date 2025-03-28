package uk.co.lophtware.msfreference.tests.core.cpu

import org.scalatest.matchers.must.Matchers._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.tests.simulation._

class CpuDataAssertionState(
	data: scala.collection.mutable.Map[Long, Long],
	expectedData: Map[Long, Long],
	nextState: Sampling) extends WithNextSampling {

	data.mustNotBeNull("data")
	expectedData.mustNotBeNull("expectedData")
	nextState.mustNotBeNull("nextState")

	override def onSampling(): Sampling = {
		data must equal(expectedData)
		nextState.onSampling()
	}

	override def withNext(nextState: Sampling) = new CpuDataAssertionState(data, expectedData, nextState)
}
