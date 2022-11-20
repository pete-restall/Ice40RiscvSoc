package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.wishbone

import uk.co.lophtware.msfreference.tests.givenwhenthen._

class SpramGiven(private val builder: SpramStateMachineBuilder) extends GivenAnd[SpramGiven, SpramWhen] {
	def spramIsPoweredOn: GivenAnd[SpramGiven, SpramWhen] = new SpramGiven(builder.powerOn())

	def accessedDirectly: GivenAnd[SpramGiven, SpramWhen] = new SpramGiven(builder.usingDirectSpramAccess())

	def populatedWith(words: Seq[Int]): GivenAnd[SpramGiven, SpramWhen] = new SpramGiven(builder.populateWith(words))

	override def when: SpramWhen = new SpramWhen(builder)

	override def and: SpramGiven = this
}
