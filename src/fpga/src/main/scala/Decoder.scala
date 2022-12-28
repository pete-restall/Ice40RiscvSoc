package uk.co.lophtware.msfreference

import spinal.core._

class Decoder(inputWidth: BitCount) extends Component {
	val io = new Decoder.IoBundle(inputWidth)
	io.outputs.zipWithIndex.foreach { case(output, index) => output := (io.input === index) }
}

object Decoder {
	case class IoBundle(private val inputWidth: BitCount) extends Bundle {
		if (inputWidth == null) {
			throw new IllegalArgumentException("Input width must be specified; arg=inputWidth, value=null")
		}

		if (inputWidth.value < 1) {
			throw new IllegalArgumentException(s"Input width must be at least 1 bit; arg=inputWidth, value=${inputWidth}")
		}

		val input = in UInt(inputWidth)
		val outputs = out Vec(Bool, 1 << inputWidth.value)
	}

	def apply(input: UInt): Decoder = {
		if (input == null) {
			throw new IllegalArgumentException("Input must be specified; arg=input, value=null")
		}

		val decoder = new Decoder(input.getWidth bits)
		decoder.io.input := input
		decoder
	}
}
