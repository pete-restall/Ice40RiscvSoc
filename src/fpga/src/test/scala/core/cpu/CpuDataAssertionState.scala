package net.restall.ice40riscvsoc.tests.core.cpu

import org.scalatest.matchers.must.Matchers._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.tests.simulation._

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
