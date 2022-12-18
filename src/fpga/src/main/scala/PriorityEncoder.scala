package uk.co.lophtware.msfreference

import spinal.core._

class PriorityEncoder(private val numberOfInputs: Int) extends Component {
	val io = new PriorityEncoder.IoBundle(numberOfInputs)

	private val dontCares = Array.fill(numberOfInputs)('-').mkString
	private val zeroes = Array.fill(numberOfInputs)('0').mkString

	switch(io.inputs.asBits) {
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
	case class IoBundle(numberOfInputs: Int) extends Bundle {
		if (numberOfInputs < 2) {
			throw new IllegalArgumentException(s"Number of inputs must be at least 2; arg=numberOfInputs, value=${numberOfInputs}")
		}

		val inputs = in Vec(Bool, numberOfInputs)
		val output = out UInt(log2Up(numberOfInputs) bits)
		val isValid = out Bool()
	}
}
