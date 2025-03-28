package uk.co.lophtware.msfreference.tests

import org.scalatest.flatspec._
import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.MsfReference
import uk.co.lophtware.msfreference.tests.simulation._

class MsfReferenceSimulationTest extends AnyFlatSpec with SimulationFixture[MsfReferenceFixture] {
	protected override def dutFactory() = new MsfReferenceFixture()

	"MsfReference" must "run" in simulator { fixture =>
		fixture.wireStimuli()
		fixture.clockDomain.waitSampling(1000)
	}
}

class MsfReferenceFixture extends Component {
	val io = new Bundle {
		val p23 = out Bool()
		val ledR = out Bool()
		val ledG = out Bool()
		val ledB = out Bool()
	}

	private val dut = new MsfReference()
	dut.io <> io

	def wireStimuli() = {
		clockDomain.assertReset()
		clockDomain.forkStimulus(period=10)
		clockDomain.waitEdge(3)
		clockDomain.deassertReset()
		SimulationFixtureBase.waitForExplicitSimulationTermination
	}
}
