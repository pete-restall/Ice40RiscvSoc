package uk.co.lophtware.msfreference.tests

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks

import uk.co.lophtware.msfreference.SeqPairingExtensions._
import uk.co.lophtware.msfreference.tests.IterableTableExtensions._

class SeqPairingExtensionsTest extends AnyFlatSpec with TableDrivenPropertyChecks {
	"The asPairedSeq method" must "not accept a null sequence of items" in {
		val nullItems: Seq[Any] = null
		val thrown = the [IllegalArgumentException] thrownBy nullItems.asPairedSeq
		thrown.getMessage must (include("arg=items") and include("null"))
	}

	it must "return an empty sequence when given an empty sequence" in {
		Seq.empty[Any].asPairedSeq must be(Seq.empty)
	}

	private val oddNumbers = Seq(1, 3, 5, 7, 9, oddNumberBetween(10, 100)).asTable("oddNumber")

	private def oddNumberBetween(minInclusive: Int, maxExclusive: Int) = Random.between(minInclusive, maxExclusive - 1) | 1

	it must "not accept an odd number of items" in {
		forAll(oddNumbers) { oddNumber =>
			val items = Seq.fill(oddNumber) { Random.nextInt() }
			val thrown = the [IllegalArgumentException] thrownBy items.asPairedSeq
			thrown.getMessage must (include("arg=items") and include("even number"))
		}
	}

	it must "return a single sequence of two items in the same order when given two items" in {
		val twoItems = Seq.fill(2) { Random.nextPrintableChar() }
		twoItems.asPairedSeq must equal(Array(twoItems))
	}

	it must "return two same-order sequences of paired items when given four items" in {
		val twoPairs = Seq.fill(2) { Seq.fill(2) { Random.nextPrintableChar() } }
		val fourItems = twoPairs.flatten
		fourItems.asPairedSeq must equal(twoPairs)
	}

	it must "return three same-order sequences of paired items when given six items" in {
		val threePairs = Seq.fill(3) { Seq.fill(2) { Random.nextInt() } }
		val sixItems = threePairs.flatten
		sixItems.asPairedSeq must equal(threePairs)
	}

	it must "return ten same-order sequences of paired items when given twenty items" in {
		val tenPairs = Seq.fill(10) { Seq.fill(2) { Random.nextInt() } }
		val twentyItems = tenPairs.flatten
		twentyItems.asPairedSeq must equal(tenPairs)
	}

	it must "allow implicit conversions from List" in {
		val pairedItems = Seq.fill(50) { Seq.fill(2) { Random.nextInt() } }
		val list = pairedItems.flatten.toList
		list.asPairedSeq must equal(pairedItems)
	}

	it must "allow implicit conversions from Array" in {
		val pairedItems = Seq.fill(50) { Seq.fill(2) { Random.nextInt() } }
		val array = pairedItems.flatten.toArray
		array.asPairedSeq must equal(pairedItems)
	}
}
