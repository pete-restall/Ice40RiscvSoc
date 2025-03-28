package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.util.Random

import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram.direct.{EbramGiven, EbramStateMachineBuilder}
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class Ice40Ebram4kInitialisationFixture(readWidth: BitCount, writeWidth: BitCount, numberOfInitialisedCells: Option[Int]) extends Component {
	readWidth.mustNotBeNull("readWidth")
	writeWidth.mustNotBeNull("writeWidth")

	val io = new Ice40Ebram4k.IoBundle(readWidth, writeWidth)

	val initialContents = numberOfInitialisedCells.map(Array.fill(_) { Random.nextInt(1 << readWidth.value) })
	private val dut = Ice40Ebram4k(readWidth, writeWidth, initialContents.map(_.toSeq))
	io <> dut.io

	private val readClockDomain = ClockDomain(clock=io.CKR, clockEnable=io.CER)
	private val writeClockDomain = ClockDomain(clock=io.CKW, clockEnable=io.CEW)

	def given = new EbramGiven(new EbramStateMachineBuilder(io, writeMask=0, factoryStack=List[Sampling => WithNextSampling]()))

	def wireStimuliUsing(initialState: Sampling) = {
		initialState.mustNotBeNull("initialState")

		var state = initialState
		readClockDomain.withRevertedClockEdge.onSamplings { state = state.onSampling() }
		readClockDomain.forkStimulus(period=10)
		writeClockDomain.forkStimulus(period=10)
		SimulationFixtureBase.waitForExplicitSimulationTermination
	}

	private val numberOfCells = 4096 / readWidth.value

	private val zeroesForRemainder = Array.fill(numberOfCells - numberOfInitialisedCells.getOrElse(0))(0.toInt)

	val expectedBlockContents = initialContents.map(_ ++ zeroesForRemainder).getOrElse(zeroesForRemainder)
}
