package net.restall.ice40riscvsoc.tests

import org.scalatest.flatspec._
import spinal.core._
import spinal.core.sim._

import net.restall.ice40riscvsoc.IceSugarDevBoard
import net.restall.ice40riscvsoc.tests.simulation._

class IceSugarDevBoardSimulationTest extends AnyFlatSpec with SimulationFixture[IceSugarDevBoardFixture] {
	protected override def dutFactory() = new IceSugarDevBoardFixture()

	"IceSugarDevBoard" must "run" in simulator { fixture =>
		fixture.wireStimuli()
		fixture.clockDomain.waitSampling(1000)
	}
}

class IceSugarDevBoardFixture extends Component {
	val io = new Bundle {
		val p23 = out Bool()
		val ledR = out Bool()
		val ledG = out Bool()
		val ledB = out Bool()
	}

	private val dut = new IceSugarDevBoard()
	dut.io <> io

	def wireStimuli() = {
		clockDomain.assertReset()
		clockDomain.forkStimulus(period=10)
		clockDomain.waitEdge(3)
		clockDomain.deassertReset()
		SimulationFixtureBase.waitForExplicitSimulationTermination
	}
}
