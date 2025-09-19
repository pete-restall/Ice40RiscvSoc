package net.restall.ice40riscvsoc.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.Suite

class WishboneBusMasterSlaveMapSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] = Array(
		new WishboneBusMasterSlaveMapSimulationTest(numberOfMasters=1, numberOfSlaves=1),
		new WishboneBusMasterSlaveMapSimulationTest(numberOfMasters=1, numberOfSlaves=2),
		new WishboneBusMasterSlaveMapSimulationTest(numberOfMasters=1, numberOfSlaves=3),
		new WishboneBusMasterSlaveMapSimulationTest(numberOfMasters=2, numberOfSlaves=1),
		new WishboneBusMasterSlaveMapSimulationTest(numberOfMasters=2, numberOfSlaves=2),
		new WishboneBusMasterSlaveMapSimulationTest(numberOfMasters=2, numberOfSlaves=3))
}
