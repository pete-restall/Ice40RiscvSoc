package uk.co.lophtware.msfreference.vendor.lattice.ice40

import spinal.core._

private class Ice40NativeEbram4k(readWidth: BitCount, writeWidth: BitCount) extends BlackBox {
	val io = new Ice40NativeEbram4k.IoBundle()

	noIoPrefix()
	setDefinitionName("PDP4K")
	addGenerics(
		("DATA_WIDTH_R" -> readWidth.value.toString),
		("DATA_WIDTH_W" -> writeWidth.value.toString))
}

private object Ice40NativeEbram4k {
	case class IoBundle() extends Bundle {
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
