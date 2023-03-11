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
		anyseq(io.driver.transaction.readWriteStrobe.ready)
		this
	}
}
