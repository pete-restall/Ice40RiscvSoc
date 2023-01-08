package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.collection.immutable.ArraySeq
import scala.util.Random

import org.scalatest.flatspec._
import spinal.core._

import uk.co.lophtware.msfreference.tests.givenwhenthen._
import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class Ice40Ebram4kAs256x16Test(dutCreatedViaApplyFactory: Boolean) extends AnyFlatSpec with SimulationFixture[Ice40Ebram4kFixture] {
	protected override def dutFactory() = new Ice40Ebram4kFixture(16 bits, 16 bits, dutCreatedViaApplyFactory)

	"PDP4K" must "be able to store 256 16-bit words" in simulator { fixture =>
		val words = ArraySeq.fill(256) { Random.nextInt(1 << 16) }
		val test = fixture
			.given.populatedWith(words)
			.when.readingFrom(address=0)
			.then.contentsMustEqual(words)

		fixture.wireStimuliUsing(test.asStateMachine)
	}

	it must "be able to mask written bits" in simulator { fixture =>
		val words = ArraySeq.fill(256) { Random.nextInt(1 << 16) }
		val mask = Random.nextInt(1 << 16)
		val maskedWords = words.map(x => x & ~mask).toArray
		val test = fixture
			.given.writeMaskOf(mask)
			.and.populatedWith(words)
			.when.readingFrom(address=0)
			.then.contentsMustEqual(maskedWords)

		fixture.wireStimuliUsing(test.asStateMachine)
	}
}
