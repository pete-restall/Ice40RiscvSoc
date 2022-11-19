package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.raw

import org.scalatest.AppendedClues._
import org.scalatest.matchers.must.Matchers._
import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Spram16k16

class SpramAssertingReadState(
	private val spram: Ice40Spram16k16.IoBundle,
	private var address: Int,
	private val expectedWords: Seq[Int],
	private val nextState: Sampling) extends WithNextSampling {

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
