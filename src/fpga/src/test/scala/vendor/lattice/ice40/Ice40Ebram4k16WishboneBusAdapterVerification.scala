package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import org.scalatest.flatspec._
import org.scalatest.Suite

import uk.co.lophtware.msfreference.tests.bus.wishbone.WishboneBusSlaveVerification
import uk.co.lophtware.msfreference.tests.formal._

class Ice40Ebram4k16WishboneBusAdapterVerification extends AnyFlatSpec with FormalVerificationFixture[Ice40Ebram4k16WishboneBusAdapterFormalVerificationFixture] {
	protected override def dutFactory() = new Ice40Ebram4k16WishboneBusAdapterFormalVerificationFixture().withStimuli

	override def nestedSuites: IndexedSeq[Suite] = Array(new WishboneBusSlaveVerification[Ice40Ebram4k16WishboneBusAdapterFormalVerificationFixture](dutFactory))
}
