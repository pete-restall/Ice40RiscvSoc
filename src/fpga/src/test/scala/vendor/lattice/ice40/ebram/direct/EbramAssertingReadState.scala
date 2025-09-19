package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.ebram.direct

import org.scalatest.AppendedClues._
import org.scalatest.matchers.must.Matchers._
import spinal.core.sim._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.tests.simulation._
import net.restall.ice40riscvsoc.vendor.lattice.ice40.Ice40Ebram4k

class EbramAssertingReadState(
	ebram: Ice40Ebram4k.IoBundle,
	expectedAddressesAndWords: Seq[(Int, Int)],
	nextState: Sampling) extends WithNextSampling {

	ebram.mustNotBeNull("ebram")
	expectedAddressesAndWords.mustNotBeNull("expectedAddressesAndWords")
	nextState.mustNotBeNull("nextState")

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

	override def withNext(nextState: Sampling) = new EbramAssertingReadState(ebram, expectedAddressesAndWords, nextState)
}
