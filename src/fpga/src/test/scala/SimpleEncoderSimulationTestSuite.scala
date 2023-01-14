package uk.co.lophtware.msfreference.tests

import org.scalatest.flatspec._
import org.scalatest.Suite

class SimpleEncoderSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] =
		createTestCasesWith(dutCreatedViaApplyFactory=false) ++
		createTestCasesWith(dutCreatedViaApplyFactory=true)

	private def createTestCasesWith(dutCreatedViaApplyFactory: Boolean) = Array(
		new SimpleEncoderSimulationTest(numberOfInputs=1, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new SimpleEncoderSimulationTest(numberOfInputs=2, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new SimpleEncoderSimulationTest(numberOfInputs=3, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new SimpleEncoderSimulationTest(numberOfInputs=16, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory))
}
