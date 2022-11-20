package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.collection.immutable.ArraySeq
import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.sim._
import spinal.lib.wishbone.sim.{WishboneDriver, WishboneTransaction}

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.{Ice40Spram16k16, Ice40Spram16k16WishboneBusAdapter}

class Ice40Spram16k16WishboneBusAdapterIntegrationTest extends AnyFlatSpec with SimulationFixture[Ice40Spram16k16WishboneBusAdapterIntegrationFixture] {
	protected override def dutFactory() = new Ice40Spram16k16WishboneBusAdapterIntegrationFixture()

	"Ice40Spram16k16WishboneBusAdapter Wishbone bus" must "respond to a simple read" in simulator { fixture =>
		val words = ArraySeq.fill(Ice40Spram16k16.NumberOfWords) { Random.nextInt(1 << 16) }
		val address = fixture.anyAddress()
		val expectedWord = words(address)
		var test = fixture
			.given.spramIsPoweredOn
			.and.accessedDirectly
			.and.populatedWith(words)
			.when.accessedOverWishbone
			.then.singleWordMustEqual(expectedWord, atAddress=address)

		fixture.wireStimuliUsing(test.asStateMachine)
	}

	// TODO: SIMPLE WRITE OF ONE WORD
	// TODO: SIMPLE NON-PIPELINED READ OF THREE WORDS
	// TODO: SIMPLE PIPELINED READ OF THREE WORDS
	// TODO: SIMPLE NON-PIPELINED WRITE OF THREE WORDS
	// TODO: SIMPLE PIPELINED WRITE OF THREE WORDS
	// TODO: THEN CREATE A (GENERIC) FORMAL VERIFICATION FOR A WISHBONE SLAVE

	// TODO: ACK WILL GO HIGH ON THE SECOND RISING EDGE AND STAY HIGH UNTIL THE RISING EDGE AFTER CYC GOES LOW
	// TODO: TO ACCOMMODATE MASTER WAIT-STATES, THE STROBE MUST BE TAKEN INTO ACCOUNT - IF THERE ARE X STB THEN THERE NEED TO BE X ACK
}
