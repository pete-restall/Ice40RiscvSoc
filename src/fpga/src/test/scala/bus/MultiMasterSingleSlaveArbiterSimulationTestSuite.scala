package net.restall.ice40riscvsoc.tests.bus

import org.scalatest.flatspec._
import org.scalatest.Suite

class MultiMasterSingleSlaveArbiterSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] =
		createTestCasesWith(dutCreatedViaApplyFactory=false)// ++
		//createTestCasesWith(dutCreatedViaApplyFactory=true)

	private def createTestCasesWith(dutCreatedViaApplyFactory: Boolean) = Array(
		new MultiMasterSingleSlaveArbiterSimulationTest(numberOfMasters=1, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new MultiMasterSingleSlaveArbiterSimulationTest(numberOfMasters=2, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new MultiMasterSingleSlaveArbiterSimulationTest(numberOfMasters=3, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new MultiMasterSingleSlaveArbiterSimulationTest(numberOfMasters=16, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory))
}
