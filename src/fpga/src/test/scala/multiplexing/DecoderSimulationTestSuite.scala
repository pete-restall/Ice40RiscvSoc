package net.restall.ice40riscvsoc.tests.multiplexing

import org.scalatest.flatspec._
import org.scalatest.Suite
import spinal.core._

class DecoderSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] =
		createTestCasesWith(dutCreatedViaApplyFactory=false) ++
		createTestCasesWith(dutCreatedViaApplyFactory=true)

	private def createTestCasesWith(dutCreatedViaApplyFactory: Boolean) = Array(
		new DecoderSimulationTest(inputWidth=1 bit, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new DecoderSimulationTest(inputWidth=2 bits, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new DecoderSimulationTest(inputWidth=3 bits, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new DecoderSimulationTest(inputWidth=7 bits, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory))
}
