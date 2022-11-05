package uk.co.lophtware.msfreference.vendor.lattice.ice40

import spinal.core._

class Ice40Ebram4k(readWidth: BitCount, writeWidth: BitCount) extends BlackBox {
	val io = new Ice40Ebram4k.IoBundle(readWidth, writeWidth) // TODO: THE BUNDLE NEEDS TO BE MAPPED TO THE *ACTUAL* INPUTS, A NativeIoBundle...

	// TODO: FORCE MASK_N TO ALL ZERO IF writeWidth != 16
	noIoPrefix()
	setDefinitionName("PDP4K")
	addGenerics(
		("DATA_WIDTH_R" -> readWidth.value),
		("DATA_WIDTH_W" -> writeWidth.value))
}

object Ice40Ebram4k {
	case class IoBundle(readWidth: BitCount, writeWidth: BitCount) extends Bundle {
		val DI = in Bits(writeWidth) // TODO: THIS NEEDS MAPPING DEPENDING ON THE WIDTH (2, 4 OR 8 BITS DOES NOT USE CONTIGUOUS BITS OF THE WORD)
		val ADW = in UInt(11 bits) // TODO: DYNAMICALLY ALTER WIDTH BASED ON writeWidth - AND TEST FOR THIS...
		val ADR = in UInt(11 bits) // TODO: DYNAMICALLY ALTER WIDTH BASED ON readWidth - AND TEST FOR THIS...
		val CKW = in Bool()
		val CKR = in Bool()
		val CEW = in Bool()
		val CER = in Bool()
		val RE = in Bool()
		val WE = in Bool()
		val MASK_N = in Bits(16 bits) // TODO: CONDITIONALLY EXPOSE THIS ONLY *IFF* writeWidth == 16 bits (AND TEST FOR IT, OBVIOUSLY); val MASK_N = (writeWidth == 16 bits) generate (in Bits(16 bits))
		val DO = out Bits(readWidth) // TODO: THIS NEEDS MAPPING DEPENDING ON THE WIDTH (2, 4 OR 8 BITS DOES NOT USE CONTIGUOUS BITS OF THE WORD)
	}

	private case class NativeIoBundle() extends Bundle {
		val DI = in Bits(16 bits)
		val ADW = in UInt(11 bits)
		val ADR = in UInt(11 bits)
		val CKW = in Bool()
		val CKR = in Bool()
		val CEW = in Bool()
		val CER = in Bool()
		val RE = in Bool()
		val WE = in Bool()
		val MASK_N = in Bits(16 bits)
		val DO = out Bits(16 bits)
	}
}
