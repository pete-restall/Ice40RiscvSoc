package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.ebram.direct

import spinal.core.sim._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.tests.simulation._
import net.restall.ice40riscvsoc.vendor.lattice.ice40.Ice40Ebram4k

class EbramWriteSeqState(
	ebram: Ice40Ebram4k.IoBundle,
	mask: Int,
	addressesAndWords: Seq[(Int, Int)],
	nextState: Sampling) extends WithNextSampling {

	ebram.mustNotBeNull("ebram")
	addressesAndWords.mustNotBeNull("addressesAndWords")
	nextState.mustNotBeNull("nextState")

	private val addressAndWord = addressesAndWords.iterator

	override def onSampling(): Sampling = {
		if (!addressAndWord.hasNext) {
			ebram.WE #= false
			return nextState.onSampling()
		}

		val (address, word) = addressAndWord.next()
		ebram.CEW #= true
		ebram.WE #= true
		ebram.MASK_N.map { _ #= mask }
		ebram.ADW #= address
		ebram.DI #= word

		return this
	}

	override def withNext(nextState: Sampling) = new EbramWriteSeqState(ebram, mask, addressesAndWords, nextState)
}
