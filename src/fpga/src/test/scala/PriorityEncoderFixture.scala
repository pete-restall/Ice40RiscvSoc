package uk.co.lophtware.msfreference.tests

import spinal.core._

import uk.co.lophtware.msfreference.PriorityEncoder

class PriorityEncoderFixture(numberOfInputs: Int) extends Component {
	val io = new PriorityEncoder.IoBundle(numberOfInputs)
	private val dut = new PriorityEncoder(numberOfInputs)
	io <> dut.io
}
