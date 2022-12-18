package uk.co.lophtware.msfreference.tests

import org.scalatest.flatspec._
import org.scalatest.Suite

class PriorityEncoderSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] = Array(
		new PriorityEncoderSimulationTest(numberOfInputs=2),
		new PriorityEncoderSimulationTest(numberOfInputs=3),
		new PriorityEncoderSimulationTest(numberOfInputs=16))
}
