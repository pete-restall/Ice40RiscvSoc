package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram

import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class EbramWriteSeqState(
	private val ebram: Ice40Ebram4k.IoBundle,
	private val mask: Int,
	private val addressesAndWords: Seq[(Int, Int)],
	private val nextState: Sampling) extends WithNextSampling {

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
