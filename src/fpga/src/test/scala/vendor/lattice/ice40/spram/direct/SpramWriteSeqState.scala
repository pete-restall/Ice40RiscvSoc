package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.direct

import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Spram16k16

class SpramWriteSeqState( // TODO: NULL CHECKS FOR ALL THESE CONSTRUCTOR ARGS
	private val spram: Ice40Spram16k16.IoBundle,
	private var address: Int,
	private val words: Seq[Int],
	private val nextState: Sampling) extends WithNextSampling {

	private val word = words.iterator

	override def onSampling(): Sampling = {
		if (!word.hasNext)
			return nextState.onSampling()

		spram.CS #= true
		spram.WE #= true
		spram.MASKWE #= 0x0f
		spram.AD #= address
		spram.DI #= word.next()
		address = if (address < spram.AD.maxValue) address + 1 else 0

		return this
	}

	override def withNext(nextState: Sampling) = new SpramWriteSeqState(spram, address, words, nextState)
}
