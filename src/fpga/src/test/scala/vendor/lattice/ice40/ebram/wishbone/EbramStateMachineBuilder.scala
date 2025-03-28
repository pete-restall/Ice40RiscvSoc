package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram.wishbone

import scala.collection.immutable.LinearSeq

import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.Wishbone

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram.direct.{EbramAssertingReadState, EbramPrimeReadState, EbramWriteSeqState}
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class EbramStateMachineBuilder(
	clockDomain: ClockDomain,
	wishbone: Wishbone,
	ebram: Ice40Ebram4k.IoBundle,
	isEbramDirect: Bool,
	factoryStack: List[Sampling => WithNextSampling]) {

	clockDomain.mustNotBeNull("clockDomain")
	wishbone.mustNotBeNull("wishbone")
	ebram.mustNotBeNull("ebram")
	factoryStack.mustNotContainNull("factoryStack")

	def powerOn() = usingDirectEbramAccess()
		.withFactory(nextState => new WishboneBitSelectState(wishbone, 0xffff, nextState))

	private def withFactory(factory: (Sampling) => WithNextSampling) = new EbramStateMachineBuilder(
		clockDomain,
		wishbone,
		ebram,
		isEbramDirect,
		factory :: factoryStack)

	def usingDirectEbramAccess() = withFactory(nextState => EbramAccessMethodState.direct(isEbramDirect, nextState))

	def usingWishboneEbramAccess() = withFactory(nextState => EbramAccessMethodState.wishbone(isEbramDirect, nextState))

	def populateWith(words: Seq[Int], startingFromAddress: Int = 0) = withFactory(nextState =>
		new SimulationBranchState(
			() => isEbramDirect.toBoolean,
			new EbramWriteSeqState(ebram, mask=0x0000, addressesAndWords=addressesAndWordsFor(words, startingFromAddress), nextState=nextState),
			new WishboneEbramWriteSeqState(clockDomain, wishbone, startingFromAddress, words, nextState)))

	private def addressesAndWordsFor(words: Seq[Int], startingFromAddress: Int = 0) = words.zipWithIndex.map(x => (startingFromAddress + x._2, x._1))

	def startReadingFrom(address: Int) = withFactory(nextState => new EbramPrimeReadState(ebram, address, nextState))

	def assertContentsEqualTo(expectedWords: Seq[Int], startingFromAddress: Int = 0) = withFactory(_ =>
		new SimulationBranchState(
			() => isEbramDirect.toBoolean,
			new EbramAssertingReadState(
				ebram,
				addressesAndWordsFor(expectedWords, startingFromAddress),
				SimulationEndsSuccessfullyState),
			new WishboneEbramAssertingReadState(
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
