package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.spram.direct

import spinal.core.sim._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.tests.simulation._
import net.restall.ice40riscvsoc.vendor.lattice.ice40.Ice40Spram16k16

class SpramWriteSeqState(
	spram: Ice40Spram16k16.IoBundle,
	private var address: Int,
	words: Seq[Int],
	nextState: Sampling) extends WithNextSampling {

	spram.mustNotBeNull("spram")
	words.mustNotBeNull("words")
	nextState.mustNotBeNull("nextState")

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
