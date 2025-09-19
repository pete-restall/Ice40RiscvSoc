package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.ebram.wishbone

import net.restall.ice40riscvsoc.tests.givenwhenthen._

class EbramGiven(builder: EbramStateMachineBuilder) extends GivenAnd[EbramGiven, EbramWhen] {
	def ebramIsPoweredOn: GivenAnd[EbramGiven, EbramWhen] = new EbramGiven(builder.powerOn())

	def accessedDirectly: GivenAnd[EbramGiven, EbramWhen] = new EbramGiven(builder.usingDirectEbramAccess())

	def accessedOverWishbone: GivenAnd[EbramGiven, EbramWhen] = new EbramGiven(builder.usingWishboneEbramAccess())

	def populatedWith(words: Seq[Int]): GivenAnd[EbramGiven, EbramWhen] = new EbramGiven(builder.populateWith(words))

	def singleWordWrittenTo(word: Int, atAddress: Int): GivenAnd[EbramGiven, EbramWhen] = new EbramGiven(builder.populateWith(List(word), atAddress))

	override def when: EbramWhen = new EbramWhen(builder)

	override def and: EbramGiven = this
}
