package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.collection.immutable.ArraySeq
import scala.util.Random

import org.scalatest.flatspec._
import spinal.core._

import uk.co.lophtware.msfreference.tests.givenwhenthen._
import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class Ice40Ebram4kAs1024x4Test extends AnyFlatSpec with SimulationFixture[Ice40Ebram4kFixture] {
	protected override def dutFactory() = new Ice40Ebram4kFixture(4 bits, 4 bits)

	"PDP4K" must "be able to store 1024 4-bit nybbles" in simulator { fixture =>
		val words = ArraySeq.fill(1024) { Random.nextInt(1 << 4) }
		var test = fixture
			.given.populatedWith(words)
			.when.readingFrom(address=0)
			.then.contentsMustEqual(words)

		fixture.wireStimuliUsing(test.asStateMachine)
	}
}
