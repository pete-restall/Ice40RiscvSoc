package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram.wishbone

import spinal.core.sim._
import spinal.lib.bus.wishbone._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.tests.simulation._

class WishboneBitSelectState(ebram: Wishbone, bitSelector: Int, nextState: Sampling) extends WithNextSampling {
	ebram.mustNotBeNull("ebram")
	nextState.mustNotBeNull("nextState")

	override def onSampling(): Sampling = {
		ebram.SEL #= bitSelector
		nextState.onSampling()
	}

	override def withNext(nextState: Sampling) = new WishboneBitSelectState(ebram, bitSelector, nextState)
}
