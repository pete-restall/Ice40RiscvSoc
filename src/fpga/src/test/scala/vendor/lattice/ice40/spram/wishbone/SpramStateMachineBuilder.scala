package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.wishbone

import scala.collection.immutable.LinearSeq

import spinal.core._
import spinal.lib.bus.wishbone.Wishbone

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.direct.{SpramAssertingReadState, SpramPowerOnState, SpramPrimeReadState, SpramWriteSeqState}
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Spram16k16

class SpramStateMachineBuilder(
	private val clockDomain: ClockDomain,
	private val wishbone: Wishbone,
	private val spram: Ice40Spram16k16.IoBundle,
	private val isSpramDirect: Bool,
	private val factoryStack: List[Sampling => WithNextSampling]) {

	def powerOn() =
		withFactory(nextState => new SpramPowerOnState(spram, nextState))
		.withFactory(nextState => new WishboneNybbleSelectState(wishbone, 0x0f, nextState))

	private def withFactory(factory: (Sampling) => WithNextSampling) = new SpramStateMachineBuilder(
		clockDomain,
		wishbone,
		spram,
		isSpramDirect,
		factory :: factoryStack)

	def usingDirectSpramAccess() = withFactory(nextState => SpramAccessMethodState.direct(isSpramDirect, nextState))

	def usingWishboneSpramAccess() = withFactory(nextState => SpramAccessMethodState.wishbone(isSpramDirect, nextState))

	def populateWith(words: Seq[Int], startingFromAddress: Int = 0) = withFactory(nextState =>
		new SpramWriteSeqState(
			spram,
			address=startingFromAddress,
			words=words,
			nextState=nextState))

	def startReadingFrom(address: Int) = withFactory(nextState => new SpramPrimeReadState(spram, address, nextState))

	def assertContentsEqualTo(expectedWords: Seq[Int], startingFromAddress: Int = 0) = withFactory(_ =>
		new SimulationBranchState(
			() => isSpramDirect == true,
			new SpramAssertingReadState(
				spram,
				startingFromAddress,
				expectedWords,
				SimulationEndsSuccessfullyState),
			new WishboneSpramAssertingReadState(
				clockDomain,
				wishbone,
				startingFromAddress,
				expectedWords,
				SimulationEndsSuccessfullyState)))

	def build(): Sampling = collapsed(SimulationMissingTerminalState)

	private def collapsed(state: Sampling, factories: LinearSeq[Sampling => WithNextSampling] = factoryStack): Sampling =
		if (factories.isEmpty) state
		else collapsed(factories.head(state), factories.tail)
}
