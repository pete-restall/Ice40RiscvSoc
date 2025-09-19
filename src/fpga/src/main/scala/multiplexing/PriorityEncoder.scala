package net.restall.ice40riscvsoc.multiplexing

import spinal.core._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._

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
	case class IoBundle(private val numberOfInputs: Int) extends Encoder.IoBundle(numberOfInputs) {
	}

	def apply(highestPriorityInput: Bool, otherInputs: Bool*): PriorityEncoder = {
		highestPriorityInput.mustNotBeNull("highestPriorityInput", "At least one input must be specified")
		otherInputs.mustNotContainNull("otherInputs", "All inputs must be specified")

		val encoder = new PriorityEncoder(otherInputs.length + 1)
		encoder.io.inputs <> Vec(highestPriorityInput +: otherInputs)
		encoder
	}
}
