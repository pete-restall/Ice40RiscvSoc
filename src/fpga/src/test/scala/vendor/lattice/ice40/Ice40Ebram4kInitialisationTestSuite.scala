package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.Suite
import spinal.core._

class Ice40Ebram4kInitialisationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] =
		createTestCasesWith(16 bits) ++
		createTestCasesWith(8 bits) ++
		createTestCasesWith(4 bits) ++
		createTestCasesWith(2 bits)

	private def createTestCasesWith(readWidth: BitCount) = Array(
		new Ice40Ebram4kInitialisationTest(readWidth=readWidth, writeWidth=anyWidth(), numberOfInitialisedCells=None),
		new Ice40Ebram4kInitialisationTest(readWidth=readWidth, writeWidth=anyWidth(), numberOfInitialisedCells=Some(0)),
		new Ice40Ebram4kInitialisationTest(readWidth=readWidth, writeWidth=anyWidth(), numberOfInitialisedCells=Some(4096 / readWidth.value)),
		new Ice40Ebram4kInitialisationTest(readWidth=readWidth, writeWidth=anyWidth(), numberOfInitialisedCells=Some(3 * readWidth.value)),
		new Ice40Ebram4kInitialisationTest(readWidth=readWidth, writeWidth=anyWidth(), numberOfInitialisedCells=Some(13 * readWidth.value)),
		new Ice40Ebram4kInitialisationTest(readWidth=readWidth, writeWidth=anyWidth(), numberOfInitialisedCells=Some(2048 / readWidth.value)))

	private def anyWidth() = (2 << Random.nextInt(3)) bits
}
