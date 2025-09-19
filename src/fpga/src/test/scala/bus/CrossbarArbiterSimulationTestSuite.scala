package net.restall.ice40riscvsoc.tests.bus

import org.scalatest.flatspec._
import org.scalatest.Suite

class CrossbarArbiterSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] = Array(
		new CrossbarArbiterSimulationTest(numberOfMasters=1, numberOfSlaves=1),
		new CrossbarArbiterSimulationTest(numberOfMasters=1, numberOfSlaves=2),
		new CrossbarArbiterSimulationTest(numberOfMasters=2, numberOfSlaves=1),
		new CrossbarArbiterSimulationTest(numberOfMasters=2, numberOfSlaves=2),
		new CrossbarArbiterSimulationTest(numberOfMasters=2, numberOfSlaves=3),
		new CrossbarArbiterSimulationTest(numberOfMasters=3, numberOfSlaves=3),
		new CrossbarArbiterSimulationTest(numberOfMasters=3, numberOfSlaves=5),
		new CrossbarArbiterSimulationTest(numberOfMasters=7, numberOfSlaves=4))
}
