package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram.{EbramGiven, EbramStateMachineBuilder}
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class Ice40Ebram4kFixture(readWidth: BitCount, writeWidth: BitCount) extends Component {
	val io = new Ice40Ebram4k.IoBundle(readWidth, writeWidth)
	private val dut = new Ice40Ebram4k(readWidth, writeWidth)
	io <> dut.io

	private val readClockDomain = ClockDomain(clock=io.CKR, clockEnable=io.CER)
	private val writeClockDomain = ClockDomain(clock=io.CKW, clockEnable=io.CEW)

	def given = new EbramGiven(new EbramStateMachineBuilder(io, writeMask=0, factoryStack=List[Sampling => WithNextSampling]()))

	def wireStimuliUsing(initialState: Sampling) = {
		var state = initialState
		readClockDomain.withRevertedClockEdge.onSamplings { state = state.onSampling() }
		readClockDomain.forkStimulus(period=10)
		writeClockDomain.forkStimulus(period=10)
		SimulationFixtureBase.waitForExplicitSimulationTermination
	}

	def wordToInterleavedBytes(word: Int): Array[Int] = wordToInterleavedQuantity(word, numOutBits=8)

	private def wordToInterleavedQuantity(word: Int, numOutBits: Int): Array[Int] = (
		for (startBit <- 16 - (16 / numOutBits) to 15)
			yield wordToInterleavedQuantity(word, startBit, numOutBits)).toArray

	private def wordToInterleavedQuantity(word: Int, startBit: Int, numOutBits: Int) = {
		var quantity = 0
		var mask = 1 << startBit
		for (i <- (numOutBits - 1) downto 0) {
			quantity = quantity | (if ((word & mask) != 0) 1 << i else 0)
			mask >>= 16 / numOutBits
		}
		quantity
	}

	def wordToInterleavedNybbles(word: Int): Array[Int] = wordToInterleavedQuantity(word, numOutBits=4)

	def wordToInterleavedCrumbs(word: Int): Array[Int] = wordToInterleavedQuantity(word, numOutBits=2)
}
