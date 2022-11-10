package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram

import scala.collection.immutable.LinearSeq

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class EbramStateMachineBuilder(
	private val ebram: Ice40Ebram4k.IoBundle,
	private val writeMask: Int,
	private val factoryStack: List[Sampling => WithNextSampling]) {

	def setWriteMaskTo(mask: Int) = new EbramStateMachineBuilder(ebram, mask, factoryStack)

	def populateWith(addressesAndWords: Seq[(Int, Int)]) = new EbramStateMachineBuilder(
		ebram,
		writeMask,
		((nextState: Sampling) => new EbramWriteSeqState(
			ebram,
			addressesAndWords=addressesAndWords,
			mask=writeMask,
			nextState=nextState)) :: factoryStack)

	def startReadingFrom(address: Int) = new EbramStateMachineBuilder(
		ebram,
		writeMask,
		(nextState => new EbramPrimeReadState(ebram, address, nextState)) :: factoryStack)

	def assertContentsEqualTo(expectedAddressesAndWords: Seq[(Int, Int)]) = new EbramStateMachineBuilder(
		ebram,
		writeMask,
		((nextState: Sampling) => new EbramAssertingReadState(
			ebram,
			expectedAddressesAndWords,
			SimulationEndsSuccessfullyState)) :: factoryStack)

	def build(): Sampling = collapsed(SimulationMissingTerminalState)

	private def collapsed(state: Sampling, factories: LinearSeq[Sampling => WithNextSampling] = factoryStack): Sampling =
		if (factories.isEmpty) state
		else collapsed(factories.head(state), factories.tail)
}
