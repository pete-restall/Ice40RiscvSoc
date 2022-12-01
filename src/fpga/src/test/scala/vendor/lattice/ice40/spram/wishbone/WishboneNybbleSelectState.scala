package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.wishbone

import spinal.core.sim._
import spinal.lib.bus.wishbone._

import uk.co.lophtware.msfreference.tests.simulation._

class WishboneNybbleSelectState( // TODO: NULL CHECKS FOR ALL THESE CONSTRUCTOR ARGS
	private val spram: Wishbone,
	private val nybbleSelector: Int,
	private val nextState: Sampling) extends WithNextSampling {

	override def onSampling(): Sampling = {
		spram.SEL #= nybbleSelector
		nextState.onSampling()
	}

	override def withNext(nextState: Sampling) = new WishboneNybbleSelectState(spram, nybbleSelector, nextState)
}
