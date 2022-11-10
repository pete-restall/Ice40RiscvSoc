package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.collection.immutable.ArraySeq
import scala.util.Random

import org.scalatest.flatspec._
import spinal.core._

import uk.co.lophtware.msfreference.tests.givenwhenthen._
import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class Ice40Ebram4kAs512x8Test extends AnyFlatSpec with SimulationFixture[Ice40Ebram4kFixture] {
	protected override def dutFactory() = new Ice40Ebram4kFixture(8 bits, 8 bits)

	"PDP4K" must "be able to store 512 8-bit bytes" in simulator { fixture =>
		val words = ArraySeq.fill(512) { Random.nextInt(1 << 8) }
		var test = fixture
			.given.populatedWith(words)
			.when.readingFrom(address=0)
			.then.contentsMustEqual(words)

		fixture.wireStimuliUsing(test.asStateMachine)
	}
}
