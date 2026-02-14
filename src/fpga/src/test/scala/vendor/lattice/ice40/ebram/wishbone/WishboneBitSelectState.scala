package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.ebram.wishbone

import spinal.core.sim._
import spinal.lib.bus.wishbone._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.tests.simulation._

class WishboneBitSelectState(ebram: Wishbone, bitSelector: Int, nextState: Sampling) extends WithNextSampling {
	ebram.mustNotBeNull("ebram")
	nextState.mustNotBeNull("nextState")

	override def onActiveEdge(): Sampling = {
		ebram.SEL #= bitSelector
		nextState.onActiveEdge()
	}

	override def withNext(nextState: Sampling) = new WishboneBitSelectState(ebram, bitSelector, nextState)
}
