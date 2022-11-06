package uk.co.lophtware.msfreference.vendor.lattice.ice40

import spinal.core._

class Ice40Ebram4k(readWidth: BitCount, writeWidth: BitCount) extends Component {
	val io = new Ice40Ebram4k.IoBundle(readWidth, writeWidth) // TODO: THE BUNDLE NEEDS TO BE MAPPED TO THE *ACTUAL* INPUTS, A NativeIoBundle...

	private val native = new NativeIce40Ebram4k(readWidth, writeWidth)
	native.io.ADW := io.ADW
	native.io.ADR := io.ADR
	native.io.CKW := io.CKW
	native.io.CKR := io.CKR
	native.io.CEW := io.CEW
	native.io.CER := io.CER
	native.io.RE := io.RE
	native.io.WE := io.WE
	native.io.MASK_N := io.MASK_N // TODO: FORCE MASK_N TO ALL ZERO IF writeWidth != 16

	if (readWidth.value == 8 && writeWidth.value == 8) { // TODO: READ WIDTH AND WRITE WIDTH ARE INDEPENDENT
		native.io.DI(15) := False
		native.io.DI(14) := io.DI(7)
		native.io.DI(13) := False
		native.io.DI(12) := io.DI(6)
		native.io.DI(11) := False
		native.io.DI(10) := io.DI(5)
		native.io.DI(9) := False
		native.io.DI(8) := io.DI(4)
		native.io.DI(7) := False
		native.io.DI(6) := io.DI(3)
		native.io.DI(5) := False
		native.io.DI(4) := io.DI(2)
		native.io.DI(3) := False
		native.io.DI(2) := io.DI(1)
		native.io.DI(1) := False
		native.io.DI(0) := io.DI(0)

		io.DO(7) := native.io.DO(14)
		io.DO(6) := native.io.DO(12)
		io.DO(5) := native.io.DO(10)
		io.DO(4) := native.io.DO(8)
		io.DO(3) := native.io.DO(6)
		io.DO(2) := native.io.DO(4)
		io.DO(1) := native.io.DO(2)
		io.DO(0) := native.io.DO(0)
	} else if (readWidth.value == 4 && writeWidth.value == 4) {
		native.io.DI(15) := False
		native.io.DI(14) := False
		native.io.DI(13) := io.DI(3)
		native.io.DI(12) := False
		native.io.DI(11) := False
		native.io.DI(10) := False
		native.io.DI(9) := io.DI(2)
		native.io.DI(8) := False
		native.io.DI(7) := False
		native.io.DI(6) := False
		native.io.DI(5) := io.DI(1)
		native.io.DI(4) := False
		native.io.DI(3) := False
		native.io.DI(2) := False
		native.io.DI(1) := io.DI(0)
		native.io.DI(0) := False

		io.DO(3) := native.io.DO(13)
		io.DO(2) := native.io.DO(9)
		io.DO(1) := native.io.DO(5)
		io.DO(0) := native.io.DO(1)
	} else if (readWidth.value == 2 && writeWidth.value == 2) {
		native.io.DI(15) := False
		native.io.DI(14) := False
		native.io.DI(13) := False
		native.io.DI(12) := False
		native.io.DI(11) := io.DI(1)
		native.io.DI(10) := False
		native.io.DI(9) := False
		native.io.DI(8) := False
		native.io.DI(7) := False
		native.io.DI(6) := False
		native.io.DI(5) := False
		native.io.DI(4) := False
		native.io.DI(3) := io.DI(0)
		native.io.DI(2) := False
		native.io.DI(1) := False
		native.io.DI(0) := False

		io.DO(1) := native.io.DO(11)
		io.DO(0) := native.io.DO(3)
	} else {
		native.io.DI := io.DI
		io.DO := native.io.DO
	}

	noIoPrefix()
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
}

private class NativeIce40Ebram4k(readWidth: BitCount, writeWidth: BitCount) extends BlackBox {
	val io = new NativeIce40Ebram4k.IoBundle()

	noIoPrefix()
	setDefinitionName("PDP4K")
	addGenerics(
		("DATA_WIDTH_R" -> readWidth.value.toString),
		("DATA_WIDTH_W" -> writeWidth.value.toString))
}

private object NativeIce40Ebram4k {
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
