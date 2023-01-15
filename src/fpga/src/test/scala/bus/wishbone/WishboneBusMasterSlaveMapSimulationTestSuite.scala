package uk.co.lophtware.msfreference.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.Suite

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusMasterSlaveMap

class WishboneBusMasterSlaveMapSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] =
		createTestCasesWith(dutCreatedViaApplyFactory=false) ++
		createTestCasesWith(dutCreatedViaApplyFactory=true)

	private def createTestCasesWith(dutCreatedViaApplyFactory: Boolean) = Array(
		new WishboneBusMasterSlaveMapSimulationTest(
			numberOfMasters=1,
			numberOfSlaves=1,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusMasterSlaveMapSimulationTest(
			numberOfMasters=1,
			numberOfSlaves=2,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusMasterSlaveMapSimulationTest(
			numberOfMasters=1,
			numberOfSlaves=3,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusMasterSlaveMapSimulationTest(
			numberOfMasters=2,
			numberOfSlaves=1,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusMasterSlaveMapSimulationTest(
			numberOfMasters=2,
			numberOfSlaves=2,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusMasterSlaveMapSimulationTest(
			numberOfMasters=2,
			numberOfSlaves=3,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory))
}
