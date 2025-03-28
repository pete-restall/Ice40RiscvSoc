package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram.wishbone

import org.scalatest.AppendedClues._
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone._
import spinal.lib.wishbone.sim.{WishboneDriver, WishboneTransaction}

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.tests.simulation._

class WishboneEbramAssertingReadState(
	clockDomain: ClockDomain,
	ebram: Wishbone,
	private var address: Int,
	expectedWords: Seq[Int],
	nextState: Sampling) extends SimulationForkedState(nextState) with WithNextSampling {

	clockDomain.mustNotBeNull("clockDomain")
	ebram.mustNotBeNull("ebram")
	expectedWords.mustNotBeNull("expectedWords")
	nextState.mustNotBeNull("nextState")

	protected override def onForked(): Unit = {
		clockDomain.waitSampling()
		val driver = new WishboneDriver(ebram, clockDomain)
		for (word <- expectedWords) {
			driver.sendBlockAsMaster(new WishboneTransaction(address), we=false)
			val readback = WishboneTransaction.sampleAsMaster(ebram)
			readback.data must be(word) withClue s"at address ${address}"
			address += 1
		}
	}

	override def withNext(nextState: Sampling) = new WishboneEbramAssertingReadState(clockDomain, ebram, address, expectedWords, nextState)
}
