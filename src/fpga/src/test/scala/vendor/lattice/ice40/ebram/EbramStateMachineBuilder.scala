package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram

import scala.collection.immutable.LinearSeq

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class EbramStateMachineBuilder(private val ebram: Ice40Ebram4k.IoBundle, private val factoryStack: List[Sampling => WithNextSampling]) {
	def populateWith(words: Seq[Int], startingFromAddress: Int = 0) = new EbramStateMachineBuilder(
		ebram,
		((nextState: Sampling) => new EbramWriteSeqState(
			ebram,
			address=startingFromAddress,
			words=words,
			nextState=nextState)) :: factoryStack)

	def startReadingFrom(address: Int) = new EbramStateMachineBuilder(
		ebram,
		(nextState => new EbramPrimeReadState(ebram, address, nextState)) :: factoryStack)

	def assertContentsEqualTo(expectedWords: Seq[Int], startingFromAddress: Int = 0) = new EbramStateMachineBuilder(
		ebram,
		((nextState: Sampling) => new EbramAssertingReadState(
			ebram,
			startingFromAddress,
			expectedWords,
			SimulationEndsSuccessfullyState)) :: factoryStack)

	def build(): Sampling = collapsed(SimulationMissingTerminalState)

	private def collapsed(state: Sampling, factories: LinearSeq[Sampling => WithNextSampling] = factoryStack): Sampling =
		if (factories.isEmpty) state
		else collapsed(factories.head(state), factories.tail)
}
