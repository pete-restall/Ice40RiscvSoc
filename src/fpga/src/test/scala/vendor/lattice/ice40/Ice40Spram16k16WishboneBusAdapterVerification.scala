package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import org.scalatest.flatspec._
import org.scalatest.Suite

import uk.co.lophtware.msfreference.tests.formal._
import uk.co.lophtware.msfreference.tests.WishboneBusSlaveVerification

class Ice40Spram16k16WishboneBusAdapterVerification extends AnyFlatSpec with FormalVerificationFixture[Ice40Spram16k16WishboneBusAdapterFormalFixture] {
	protected override def dutFactory() = new Ice40Spram16k16WishboneBusAdapterFormalFixture().withStimuli

	override def nestedSuites: IndexedSeq[Suite] = Array(new WishboneBusSlaveVerification[Ice40Spram16k16WishboneBusAdapterFormalFixture](dutFactory))
}
