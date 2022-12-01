package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram

import org.scalatest.AppendedClues._
import org.scalatest.matchers.must.Matchers._
import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class EbramAssertingReadState(// TODO: NULL CHECKS FOR ALL THESE CONSTRUCTOR ARGS
	private val ebram: Ice40Ebram4k.IoBundle,
	private val expectedAddressesAndWords: Seq[(Int, Int)],
	private val nextState: Sampling) extends WithNextSampling {

	private val addressAndWord = expectedAddressesAndWords.iterator
	private var currentAddressAndWord = addressAndWord.nextOption()

	override def onSampling(): Sampling = {
		if (currentAddressAndWord.isEmpty)
			return nextState.onSampling()

		ebram.CER #= true
		ebram.RE #= true

		val nextAddressAndWord = addressAndWord.nextOption()
		ebram.ADR #= nextAddressAndWord.orElse(Some((0, 0))).map(x => wrappedAddress(x._1)).get

		val (address, expectedWord) = currentAddressAndWord.get
		ebram.DO.toInt must be(expectedWord) withClue s"at address ${address}"

		currentAddressAndWord = nextAddressAndWord
		return this
	}

	private def wrappedAddress(address: Int) = address % (ebram.ADR.maxValue + 1)

	override def withNext(nextState: Sampling) = new EbramAssertingReadState(ebram, expectedAddressesAndWords, nextState) // TODO: NULL CHECKS FOR nextState
}
