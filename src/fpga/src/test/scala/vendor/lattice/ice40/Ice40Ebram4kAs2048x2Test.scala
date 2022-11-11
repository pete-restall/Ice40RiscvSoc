package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.collection.immutable.ArraySeq
import scala.util.Random

import org.scalatest.flatspec._
import spinal.core._

import uk.co.lophtware.msfreference.tests.givenwhenthen._
import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class Ice40Ebram4kAs2048x2Test extends AnyFlatSpec with SimulationFixture[Ice40Ebram4kFixture] {
	protected override def dutFactory() = new Ice40Ebram4kFixture(2 bits, 2 bits)

	"PDP4K" must "be able to store 2048 2-bit crumbs" in simulator { fixture =>
		val words = ArraySeq.fill(2048) { Random.nextInt(1 << 2) }
		var test = fixture
			.given.populatedWith(words)
			.when.readingFrom(address=0)
			.then.contentsMustEqual(words)

		fixture.wireStimuliUsing(test.asStateMachine)
	}

	it must "not be able to mask written bits" in simulator { fixture =>
		val unmaskedWords = ArraySeq.fill(2048) { Random.nextInt(1 << 2) }
		val ignoredMask = Random.nextInt(1 << 16)
		var test = fixture
			.given.writeMaskOf(ignoredMask)
			.and.populatedWith(unmaskedWords)
			.when.readingFrom(address=0)
			.then.contentsMustEqual(unmaskedWords)

		fixture.wireStimuliUsing(test.asStateMachine)
	}
}
