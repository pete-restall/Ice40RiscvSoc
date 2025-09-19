package uk.co.lophtware.msfreference.tests.memory.flashqspi

import spinal.core._
import spinal.core.formal._

import uk.co.lophtware.msfreference.memory.flashqspi.FlashQspiMemoryStateMachine

class FlashQspiMemoryStateMachineVerificationFixture extends Component {
	private val dut = new FlashQspiMemoryStateMachine()
	val io = dut.io

	def withStimuli: this.type = {
		anyseq(io.bitBanger.isChipSelected)
		anyseq(io.bitBanger.isWriteProtected)
		anyseq(io.bitBanger.isQspiRequested)
		anyseq(io.bitBanger.isBitBangingRequested)
		anyseq(io.fastReader.valid)
		anyseq(io.fastReader.payload)
		anyseq(io.driver.transaction.readWriteStrobe.ready)
		this
	}

	def allStatePredicates: Seq[Bool] = dut.states.filter(_ != dut.stateBoot).map(state => isCurrentState(state.name)).toSeq

	def isCurrentState(name: String): Bool = (dut.stateReg === getStateEnumByName(name))

	private def getStateEnumByName(name: String) = dut.enumOf(getStateByName(name))

	private def getStateByName(name: String) = dut.states.find(_.name == name).get

	def isNextState(name: String): Bool = (dut.stateNext === getStateEnumByName(name))
}
