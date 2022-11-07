package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram

import org.scalatest.AppendedClues._
import org.scalatest.matchers.must.Matchers._
import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class EbramAssertingReadState(
	private val ebram: Ice40Ebram4k.IoBundle,
	private var address: Int,
	private val expectedWords: Seq[Int],
	private val nextState: Sampling) extends WithNextSampling {

	private val word = expectedWords.iterator

	override def onSampling(): Sampling = {
		if (!word.hasNext)
			return nextState.onSampling()

		ebram.CER #= true
		ebram.RE #= true

		val readAddress = address
		address = if (address < ebram.ADR.maxValue) address + 1 else 0
		ebram.ADR #= address

		ebram.DO.toInt must be(word.next()) withClue s"at address ${readAddress}"
		return this
	}

	override def withNext(nextState: Sampling) = new EbramAssertingReadState(ebram, address, expectedWords, nextState)
}
