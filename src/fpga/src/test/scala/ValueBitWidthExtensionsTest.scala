package net.restall.ice40riscvsoc.tests

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._

import net.restall.ice40riscvsoc.ValueBitWidthExtensions._
import net.restall.ice40riscvsoc.tests.IterableTableExtensions._

class ValueBitWidthExtensionsTest extends AnyFlatSpec with TableDrivenPropertyChecks {
	private val lessThanOne = Seq(0, -1, -2, -33, -1234).asTable("invalidValue")

	"The toCombinationalBitWidth method" must "not accept a value less than 1" in {
		forAll(lessThanOne) { (invalidValue: Int) =>
			val thrown = the [IllegalArgumentException] thrownBy(invalidValue.toCombinationalBitWidth)
			thrown.getMessage must include("arg=value")
		}
	}

	private val numbersOfValuesVsBitWidths = Seq(
		(1, 1 bit),
		(2, 1 bit),
		(3, 2 bits),
		(4, 2 bits),
		(5, 3 bits),
		(8, 3 bits),
		(9, 4 bits),
		(16, 4 bits),
		(17, 5 bits),
		(32, 5 bits),
		(33, 6 bits),
		(64, 6 bits),
		(128, 7 bits),
		(65536, 16 bits)
	).asTable("value", "bitWidth")

	it must "return the minimum number of bits required to represent the given number of values" in {
		forAll(numbersOfValuesVsBitWidths) { (numberOfValues: Int, bitWidth: BitCount) =>
			numberOfValues.toCombinationalBitWidth must be(bitWidth)
		}
	}
}
