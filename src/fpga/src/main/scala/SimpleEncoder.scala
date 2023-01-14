package uk.co.lophtware.msfreference

import spinal.core._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.ValueBitWidthExtensions._

class SimpleEncoder(numberOfInputs: Int) extends Component {
	val io = new SimpleEncoder.IoBundle(numberOfInputs)
	noIoPrefix()

	private val zeroes = Array.fill(numberOfInputs)('0').mkString

	switch(io.inputs.asBits(0 until numberOfInputs)) {
		for (index <- 0 until numberOfInputs) {
			is(bitmaskFor(index)) {
				io.output := index
				io.isValid := True
			}
		}

		default {
			io.output := 0
			io.isValid := False
		}
	}

	private def bitmaskFor(index: Int) = MaskedLiteral(s"${msbZeroesFor(index)}1${lsbZeroesFor(index)}")

	private def msbZeroesFor(index: Int) = zeroes.substring(0, numberOfInputs - index - 1)

	private def lsbZeroesFor(index: Int) = zeroes.substring(0, index)
}

object SimpleEncoder {
	case class IoBundle(private val numberOfInputs: Int) extends Bundle {
		if (numberOfInputs < 1) {
			throw numberOfInputs.isOutOfRange("numberOfInputs", "Number of inputs must be at least 1")
		}

		val inputs = in Vec(Bool, numberOfInputs)
		val output = out UInt(numberOfInputs.toCombinationalBitWidth)
		val isValid = out Bool()
	}

	def apply(firstInput: Bool, otherInputs: Bool*): SimpleEncoder = {
		firstInput.mustNotBeNull("firstInput", "At least one input must be specified")
		otherInputs.mustNotContainNull("otherInputs", "All inputs must be specified")

		val encoder = new SimpleEncoder(otherInputs.length + 1)
		encoder.io.inputs <> Vec(firstInput +: otherInputs)
		encoder
	}
}
