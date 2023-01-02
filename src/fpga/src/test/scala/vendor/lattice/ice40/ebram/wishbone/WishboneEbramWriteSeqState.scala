package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram.wishbone

import spinal.core.ClockDomain
import spinal.core.sim._
import spinal.lib.bus.wishbone.Wishbone
import spinal.lib.wishbone.sim.{WishboneDriver, WishboneTransaction}

import uk.co.lophtware.msfreference.tests.simulation._

class WishboneEbramWriteSeqState( // TODO: NULL CHECKS FOR ALL THESE CONSTRUCTOR ARGS
	private val clockDomain: ClockDomain,
	private val ebram: Wishbone,
	private var address: Int,
	private val words: Seq[Int],
	nextState: Sampling) extends SimulationForkedState(nextState) with WithNextSampling {

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
