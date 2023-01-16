package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.matchers.must._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._

import uk.co.lophtware.msfreference.tests.IterableTableExtensions._
import uk.co.lophtware.msfreference.tests.simulation.NonSimulationFixture
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class Ice40Ebram4kTest extends AnyFlatSpec with NonSimulationFixture with Matchers with TableDrivenPropertyChecks {
	"Ice40Ebram4k" must "not use the 'io' prefix for signals" in spinalContext { () =>
		val ebram = new Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits)
		ebram.io.name must be("")
	}

	it must "not accept a null readWidth" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(readWidth=null, writeWidth=16 bits))
		thrown.getMessage must (include("arg=readWidth") and include("null"))
	}

	it must "not accept a null writeWidth" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(readWidth=16 bits, writeWidth=null))
		thrown.getMessage must (include("arg=writeWidth") and include("null"))
	}

	it must "not accept null initialRows" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits, initialRows=null))
		thrown.getMessage must (include("arg=initialRows") and include("null"))
	}

	it must "not accept a null inside initialRows" in spinalContext { () =>
		val nullCarryingInitialRows = Some(Random.shuffle(Seq.fill(15) { dummyInitialRow() } :+ null))
		val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits, initialRows=nullCarryingInitialRows))
		thrown.getMessage must (include("arg=initialRows") and include("16 rows"))
	}

	private def dummyInitialRow() = Seq.fill(32)(0.toByte)

	it must "not accept an empty initialRows" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits, initialRows=Some(Seq[Seq[Byte]]())))
		thrown.getMessage must (include("arg=initialRows") and include("16 rows"))
	}

	it must "not accept 15 rows in initialRows" in spinalContext { () =>
		val insufficientIitialRows = Some(Seq.fill(15) { dummyInitialRow() })
		val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits, initialRows=insufficientIitialRows))
		thrown.getMessage must (include("arg=initialRows") and include("16 rows"))
	}

	it must "not accept less than 16 rows in initialRows" in spinalContext { () =>
		val insufficientInitialRows = Some(Seq.fill(Random.between(1, 15)) { dummyInitialRow() })
		val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits, initialRows=insufficientInitialRows))
		thrown.getMessage must (include("arg=initialRows") and include("16 rows"))
	}

	it must "not accept 17 rows in initialRows" in spinalContext { () =>
		val tooManyInitialRows = Some(Seq.fill(17) { dummyInitialRow() })
		val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits, initialRows=tooManyInitialRows))
		thrown.getMessage must (include("arg=initialRows") and include("16 rows"))
	}

	it must "not accept more than 16 rows in initialRows" in spinalContext { () =>
		val tooManyInitialRows = Some(Seq.fill(Random.between(16, 128)) { dummyInitialRow() })
		val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits, initialRows=tooManyInitialRows))
		thrown.getMessage must (include("arg=initialRows") and include("16 rows"))
	}

	it must "not accept empty rows in initialRows" in spinalContext { () =>
		val emptyCarryingInitialRows = Some(Random.shuffle(Seq.fill(15) { dummyInitialRow() } :+ Seq[Byte]()))
		val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits, initialRows=emptyCarryingInitialRows))
		thrown.getMessage must (include("arg=initialRows") and include("32 bytes"))
	}

	it must "not accept 31-byte rows in initialRows" in spinalContext { () =>
		val runtRow = Seq.fill(31)(0.toByte)
		val runtCarryingInitialRows = Some(Random.shuffle(Seq.fill(15) { dummyInitialRow() } :+ runtRow))
		val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits, initialRows=runtCarryingInitialRows))
		thrown.getMessage must (include("arg=initialRows") and include("32 bytes"))
	}

	it must "not accept less than 32-byte rows in initialRows" in spinalContext { () =>
		val runtRow = Seq.fill(Random.between(1, 31))(0.toByte)
		val runtCarryingInitialRows = Some(Random.shuffle(Seq.fill(15) { dummyInitialRow() } :+ runtRow))
		val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits, initialRows=runtCarryingInitialRows))
		thrown.getMessage must (include("arg=initialRows") and include("32 bytes"))
	}

	it must "not accept 33-byte rows in initialRows" in spinalContext { () =>
		val fatRow = Seq.fill(33)(0.toByte)
		val fatCarryingInitialRows = Some(Random.shuffle(Seq.fill(15) { dummyInitialRow() } :+ fatRow))
		val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits, initialRows=fatCarryingInitialRows))
		thrown.getMessage must (include("arg=initialRows") and include("32 bytes"))
	}

	it must "not accept more than 32-byte rows in initialRows" in spinalContext { () =>
		val fatRow = Seq.fill(Random.between(33, 128))(0.toByte)
		val fatCarryingInitialRows = Some(Random.shuffle(Seq.fill(15) { dummyInitialRow() } :+ fatRow))
		val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits, initialRows=fatCarryingInitialRows))
		thrown.getMessage must (include("arg=initialRows") and include("32 bytes"))
	}

	private val validWidths = List(2 bits, 4 bits, 8 bits, 16 bits)
	private val validWidthCombinations = (for {
		readWidth <- validWidths
		writeWidth <- validWidths
	} yield (readWidth, writeWidth)).asTable("readWidth", "writeWidth")

	it must "be able to set read and write widths independently" in spinalContext { () =>
		forAll(validWidthCombinations) { (readWidth: BitCount, writeWidth: BitCount) =>
			val ebram = new Ice40Ebram4k(readWidth, writeWidth)
			ebram.io.DO.getWidth must be(readWidth.value)
			ebram.io.DI.getWidth must be(writeWidth.value)
		}
	}

	private val invalidWidths = Seq(0 bits, 1 bits, 3 bits, 5 bits, 12 bits, 17 bits, 32 bits).asTable("invalidWidth")

	it must "not accept invalid read widths" in spinalContext { () =>
		forAll(invalidWidths) { (invalidReadWidth: BitCount) => {
			val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(invalidReadWidth, 16 bits))
			thrown.getMessage must include("arg=readWidth")
		}}
	}

	it must "not accept invalid write widths" in spinalContext { () =>
		forAll(invalidWidths) { (invalidWriteWidth: BitCount) => {
			val thrown = the [IllegalArgumentException] thrownBy(new Ice40Ebram4k(16 bits, invalidWriteWidth))
			thrown.getMessage must include("arg=writeWidth")
		}}
	}

	it must "have an 8-bit address for a 16-bit data width (read)" in spinalContext { () =>
		val ebram = new Ice40Ebram4k(16 bits, 4 bits)
		ebram.io.ADR.getWidth must be(8)
	}

	it must "have an 8-bit address for a 16-bit data width (write)" in spinalContext { () =>
		val ebram = new Ice40Ebram4k(4 bits, 16 bits)
		ebram.io.ADW.getWidth must be(8)
	}

	it must "have a 9-bit address for an 8-bit data width (read)" in spinalContext { () =>
		val ebram = new Ice40Ebram4k(8 bits, 16 bits)
		ebram.io.ADR.getWidth must be(9)
	}

	it must "have a 9-bit address for an 8-bit data width (write)" in spinalContext { () =>
		val ebram = new Ice40Ebram4k(16 bits, 8 bits)
		ebram.io.ADW.getWidth must be(9)
	}

	it must "have a 10-bit address for a 4-bit data width (read)" in spinalContext { () =>
		val ebram = new Ice40Ebram4k(4 bits, 16 bits)
		ebram.io.ADR.getWidth must be(10)
	}

	it must "have a 10-bit address for a 4-bit data width (write)" in spinalContext { () =>
		val ebram = new Ice40Ebram4k(16 bits, 4 bits)
		ebram.io.ADW.getWidth must be(10)
	}

	it must "have an 11-bit address for a 2-bit data width (read)" in spinalContext { () =>
		val ebram = new Ice40Ebram4k(2 bits, 16 bits)
		ebram.io.ADR.getWidth must be(11)
	}

	it must "have an 11-bit address for a 2-bit data width (write)" in spinalContext { () =>
		val ebram = new Ice40Ebram4k(16 bits, 2 bits)
		ebram.io.ADW.getWidth must be(11)
	}

	it must "have 16 MASK_N bits if write width is 16 bits" in spinalContext { () =>
		val ebram = new Ice40Ebram4k(8 bits, 16 bits)
		ebram.io.MASK_N.get.getWidth must be(16)
	}

	private val widthsExcluding16Bits = validWidths.filter(width => width.value != 16).asTable("width")

	it must "have have no MASK_N if write width is not 16 bits" in spinalContext { () =>
		forAll(widthsExcluding16Bits) { (writeWidth: BitCount) =>
			val ebram = new Ice40Ebram4k(16 bits, writeWidth)
			ebram.io.MASK_N must be(None)
		}
	}

	"Ice40Ebram4k companion's apply() method" must "not accept a null readWidth" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy Ice40Ebram4k(null, anyWidth())
		thrown.getMessage must (include("arg=readWidth") and include("null"))
	}

	private def anyWidth() = validWidths(Random.nextInt(validWidths.length))

	it must "not accept a null writeWidth" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy Ice40Ebram4k(anyWidth(), null)
		thrown.getMessage must (include("arg=writeWidth") and include("null"))
	}

	it must "not accept null initialContents" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy Ice40Ebram4k(anyWidth(), anyWidth(), null)
		thrown.getMessage must (include("arg=initialContents") and include("null"))
	}
}
