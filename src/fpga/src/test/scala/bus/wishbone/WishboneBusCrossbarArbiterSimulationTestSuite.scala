package uk.co.lophtware.msfreference.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.Suite
import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusMasterSlaveMap

class WishboneBusCrossbarArbiterSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] =
		createTestCasesWith(dutCreatedViaApplyFactory=false) ++
		createTestCasesWith(dutCreatedViaApplyFactory=true)

	private def createTestCasesWith(dutCreatedViaApplyFactory: Boolean): IndexedSeq[WishboneBusCrossbarArbiterSimulationTest] =
		createTestCasesWith(dutCreatedViaApplyFactory, WishboneConfigTestDoubles.stubWith(addressWidth=16, selWidth=4)) ++
		createTestCasesWith(dutCreatedViaApplyFactory, WishboneConfigTestDoubles.stubWith(addressWidth=17, selWidth=0)) ++
		createTestCasesWith(dutCreatedViaApplyFactory, WishboneConfigTestDoubles.stubWith(addressWidth=18, useSTALL=true, useERR=true, useRTY=true))

	private def createTestCasesWith(dutCreatedViaApplyFactory: Boolean, busConfig: WishboneConfig): IndexedSeq[WishboneBusCrossbarArbiterSimulationTest] = Array(
		createTestCaseWith(numberOfMasters=1, numberOfSlaves=1, busConfig, dutCreatedViaApplyFactory),
		createTestCaseWith(numberOfMasters=1, numberOfSlaves=2, busConfig, dutCreatedViaApplyFactory),
		createTestCaseWith(numberOfMasters=2, numberOfSlaves=1, busConfig, dutCreatedViaApplyFactory),
		createTestCaseWith(numberOfMasters=2, numberOfSlaves=2, busConfig, dutCreatedViaApplyFactory),
		createTestCaseWith(numberOfMasters=2, numberOfSlaves=3, busConfig, dutCreatedViaApplyFactory),
		createTestCaseWith(numberOfMasters=3, numberOfSlaves=1, busConfig, dutCreatedViaApplyFactory),
		createTestCaseWith(numberOfMasters=3, numberOfSlaves=2, busConfig, dutCreatedViaApplyFactory),
		createTestCaseWith(numberOfMasters=7, numberOfSlaves=5, busConfig, dutCreatedViaApplyFactory))

	private def createTestCaseWith(numberOfMasters: Int, numberOfSlaves: Int, busConfig: WishboneConfig, dutCreatedViaApplyFactory: Boolean) =
		new WishboneBusCrossbarArbiterSimulationTest(
			numberOfMasters,
			numberOfSlaves,
			createBusMapFor(numberOfMasters, numberOfSlaves, busConfig),
			dutCreatedViaApplyFactory)

	private def createBusMapFor(numberOfMasters: Int, numberOfSlaves: Int, busConfig: WishboneConfig) = {
		val masters = Seq.fill(numberOfMasters) { WishboneTestDoubles.stubMasterWith(busConfig) }
		val slaves = Seq.fill(numberOfSlaves) { WishboneTestDoubles.stubSlaveWith(busConfig) }
		val allCombinations = for (master <- masters.zipWithIndex; slave <- slaves.zipWithIndex) yield { (
			master._1,
				(bus: Wishbone) =>
					bus.ADR === WishboneBusCrossbarArbiterFixture.AllSlavesSelected ||
					bus.ADR === WishboneBusCrossbarArbiterFixture.addressOf(master._2, slave._2),
			slave._1
		) }

		WishboneBusMasterSlaveMap(allCombinations.head, allCombinations.tail:_*)
	}
}
