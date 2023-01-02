package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram.wishbone

import spinal.core.sim._
import spinal.lib.bus.wishbone._

import uk.co.lophtware.msfreference.tests.simulation._

class WishboneBitSelectState( // TODO: NULL CHECKS FOR ALL THESE CONSTRUCTOR ARGS
	private val ebram: Wishbone,
	private val bitSelector: Int,
	private val nextState: Sampling) extends WithNextSampling {

	override def onSampling(): Sampling = {
		ebram.SEL #= bitSelector
		nextState.onSampling()
	}

	override def withNext(nextState: Sampling) = new WishboneBitSelectState(ebram, bitSelector, nextState)
}
