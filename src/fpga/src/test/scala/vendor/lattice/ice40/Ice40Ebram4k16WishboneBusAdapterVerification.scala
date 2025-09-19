package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40

import org.scalatest.flatspec._
import org.scalatest.Suite

import net.restall.ice40riscvsoc.tests.bus.wishbone.WishboneBusSlaveVerification
import net.restall.ice40riscvsoc.tests.formal._

class Ice40Ebram4k16WishboneBusAdapterVerification extends AnyFlatSpec with FormalVerificationFixture[Ice40Ebram4k16WishboneBusAdapterFormalVerificationFixture] {
	protected override def dutFactory() = new Ice40Ebram4k16WishboneBusAdapterFormalVerificationFixture().withStimuli

	override def nestedSuites: IndexedSeq[Suite] = Array(new WishboneBusSlaveVerification[Ice40Ebram4k16WishboneBusAdapterFormalVerificationFixture](dutFactory))
}
