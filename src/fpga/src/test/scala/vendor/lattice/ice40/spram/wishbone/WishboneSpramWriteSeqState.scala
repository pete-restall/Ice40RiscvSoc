package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.wishbone

import spinal.core.ClockDomain
import spinal.core.sim._
import spinal.lib.bus.wishbone.Wishbone
import spinal.lib.wishbone.sim.{WishboneDriver, WishboneTransaction}

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.tests.simulation._

class WishboneSpramWriteSeqState(
	clockDomain: ClockDomain,
	spram: Wishbone,
	private var address: Int,
	words: Seq[Int],
	nextState: Sampling) extends SimulationForkedState(nextState) with WithNextSampling {

	clockDomain.mustNotBeNull("clockDomain")
	spram.mustNotBeNull("spram")
	words.mustNotBeNull("words")
	nextState.mustNotBeNull("nextState")

	protected override def onForked(): Unit = {
		clockDomain.waitSampling()
		val driver = new WishboneDriver(spram, clockDomain)
		for (word <- words) {
			driver.sendBlockAsMaster(new WishboneTransaction(address, word), we=true)
			address += 1
		}
	}

	override def withNext(nextState: Sampling) = new WishboneSpramWriteSeqState(clockDomain, spram, address, words, nextState)
}
