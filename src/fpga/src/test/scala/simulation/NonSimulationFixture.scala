package uk.co.lophtware.msfreference.tests.simulation

import org.scalatest.TestSuite
import spinal.core._
import spinal.core.sim._

trait NonSimulationFixture {
	protected def spinalContext(test: () => Any) = {// TODO: NULL CHECKS FOR test
		NonSimulationFixture.dummySim.doSim { dut =>
			test()
			simSuccess()
		}
	}
}

object NonSimulationFixture {
	case class DummyComponent() extends Component {
	}

	private lazy val dummySim = LightweightSimulationFixture.createSimulation().compile(new DummyComponent())
}
