package net.restall.ice40riscvsoc.tests.multiplexing

import spinal.core._

import net.restall.ice40riscvsoc.multiplexing.SimpleEncoder

class SimpleEncoderFixture(numberOfInputs: Int, dutCreatedViaApplyFactory: Boolean) extends Component {
	val io = new SimpleEncoder.IoBundle(numberOfInputs)
	private val dut = createAndWireDut()

	private def createAndWireDut() = if (dutCreatedViaApplyFactory) createWiredDutViaApplyFactory() else createAndWireDutManually()

	private def createWiredDutViaApplyFactory() = {
		val dut = SimpleEncoder(io.inputs.head, io.inputs.tail.toSeq:_*)
		io.isValid := dut.io.isValid
		io.output := dut.io.output
		dut
	}

	private def createAndWireDutManually() = {
		val dut = new SimpleEncoder(numberOfInputs)
		io <> dut.io
		dut
	}
}
