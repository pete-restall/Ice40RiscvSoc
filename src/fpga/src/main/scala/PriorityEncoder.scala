package uk.co.lophtware.msfreference

import spinal.core._

import uk.co.lophtware.msfreference.ValueBitWidthExtensions._

class PriorityEncoder(numberOfInputs: Int) extends Component {
	val io = new PriorityEncoder.IoBundle(numberOfInputs)
	noIoPrefix()

	private val dontCares = Array.fill(numberOfInputs)('-').mkString
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

	private def bitmaskFor(index: Int) = MaskedLiteral(s"${dontCaresFor(index)}1${zeroesFor(index)}")

	private def dontCaresFor(index: Int) = dontCares.substring(0, numberOfInputs - index - 1)

	private def zeroesFor(index: Int) = zeroes.substring(0, index)
}

object PriorityEncoder {
	case class IoBundle(private val numberOfInputs: Int) extends Bundle {
		if (numberOfInputs < 1) {
			throw new IllegalArgumentException(s"Number of inputs must be at least 1; arg=numberOfInputs, value=${numberOfInputs}")
		}

		val inputs = in Vec(Bool, numberOfInputs)
		val output = out UInt(numberOfInputs.toCombinationalBitWidth)
		val isValid = out Bool()
	}

	def apply(highestPriorityInput: Bool, otherInputs: Bool*): PriorityEncoder = {
		if (highestPriorityInput == null) {
			throw new IllegalArgumentException("At least one input must be specified; arg=highestPriorityInput, value=null")
		}

		val indexOfNull = otherInputs.indexOf(null)
		if (indexOfNull >= 0) {
			throw new IllegalArgumentException(s"Inputs must all be specified; arg=otherInputs, value=null, index=${indexOfNull}")
		}

		val encoder = new PriorityEncoder(otherInputs.length + 1)
		encoder.io.inputs <> Vec(highestPriorityInput +: otherInputs)
		encoder
	}
}
