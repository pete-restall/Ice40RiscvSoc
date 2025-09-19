package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.spram.wishbone

import spinal.core.sim._
import spinal.lib.bus.wishbone._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.tests.simulation._

class WishboneNybbleSelectState(spram: Wishbone, nybbleSelector: Int, nextState: Sampling) extends WithNextSampling {
	spram.mustNotBeNull("spram")
	nextState.mustNotBeNull("nextState")

	override def onSampling(): Sampling = {
		spram.SEL #= nybbleSelector
		nextState.onSampling()
	}

	override def withNext(nextState: Sampling) = new WishboneNybbleSelectState(spram, nybbleSelector, nextState)
}
