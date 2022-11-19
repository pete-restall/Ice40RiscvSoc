package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.raw

import scala.collection.immutable.LinearSeq

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Spram16k16

class SpramStateMachineBuilder(private val spram: Ice40Spram16k16.IoBundle, private val factoryStack: List[Sampling => WithNextSampling]) {
	def powerOn() = new SpramStateMachineBuilder(
		spram,
		(nextState => new SpramPowerOnState(spram, nextState)) :: factoryStack)

	def populateWith(words: Seq[Int], startingFromAddress: Int = 0) = new SpramStateMachineBuilder(
		spram,
		((nextState: Sampling) => new SpramWriteSeqState(
			spram,
			address=startingFromAddress,
			words=words,
			nextState=nextState)) :: factoryStack)

	def startReadingFrom(address: Int) = new SpramStateMachineBuilder(
		spram,
		(nextState => new SpramPrimeReadState(spram, address, nextState)) :: factoryStack)

	def assertContentsEqualTo(expectedWords: Seq[Int], startingFromAddress: Int = 0) = new SpramStateMachineBuilder(
		spram,
		((nextState: Sampling) => new SpramAssertingReadState(
			spram,
			startingFromAddress,
			expectedWords,
			SimulationEndsSuccessfullyState)) :: factoryStack)

	def build(): Sampling = collapsed(SimulationMissingTerminalState)

	private def collapsed(state: Sampling, factories: LinearSeq[Sampling => WithNextSampling] = factoryStack): Sampling =
		if (factories.isEmpty) state
		else collapsed(factories.head(state), factories.tail)
}
