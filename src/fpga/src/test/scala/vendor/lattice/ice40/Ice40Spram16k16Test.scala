package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40

import scala.collection.immutable.ArraySeq
import scala.util.Random

import org.scalatest.flatspec._

import net.restall.ice40riscvsoc.tests.givenwhenthen._
import net.restall.ice40riscvsoc.tests.simulation._
import net.restall.ice40riscvsoc.vendor.lattice.ice40.Ice40Spram16k16

class Ice40Spram16k16Test extends AnyFlatSpec with SimulationFixture[Ice40Spram16k16Fixture] {
	protected override def dutFactory() = new Ice40Spram16k16Fixture

	"SB_SPRAM256KA" must "be able to store 256Kib of 16-bit words" in simulator { fixture =>
		val words = ArraySeq.fill(Ice40Spram16k16.NumberOfWords) { Random.nextInt(1 << 16) }
		val test = fixture
			.given.spramIsPoweredOn
			.and.populatedWith(words)
			.when.readingFrom(address=0)
			.then.contentsMustEqual(words)

		fixture.wireStimuliUsing(test.asStateMachine)
	}
}
