package net.restall.ice40riscvsoc.multiplexing

import spinal.core._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.ValueBitWidthExtensions._

object Encoder {
	class IoBundle(numberOfInputs: Int) extends Bundle {
		if (numberOfInputs < 1) {
			throw numberOfInputs.isOutOfRange("numberOfInputs", "Number of inputs must be at least 1")
		}

		val inputs = in Vec(Bool, numberOfInputs)
		val output = out UInt(numberOfInputs.toCombinationalBitWidth)
		val isValid = out Bool()
	}
}
