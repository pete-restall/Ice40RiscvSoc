package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.collection.immutable.ArraySeq
import scala.util.Random

import org.scalatest.flatspec._

import uk.co.lophtware.msfreference.tests.givenwhenthen._
import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.{SpramGiven, SpramWhen, SpramThen}
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Spram16k16

class Ice40Spram16k16Test extends AnyFlatSpec with SimulationFixture[Ice40Spram16k16Fixture] {
	private val NumberOfWords = 256 * 1024 / 16

	protected override def dutFactory() = new Ice40Spram16k16Fixture

	"SB_SPRAM256KA" must "be able to store 256Kib of 16-bit words" in simulator { fixture =>
		val words = ArraySeq.fill(NumberOfWords) { Random.nextInt(1 << 16) }
		var test = fixture
			.given.spramIsPoweredOn()
			.and.populatedWith(words)
			.when.readingFrom(address=0)
			.then.contentsMustEqual(words)

		fixture.wireStimuliWithStateMachine(test.asStateMachine)
	}
}
