package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import org.scalatest.flatspec._
import org.scalatest.matchers.must._
import spinal.core._

import uk.co.lophtware.msfreference.tests.simulation.SimulationFixture
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class Ice40Ebram4kInitialisationTest(readWidth: BitCount, writeWidth: BitCount, numberOfInitialisedCells: Option[Int]) extends AnyFlatSpec
	with SimulationFixture[Ice40Ebram4kInitialisationFixture]
	with Matchers {

	protected override def dutFactory() = new Ice40Ebram4kInitialisationFixture(readWidth, writeWidth, numberOfInitialisedCells)

	private val AllReadAddresses = 4096 / readWidth.value

	"Ice40Ebram4k" must "have zero contents when not initialised" in simulator { fixture =>
		if (!numberOfInitialisedCells.isDefined) {
			val allZeroes = Seq.fill(AllReadAddresses)(0)
			val test = fixture
				.given.contentsHaveNotBeenInitialised
				.when.readingFrom(address=0)
				.then.contentsMustEqual(allZeroes)

			fixture.wireStimuliUsing(test.asStateMachine)
		}
	}

	it must "have the same contents as passed to the factory when initialised" in simulator { fixture =>
		if (numberOfInitialisedCells.isDefined) {
			val test = fixture
				.given.contentsHaveBeenInitialised
				.when.readingFrom(address=0)
				.then.contentsMustEqual(fixture.expectedBlockContents)

			fixture.wireStimuliUsing(test.asStateMachine)
		}
	}
}
