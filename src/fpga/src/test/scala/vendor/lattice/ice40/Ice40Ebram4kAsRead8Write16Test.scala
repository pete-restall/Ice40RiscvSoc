package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.util.Random

import org.scalatest.flatspec._
import spinal.core._

import uk.co.lophtware.msfreference.tests.givenwhenthen._
import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class Ice40Ebram4kAsRead8Write16Test extends AnyFlatSpec with SimulationFixture[Ice40Ebram4kFixture] {
	protected override def dutFactory() = new Ice40Ebram4kFixture(8 bits, 16 bits)

	"PDP4K" must "be able to read two interlaced 8-bit bytes from a single 16-bit word" in simulator { fixture =>
		val word = Random.nextInt(1 << 16)
		val bytes = fixture.wordToInterleavedBytes(word)
		val test = fixture
			.given.populatedWith(word)
			.when.readingFrom(address=0)
			.then.contentsMustEqual(
				0x000 -> bytes(0),
				0x100 -> bytes(1))

		fixture.wireStimuliUsing(test.asStateMachine)
	}

	it must "be able to mask written bits" in simulator { fixture =>
		val word = Random.nextInt(1 << 16)
		val mask = Random.nextInt(1 << 16)
		val maskedWord = word & ~mask
		val bytes = fixture.wordToInterleavedBytes(maskedWord)
		val test = fixture
			.given.writeMaskOf(mask)
			.and.populatedWith(word)
			.when.readingFrom(address=0)
			.then.contentsMustEqual(
				0x000 -> bytes(0),
				0x100 -> bytes(1))

		fixture.wireStimuliUsing(test.asStateMachine)
	}
}
