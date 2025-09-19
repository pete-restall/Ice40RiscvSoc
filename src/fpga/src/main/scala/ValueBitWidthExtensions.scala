package net.restall.ice40riscvsoc

import spinal.core._

object ValueBitWidthExtensions {
	implicit class IntBitWidthExtensions(value: Int) {
		def toCombinationalBitWidth = {
			if (value < 1) {
				throw new IllegalArgumentException(s"Value must be at least 1, ie. there must be at least one combination; arg=value, value=${value}")
			}

			Math.max(1, log2Up(value)) bits
		}
	}
}
