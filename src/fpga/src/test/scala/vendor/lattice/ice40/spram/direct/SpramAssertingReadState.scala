package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.spram.direct

import org.scalatest.AppendedClues._
import org.scalatest.matchers.must.Matchers._
import spinal.core.sim._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.tests.simulation._
import net.restall.ice40riscvsoc.vendor.lattice.ice40.Ice40Spram16k16

class SpramAssertingReadState(
	spram: Ice40Spram16k16.IoBundle,
	private var address: Int,
	expectedWords: Seq[Int],
	nextState: Sampling) extends WithNextSampling {

	spram.mustNotBeNull("spram")
	expectedWords.mustNotBeNull("expectedWords")
	nextState.mustNotBeNull("nextState")

	private val word = expectedWords.iterator

	override def onSampling(): Sampling = {
		if (!word.hasNext)
			return nextState.onSampling()

		spram.CS #= true
		spram.WE #= false

		val readAddress = address
		address = if (address < spram.AD.maxValue) address + 1 else 0
		spram.AD #= address

		spram.DO.toInt must be(word.next()) withClue s"at address ${readAddress}"
		return this
	}

	override def withNext(nextState: Sampling) = new SpramAssertingReadState(spram, address, expectedWords, nextState)
}
