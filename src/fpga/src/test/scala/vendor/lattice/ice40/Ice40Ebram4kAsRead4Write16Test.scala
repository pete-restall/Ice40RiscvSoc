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
		val nybbles = interleaved(word)
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

	private def interleaved(word: Int): Array[Int] = Array(
		interleaved(word, startBit=12),
		interleaved(word, startBit=13),
		interleaved(word, startBit=14),
		interleaved(word, startBit=15))

	private def interleaved(word: Int, startBit: Int) = {
		var nybble = 0
		var mask = 1 << startBit
		for (i <- 3 downto 0) {
			nybble = nybble | (if ((word & mask) != 0) 1 << i else 0)
			mask >>= 4
		}
		nybble
	}

	it must "be able to mask written bits" in simulator { fixture =>
		val word = Random.nextInt(1 << 16)
		val mask = Random.nextInt(1 << 16)
		val maskedWord = word & ~mask
		val nybbles = interleaved(maskedWord)
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
