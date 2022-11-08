package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import org.scalatest.flatspec._
import org.scalatest.matchers.must._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._

import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class Ice40Ebram4kTest extends AnyFlatSpec with Matchers with TableDrivenPropertyChecks {
	private val validWidths = List(2 bits, 4 bits, 8 bits, 16 bits)
	private val validWidthCombinations = tableFor(("readWidth", "writeWidth"), for {
		readWidth <- validWidths
		writeWidth <- validWidths
	} yield (readWidth, writeWidth))

	private def tableFor[A](headers: (String, String), values: Iterable[(A, A)]) = Table(headers) ++ values

	"PDP4K" must "be able to set read and write widths independently" in {
		forAll(validWidthCombinations) { (readWidth: BitCount, writeWidth: BitCount) =>
			val ebram = new Ice40Ebram4k(readWidth, writeWidth)
			ebram.io.DO.getWidth must be(readWidth.value)
			ebram.io.DI.getWidth must be(writeWidth.value)
		}
	}

	private val invalidWidths = tableFor("invalidWidth", List(0 bits, 1 bits, 3 bits, 5 bits, 12 bits, 17 bits, 32 bits))

	private def tableFor[A](headers: (String), values: Iterable[A]) = Table(headers) ++ values

	it must "not accept invalid read widths" in {
		forAll(invalidWidths) { (invalidReadWidth: BitCount) => {
			val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(invalidReadWidth, 16 bits))
			thrown.getMessage must include("arg=readWidth")
		}}
	}

	it must "not accept invalid write widths" in {
		forAll(invalidWidths) { (invalidWriteWidth: BitCount) => {
			val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(16 bits, invalidWriteWidth))
			thrown.getMessage must include("arg=writeWidth")
		}}
	}

	it must "have an 8-bit address for a 16-bit data width (read)" in {
		val ebram = new Ice40Ebram4k(16 bits, 4 bits)
		ebram.io.ADR.getWidth must be(8)
	}

	it must "have an 8-bit address for a 16-bit data width (write)" in {
		val ebram = new Ice40Ebram4k(4 bits, 16 bits)
		ebram.io.ADW.getWidth must be(8)
	}

	it must "have a 9-bit address for an 8-bit data width (read)" in {
		val ebram = new Ice40Ebram4k(8 bits, 16 bits)
		ebram.io.ADR.getWidth must be(9)
	}

	it must "have a 9-bit address for an 8-bit data width (write)" in {
		val ebram = new Ice40Ebram4k(16 bits, 8 bits)
		ebram.io.ADW.getWidth must be(9)
	}

	it must "have a 10-bit address for a 4-bit data width (read)" in {
		val ebram = new Ice40Ebram4k(4 bits, 16 bits)
		ebram.io.ADR.getWidth must be(10)
	}

	it must "have a 10-bit address for a 4-bit data width (write)" in {
		val ebram = new Ice40Ebram4k(16 bits, 4 bits)
		ebram.io.ADW.getWidth must be(10)
	}

	it must "have an 11-bit address for a 2-bit data width (read)" in {
		val ebram = new Ice40Ebram4k(2 bits, 16 bits)
		ebram.io.ADR.getWidth must be(11)
	}

	it must "have an 11-bit address for a 2-bit data width (write)" in {
		val ebram = new Ice40Ebram4k(16 bits, 2 bits)
		ebram.io.ADW.getWidth must be(11)
	}

	it must "have 16 MASK_N bits if write width is 16 bits" in {
		val ebram = new Ice40Ebram4k(8 bits, 16 bits)
		ebram.io.MASK_N.get.getWidth must be(16)
	}

	private val widthsExcluding16Bits = tableFor("width", validWidths.filter(width => width.value != 16))

	it must "have have no MASK_N if write width is not 16 bits" in {
		forAll(widthsExcluding16Bits) { (writeWidth: BitCount) =>
			val ebram = new Ice40Ebram4k(16 bits, writeWidth)
			ebram.io.MASK_N must be(None)
		}
	}
}
