package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.wishbone

import uk.co.lophtware.msfreference.tests.givenwhenthen._

class SpramGiven(builder: SpramStateMachineBuilder) extends GivenAnd[SpramGiven, SpramWhen] {
	def spramIsPoweredOn: GivenAnd[SpramGiven, SpramWhen] = new SpramGiven(builder.powerOn())

	def accessedDirectly: GivenAnd[SpramGiven, SpramWhen] = new SpramGiven(builder.usingDirectSpramAccess())

	def accessedOverWishbone: GivenAnd[SpramGiven, SpramWhen] = new SpramGiven(builder.usingWishboneSpramAccess())

	def populatedWith(words: Seq[Int]): GivenAnd[SpramGiven, SpramWhen] = new SpramGiven(builder.populateWith(words))

	def singleWordWrittenTo(word: Int, atAddress: Int): GivenAnd[SpramGiven, SpramWhen] = new SpramGiven(builder.populateWith(List(word), atAddress))

	override def when: SpramWhen = new SpramWhen(builder)

	override def and: SpramGiven = this
}
