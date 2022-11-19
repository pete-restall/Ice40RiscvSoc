package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.direct

import scala.collection.immutable.LinearSeq

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Spram16k16

class SpramStateMachineBuilder(private val spram: Ice40Spram16k16.IoBundle, private val factoryStack: List[Sampling => WithNextSampling]) {
	def powerOn() = withFactory(nextState => new SpramPowerOnState(spram, nextState))

	private def withFactory(factory: (Sampling) => WithNextSampling) = new SpramStateMachineBuilder(spram, factory :: factoryStack)

	def populateWith(words: Seq[Int], startingFromAddress: Int = 0) = withFactory(nextState =>
		new SpramWriteSeqState(
			spram,
			address=startingFromAddress,
			words=words,
			nextState=nextState))

	def startReadingFrom(address: Int) = withFactory(nextState => new SpramPrimeReadState(spram, address, nextState))

	def assertContentsEqualTo(expectedWords: Seq[Int], startingFromAddress: Int = 0) = withFactory(nextState =>
		new SpramAssertingReadState(
			spram,
			startingFromAddress,
			expectedWords,
			SimulationEndsSuccessfullyState))

	def build(): Sampling = collapsed(SimulationMissingTerminalState)

	private def collapsed(state: Sampling, factories: LinearSeq[Sampling => WithNextSampling] = factoryStack): Sampling =
		if (factories.isEmpty) state
		else collapsed(factories.head(state), factories.tail)
}
