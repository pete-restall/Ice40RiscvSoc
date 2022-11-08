package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram

import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class EbramWriteSeqState(
	private val ebram: Ice40Ebram4k.IoBundle,
	private var address: Int,
	private val mask: Int,
	private val words: Seq[Int],
	private val nextState: Sampling) extends WithNextSampling {

	private val word = words.iterator

	override def onSampling(): Sampling = {
		if (!word.hasNext) {
			ebram.WE #= false
			return nextState.onSampling()
		}

		ebram.CEW #= true
		ebram.WE #= true
		ebram.MASK_N.map { _ #= mask }
		ebram.ADW #= address
		ebram.DI #= word.next()
		address = if (address < ebram.ADW.maxValue) address + 1 else 0

		return this
	}

	override def withNext(nextState: Sampling) = new EbramWriteSeqState(ebram, address, mask, words, nextState)
}
