package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.ebram.wishbone

import spinal.core.ClockDomain
import spinal.core.sim._
import spinal.lib.bus.wishbone.Wishbone
import spinal.lib.wishbone.sim.{WishboneDriver, WishboneTransaction}

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.tests.simulation._

class WishboneEbramWriteSeqState(
	clockDomain: ClockDomain,
	ebram: Wishbone,
	private var address: Int,
	words: Seq[Int],
	nextState: Sampling) extends SimulationForkedState(nextState) with WithNextSampling {

	clockDomain.mustNotBeNull("clockDomain")
	ebram.mustNotBeNull("ebram")
	words.mustNotBeNull("words")
	nextState.mustNotBeNull("nextState")

	protected override def onForked(): Unit = {
		clockDomain.waitSampling()
		val driver = new WishboneDriver(ebram, clockDomain)
		for (word <- words) {
			driver.sendBlockAsMaster(new WishboneTransaction(address, word), we=true)
			address += 1
		}
	}

	override def withNext(nextState: Sampling) = new WishboneEbramWriteSeqState(clockDomain, ebram, address, words, nextState)
}
