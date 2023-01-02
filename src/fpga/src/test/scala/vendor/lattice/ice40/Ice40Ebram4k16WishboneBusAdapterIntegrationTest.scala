package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.collection.immutable.ArraySeq
import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.sim._
import spinal.lib.wishbone.sim.{WishboneDriver, WishboneTransaction}

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.{Ice40Ebram4k, Ice40Ebram4k16WishboneBusAdapter}

class Ice40Ebram4k16WishboneBusAdapterIntegrationTest extends AnyFlatSpec with SimulationFixture[Ice40Ebram4k16WishboneBusAdapterIntegrationFixture] {
	protected override def dutFactory() = new Ice40Ebram4k16WishboneBusAdapterIntegrationFixture()

	"Ice40Ebram4k16WishboneBusAdapter Wishbone bus" must "respond to a simple read" in simulator { fixture =>
		val word = fixture.anyData()
		val address = fixture.anyAddress()
		val test = fixture
			.given.ebramIsPoweredOn
			.and.accessedDirectly
			.and.singleWordWrittenTo(word, atAddress=address)
			.when.accessedOverWishbone
			.then.singleWordMustEqual(word, atAddress=address)

		fixture.wireStimuliUsing(test.asStateMachine)
	}

	it must "respond to a simple write" in simulator { fixture =>
		val word = fixture.anyData()
		val address = fixture.anyAddress()
		val test = fixture
			.given.ebramIsPoweredOn
			.and.accessedOverWishbone
			.and.singleWordWrittenTo(word, atAddress=address)
			.when.accessedDirectly
			.and.readingFrom(address)
			.then.singleWordMustEqual(word, atAddress=address)

		fixture.wireStimuliUsing(test.asStateMachine)
	}

	// TODO: SIMPLE NON-PIPELINED READ OF THREE WORDS
	// TODO: SIMPLE PIPELINED READ OF THREE WORDS
	// TODO: SIMPLE NON-PIPELINED WRITE OF THREE WORDS
	// TODO: SIMPLE PIPELINED WRITE OF THREE WORDS

	// TODO: ACK WILL GO HIGH ON THE SECOND RISING EDGE AND STAY HIGH UNTIL THE RISING EDGE AFTER CYC GOES LOW - THIS IS WRONG (ACK MUST NOT BE HIGH WHEN CYC IS NOT)
	// TODO: TO ACCOMMODATE MASTER WAIT-STATES, THE STROBE MUST BE TAKEN INTO ACCOUNT - IF THERE ARE X STB THEN THERE NEED TO BE X ACK
}
