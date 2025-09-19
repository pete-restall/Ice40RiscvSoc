package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40

import scala.util.Random

import org.scalatest.flatspec._
import spinal.core._

import net.restall.ice40riscvsoc.tests.givenwhenthen._
import net.restall.ice40riscvsoc.tests.simulation._
import net.restall.ice40riscvsoc.vendor.lattice.ice40.Ice40Ebram4k

class Ice40Ebram4kAsRead16Write8Test(dutCreatedViaApplyFactory: Boolean) extends AnyFlatSpec with SimulationFixture[Ice40Ebram4kFixture] {
	protected override def dutFactory() = new Ice40Ebram4kFixture(16 bits, 8 bits, dutCreatedViaApplyFactory)

	"PDP4K" must "be able to read one interlaced 16-bit word from two 8-bit bytes" in simulator { fixture =>
		val word = Random.nextInt(1 << 16)
		val bytes = fixture.wordToInterleavedBytes(word)
		val test = fixture
			.given.populatedWith(
				0x000 -> bytes(0),
				0x100 -> bytes(1))
			.when.readingFrom(address=0)
			.then.contentsMustEqual(0 -> word)

		fixture.wireStimuliUsing(test.asStateMachine)
	}

	it must "not be able to mask written bits" in simulator { fixture =>
		val unmaskedWord = Random.nextInt(1 << 16)
		val ignoredMask = Random.nextInt(1 << 16)
		val bytes = fixture.wordToInterleavedBytes(unmaskedWord)
		val test = fixture
			.given.writeMaskOf(ignoredMask)
			.and.populatedWith(
				0x000 -> bytes(0),
				0x100 -> bytes(1))
			.when.readingFrom(address=0)
			.then.contentsMustEqual(0 -> unmaskedWord)

		fixture.wireStimuliUsing(test.asStateMachine)
	}
}
