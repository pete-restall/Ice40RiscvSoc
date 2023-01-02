package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram.direct

import scala.collection.immutable.LinearSeq

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class EbramStateMachineBuilder( // TODO: NULL CHECKS FOR ALL THESE CONSTRUCTOR ARGS
	private val ebram: Ice40Ebram4k.IoBundle,
	private val writeMask: Int,
	private val factoryStack: List[Sampling => WithNextSampling]) {

	def setWriteMaskTo(mask: Int) = new EbramStateMachineBuilder(ebram, mask, factoryStack)

	def populateWith(addressesAndWords: Seq[(Int, Int)]) = withFactory(nextState =>
		new EbramWriteSeqState(
			ebram,
			addressesAndWords=addressesAndWords,
			mask=writeMask,
			nextState=nextState))

	private def withFactory(factory: (Sampling) => WithNextSampling) = new EbramStateMachineBuilder(ebram, writeMask, factory :: factoryStack)

	def startReadingFrom(address: Int) = withFactory(nextState => new EbramPrimeReadState(ebram, address, nextState))

	def assertContentsEqualTo(expectedAddressesAndWords: Seq[(Int, Int)]) = withFactory(nextState =>
		new EbramAssertingReadState(
			ebram,
			expectedAddressesAndWords,
			SimulationEndsSuccessfullyState))

	def build(): Sampling = collapsed(SimulationMissingTerminalState)

	private def collapsed(state: Sampling, factories: LinearSeq[Sampling => WithNextSampling] = factoryStack): Sampling =
		if (factories.isEmpty) state
		else collapsed(factories.head(state), factories.tail)
}
