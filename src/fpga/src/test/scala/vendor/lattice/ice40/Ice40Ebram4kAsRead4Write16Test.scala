package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.util.Random

import org.scalatest.flatspec._
import spinal.core._

import uk.co.lophtware.msfreference.tests.givenwhenthen._
import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class Ice40Ebram4kAsRead4Write16Test extends AnyFlatSpec with SimulationFixture[Ice40Ebram4kFixture] {
	protected override def dutFactory() = new Ice40Ebram4kFixture(4 bits, 16 bits)

	"PDP4K" must "be able to read four interlaced 4-bit nybbles from a single 16-bit word" in simulator { fixture =>
		val word = Random.nextInt(1 << 16)
		val nybbles = fixture.wordToInterleavedNybbles(word)
		var test = fixture
			.given.populatedWith(word)
			.when.readingFrom(address=0)
			.then.contentsMustEqual(
				0x000 -> nybbles(0),
				0x100 -> nybbles(1),
				0x200 -> nybbles(2),
				0x300 -> nybbles(3))

		fixture.wireStimuliUsing(test.asStateMachine)
	}

	it must "be able to mask written bits" in simulator { fixture =>
		val word = Random.nextInt(1 << 16)
		val mask = Random.nextInt(1 << 16)
		val maskedWord = word & ~mask
		val nybbles = fixture.wordToInterleavedNybbles(maskedWord)
		var test = fixture
			.given.writeMaskOf(mask)
			.and.populatedWith(word)
			.when.readingFrom(address=0)
			.then.contentsMustEqual(
				0x000 -> nybbles(0),
				0x100 -> nybbles(1),
				0x200 -> nybbles(2),
				0x300 -> nybbles(3))

		fixture.wireStimuliUsing(test.asStateMachine)
	}
}
