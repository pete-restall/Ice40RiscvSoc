package uk.co.lophtware.msfreference.tests.simulation

import org.scalatest.TestSuite
import spinal.core._

trait NonSimulationFixture extends SimulationFixture[NonSimulationFixture.DummyComponent] { this: TestSuite =>
	protected override def dutFactory(): NonSimulationFixture.DummyComponent = new NonSimulationFixture.DummyComponent()
}

object NonSimulationFixture {
	case class DummyComponent() extends Component {
	}
}
