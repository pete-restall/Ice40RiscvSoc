package uk.co.lophtware.msfreference.vendor.lattice.ice40

import spinal.core._

class Ice40Ebram4k(readWidth: BitCount, writeWidth: BitCount) extends Component {
	val io = new Ice40Ebram4k.IoBundle(readWidth, writeWidth)

	private val native = new Ice40NativeEbram4k(readWidth, writeWidth)
	native.io.ADW := io.ADW.resized
	native.io.ADR := io.ADR.resized
	native.io.CKW := io.CKW
	native.io.CKR := io.CKR
	native.io.CEW := io.CEW
	native.io.CER := io.CER
	native.io.RE := io.RE
	native.io.WE := io.WE
	native.io.MASK_N := io.MASK_N.getOrElse(B(0))

	if (writeWidth.value == 8) {
		native.io.DI := (
			14 -> io.DI(7),
			12 -> io.DI(6),
			10 -> io.DI(5),
			8 -> io.DI(4),
			6 -> io.DI(3),
			4 -> io.DI(2),
			2 -> io.DI(1),
			0 -> io.DI(0),
			default -> False)
	}

	if (readWidth.value == 8) {
		io.DO := (
			7 -> native.io.DO(14),
			6 -> native.io.DO(12),
			5 -> native.io.DO(10),
			4 -> native.io.DO(8),
			3 -> native.io.DO(6),
			2 -> native.io.DO(4),
			1 -> native.io.DO(2),
			0 -> native.io.DO(0))

	}

	if (writeWidth.value == 4)
		native.io.DI := (13 -> io.DI(3), 9 -> io.DI(2), 5 -> io.DI(1), 1 -> io.DI(0), default -> False)

	if (readWidth.value == 4)
		io.DO := (3 -> native.io.DO(13), 2 -> native.io.DO(9), 1 -> native.io.DO(5), 0 -> native.io.DO(1))

	if (writeWidth.value == 2)
		native.io.DI := (11 -> io.DI(1), 3 -> io.DI(0), default -> False)

	if (readWidth.value == 2)
		io.DO := (1 -> native.io.DO(11), 0 -> native.io.DO(3))

	if (writeWidth.value == 16)
		native.io.DI := io.DI

	if (readWidth.value == 16)
		io.DO := native.io.DO

	noIoPrefix()
}

object Ice40Ebram4k {
	case class IoBundle(readWidth: BitCount, writeWidth: BitCount) extends Bundle {
		val DI = in Bits(writeWidth)
		val ADW = in UInt(addressBusWidthFor("writeWidth", writeWidth))
		val ADR = in UInt(addressBusWidthFor("readWidth", readWidth))
		val CKW = in Bool()
		val CKR = in Bool()
		val CEW = in Bool()
		val CER = in Bool()
		val RE = in Bool()
		val WE = in Bool()
		val MASK_N = if (writeWidth.value == 16) Some(in Bits(16 bits)) else None
		val DO = out Bits(readWidth)

		private def addressBusWidthFor(argName: String, dataBusWidth: BitCount) = dataBusWidth match {
			case BitCount(16) => 8 bits
			case BitCount(8) => 9 bits
			case BitCount(4) => 10 bits
			case BitCount(2) => 11 bits
			case _ => throw new IllegalArgumentException(s"Data bus width must be either 2, 4, 8 or 16 bits; arg=${argName}, value=${dataBusWidth}")
		}
	}
}
