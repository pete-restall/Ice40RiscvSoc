package uk.co.lophtware.msfreference.vendor.lattice.ice40

import spinal.core._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._

private class Ice40NativeEbram4k(readWidth: BitCount, writeWidth: BitCount, initialRows: Option[Seq[Seq[Byte]]] = None) extends BlackBox {
	val io = new Ice40NativeEbram4k.IoBundle()

	noIoPrefix()
	setDefinitionName("PDP4K")
	addGenerics(
		("DATA_WIDTH_R" -> readWidth.value.toString),
		("DATA_WIDTH_W" -> writeWidth.value.toString))

	initialRows.map(row => addGenerics(row.zipWithIndex.map { case(bytes, rowIndex) =>
		("INITVAL_" + nybbleToHex(rowIndex.toByte) -> bytesToHex(bytes))
	}:_*))

	private def nybbleToHex(byte: Byte) = ((if (byte < 10) '0' else 'A' - 10) + byte).toChar

	private def bytesToHex(bytes: Seq[Byte]) = "0x" + String.valueOf(bytes.map(byteToHex).reverse.flatten.toArray)

	private def byteToHex(byte: Byte) = Seq(nybbleToHex(upperNybble(byte)), nybbleToHex(lowerNybble(byte)))

	private def upperNybble(byte: Byte) = ((byte >> 4) & 0x0f).toByte

	private def lowerNybble(byte: Byte) = ((byte >> 0) & 0x0f).toByte
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
