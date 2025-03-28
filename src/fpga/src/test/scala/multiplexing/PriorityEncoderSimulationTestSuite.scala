package uk.co.lophtware.msfreference.tests.multiplexing

import org.scalatest.flatspec._
import org.scalatest.Suite

class PriorityEncoderSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] =
		createTestCasesWith(dutCreatedViaApplyFactory=false) ++
		createTestCasesWith(dutCreatedViaApplyFactory=true)

	private def createTestCasesWith(dutCreatedViaApplyFactory: Boolean) = Array(
		new PriorityEncoderSimulationTest(numberOfInputs=1, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new PriorityEncoderSimulationTest(numberOfInputs=2, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new PriorityEncoderSimulationTest(numberOfInputs=3, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new PriorityEncoderSimulationTest(numberOfInputs=16, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory))
}
