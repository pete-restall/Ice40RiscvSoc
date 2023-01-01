package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.util.Random

import org.scalatest.flatspec._
import spinal.core._

import uk.co.lophtware.msfreference.tests.givenwhenthen._
import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class Ice40Ebram4kAsRead2Write16Test extends AnyFlatSpec with SimulationFixture[Ice40Ebram4kFixture] {
	protected override def dutFactory() = new Ice40Ebram4kFixture(2 bits, 16 bits)

	"PDP4K" must "be able to read eight interlaced 2-bit crumbs from a single 16-bit word" in simulator { fixture =>
		val word = Random.nextInt(1 << 16)
		val crumbs = fixture.wordToInterleavedCrumbs(word)
		val test = fixture
			.given.populatedWith(word)
			.when.readingFrom(address=0)
			.then.contentsMustEqual(
				0x000 -> crumbs(0),
				0x100 -> crumbs(1),
				0x200 -> crumbs(2),
				0x300 -> crumbs(3),
				0x400 -> crumbs(4),
				0x500 -> crumbs(5),
				0x600 -> crumbs(6),
				0x700 -> crumbs(7))

		fixture.wireStimuliUsing(test.asStateMachine)
	}

	it must "be able to mask written bits" in simulator { fixture =>
		val word = Random.nextInt(1 << 16)
		val mask = Random.nextInt(1 << 16)
		val maskedWord = word & ~mask
		val crumbs = fixture.wordToInterleavedCrumbs(maskedWord)
		val test = fixture
			.given.writeMaskOf(mask)
			.and.populatedWith(word)
			.when.readingFrom(address=0)
			.then.contentsMustEqual(
				0x000 -> crumbs(0),
				0x100 -> crumbs(1),
				0x200 -> crumbs(2),
				0x300 -> crumbs(3),
				0x400 -> crumbs(4),
				0x500 -> crumbs(5),
				0x600 -> crumbs(6),
				0x700 -> crumbs(7))

		fixture.wireStimuliUsing(test.asStateMachine)
	}
}
