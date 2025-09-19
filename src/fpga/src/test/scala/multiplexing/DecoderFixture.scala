package net.restall.ice40riscvsoc.tests.multiplexing

import spinal.core._

import net.restall.ice40riscvsoc.multiplexing.Decoder

class DecoderFixture(inputWidth: BitCount, dutCreatedViaApplyFactory: Boolean) extends Component {
	val io = new Decoder.IoBundle(inputWidth)
	private val dut = createAndWireDut()

	private def createAndWireDut() = if (dutCreatedViaApplyFactory) createWiredDutViaApplyFactory() else createAndWireDutManually()

	private def createWiredDutViaApplyFactory() = {
		val dut = Decoder(io.input)
		io.outputs := dut.io.outputs
		dut
	}

	private def createAndWireDutManually() = {
		val dut = new Decoder(inputWidth)
		io <> dut.io
		dut
	}
}
