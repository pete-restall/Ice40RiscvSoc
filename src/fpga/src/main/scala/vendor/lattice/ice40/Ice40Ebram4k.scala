package net.restall.ice40riscvsoc.vendor.lattice.ice40

import spinal.core._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._

class Ice40Ebram4k(readWidth: BitCount, writeWidth: BitCount, initialRows: Option[Seq[Seq[Byte]]] = None) extends Component {
	initialRows.mustNotBeNull("initialRows", "Use None (or omit the argument entirely) if EBRAM initialisation is unimportant")

	if (initialRows.map(rows => rows.length != 16 || rows.exists(row => row == null || row.length != 32)).getOrElse(false)) {
		throw new IllegalArgumentException("All 16 rows of 32 bytes each must be given when initial contents have been specified; arg=initialRows")
	}

	val io = new Ice40Ebram4k.IoBundle(readWidth, writeWidth)

	private val native = new Ice40NativeEbram4k(readWidth, writeWidth, initialRows)
	native.io.ADW := io.ADW.resized
	native.io.ADR := io.ADR.resized
	native.io.CKW := io.CKW
	native.io.CKR := io.CKR
	native.io.CEW := io.CEW
	native.io.CER := io.CER
	native.io.RE := io.RE // TODO: THIS IS ONLY AVAILABLE FOR 256x16 CONFIGURATIONS - MAKE IT OPTIONAL !
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
	case class IoBundle(private val readWidth: BitCount, private val writeWidth: BitCount) extends Bundle {
		readWidth.mustNotBeNull("readWidth", "Data bus width (read port) must be specified")
		writeWidth.mustNotBeNull("writeWidth", "Data bus width (write port) must be specified")

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
			case _ => throw dataBusWidth.isOutOfRange(argName, "Data bus width must be either 2, 4, 8 or 16 bits")
		}
	}

	def apply(readWidth: BitCount, writeWidth: BitCount, initialContents: Option[Seq[Int]] = None): Ice40Ebram4k = {
		initialContents.mustNotBeNull("initialContents", "Use None (or omit the argument entirely) if EBRAM initialisation is unimportant")
		val ebram = new Ice40Ebram4k(readWidth, writeWidth, initialContents.map(initialContentsToRows(readWidth)))
		ebram
	}

	private def initialContentsToRows(width: BitCount)(cells: Seq[Int]) =
		cellsToFlattenedBytes(cellsPaddedToBlockSize(cells, width), width)
		.grouped(size=32)
		.toSeq

	private def cellsPaddedToBlockSize(cells: Seq[Int], width: BitCount) = cells ++ Seq.fill((4096 / width.value) - cells.length)(0)

	private def cellsToFlattenedBytes(cells: Seq[Int], width: BitCount) = {
		val cellsPerWord = 16 / width.value
		var words = for (n <- 0 until 256) yield {
			var word = 0
			for (i <- 0 until width.value; j <- 0 until cellsPerWord) {
				word |= (if (isBitSet(cells(j * 256 + n), i)) 1 << (j + i * cellsPerWord) else 0)
			}
			word
		}

		words.flatMap(word => Seq(lowerByteOf(word), upperByteOf(word)))
	}

	private def isBitSet(word: Int, bit: Int) = (word & (1 << bit)) != 0

	private def lowerByteOf(word: Int) = ((word >> 0) & 0xff).toByte

	private def upperByteOf(word: Int) = ((word >> 8) & 0xff).toByte
}
