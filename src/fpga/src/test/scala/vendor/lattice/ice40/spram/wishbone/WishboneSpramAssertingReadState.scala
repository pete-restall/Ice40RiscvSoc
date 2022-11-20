package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.wishbone

import org.scalatest.AppendedClues._
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone._
import spinal.lib.wishbone.sim.{WishboneDriver, WishboneTransaction}

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Spram16k16

class WishboneSpramAssertingReadState(
	private val clockDomain: ClockDomain,
	private val spram: Wishbone,
	private var address: Int,
	private val expectedWords: Seq[Int],
	nextState: Sampling) extends SimulationForkedState(nextState) with WithNextSampling {

	protected override def onForked(): Unit = {
		clockDomain.waitSampling()
		val driver = new WishboneDriver(spram, clockDomain)
		for (word <- expectedWords) {
			driver.sendBlockAsMaster(new WishboneTransaction(address), we=false)
			val readback = WishboneTransaction.sampleAsMaster(spram)
			readback.data must be(word) withClue s"at address ${address}"
			address += 1
		}
	}

	override def withNext(nextState: Sampling) = new WishboneSpramAssertingReadState(clockDomain, spram, address, expectedWords, nextState)
}
