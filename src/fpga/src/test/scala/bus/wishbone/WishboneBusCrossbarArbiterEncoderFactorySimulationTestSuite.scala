package net.restall.ice40riscvsoc.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.Suite

class WishboneBusCrossbarArbiterEncoderFactorySimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] = Array(
		createTestCaseWith(numberOfMasters=1, numberOfSlaves=1),
		createTestCaseWith(numberOfMasters=2, numberOfSlaves=1),
		createTestCaseWith(numberOfMasters=2, numberOfSlaves=5),
		createTestCaseWith(numberOfMasters=11, numberOfSlaves=32))

	private def createTestCaseWith(numberOfMasters: Int, numberOfSlaves: Int) =
		new WishboneBusCrossbarArbiterEncoderFactorySimulationTest(numberOfMasters, numberOfSlaves)
}
