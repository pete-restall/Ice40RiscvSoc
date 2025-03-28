package uk.co.lophtware.msfreference.tests.multiplexing

import spinal.core._

import uk.co.lophtware.msfreference.multiplexing.PriorityEncoder

class PriorityEncoderFixture(numberOfInputs: Int, dutCreatedViaApplyFactory: Boolean) extends Component {
	val io = new PriorityEncoder.IoBundle(numberOfInputs)
	private val dut = createAndWireDut()

	private def createAndWireDut() = if (dutCreatedViaApplyFactory) createWiredDutViaApplyFactory() else createAndWireDutManually()

	private def createWiredDutViaApplyFactory() = {
		val dut = PriorityEncoder(io.inputs.head, io.inputs.tail.toSeq:_*)
		io.isValid := dut.io.isValid
		io.output := dut.io.output
		dut
	}

	private def createAndWireDutManually() = {
		val dut = new PriorityEncoder(numberOfInputs)
		io <> dut.io
		dut
	}
}
