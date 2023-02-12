package uk.co.lophtware.msfreference.tests.simulation

import org.scalatest.TestSuite
import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._

trait NonSimulationFixture {
	protected def spinalContext(test: => Any) = {
		NonSimulationFixture.dummySim.doSim { dut =>
			dut.mustNotBeNull("dut")
			new Component { test }
			simSuccess()
		}
	}
}

object NonSimulationFixture {
	case class DummyComponent() extends Component {
	}

	private lazy val dummySim = LightweightSimulationFixture.createSimulation().compile(new DummyComponent())
}
