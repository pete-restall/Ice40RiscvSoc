package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.collection.immutable.ArraySeq
import scala.util.Random

import org.scalatest.flatspec._
import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.givenwhenthen._
import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram.{EbramGiven, EbramWhen, EbramThen}
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class Ice40Ebram4kAs256x16Test extends AnyFlatSpec with SimulationFixture[Ice40Ebram4kFixture] {
	protected override def dutFactory() = new Ice40Ebram4kFixture(16 bits, 16 bits)

	"PDP4K" must "be able to store 256 16-bit words" in simulator { fixture =>
		val words = ArraySeq.fill(256) { Random.nextInt(1 << 16) }
		var state = fixture
			.given.populatedWith(words)
			.when.readingFrom(address=0)
			.then.contentsMustEqual(words)
			.asStateMachine

		fixture.readClockDomain.withRevertedClockEdge.onSamplings { state = state.onSampling() }
		fixture.readClockDomain.forkStimulus(period=10)
		fixture.writeClockDomain.forkStimulus(period=10)
	}
}
