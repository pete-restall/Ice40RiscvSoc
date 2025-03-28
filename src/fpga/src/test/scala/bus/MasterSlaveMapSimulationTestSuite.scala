package uk.co.lophtware.msfreference.tests.bus

import org.scalatest.flatspec._
import org.scalatest.Suite

import uk.co.lophtware.msfreference.bus.MasterSlaveMap

class MasterSlaveMapSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] =
		createTestCasesWith(dutCreatedViaApplyFactory=false) ++
		createTestCasesWith(dutCreatedViaApplyFactory=true)

	private def createTestCasesWith(dutCreatedViaApplyFactory: Boolean) = Array(
		new MasterSlaveMapSimulationTest(
			numberOfMasters=1,
			numberOfSlaves=1,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new MasterSlaveMapSimulationTest(
			numberOfMasters=1,
			numberOfSlaves=2,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new MasterSlaveMapSimulationTest(
			numberOfMasters=1,
			numberOfSlaves=3,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new MasterSlaveMapSimulationTest(
			numberOfMasters=2,
			numberOfSlaves=1,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new MasterSlaveMapSimulationTest(
			numberOfMasters=2,
			numberOfSlaves=2,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new MasterSlaveMapSimulationTest(
			numberOfMasters=2,
			numberOfSlaves=3,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory))
}
