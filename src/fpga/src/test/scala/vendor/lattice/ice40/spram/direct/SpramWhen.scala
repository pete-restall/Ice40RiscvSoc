package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.direct

import uk.co.lophtware.msfreference.tests.givenwhenthen._

class SpramWhen(builder: SpramStateMachineBuilder) extends WhenAnd[SpramWhen, SpramThen] {
	def readingFrom(address: Int): WhenAnd[SpramWhen, SpramThen] = new SpramWhen(builder.startReadingFrom(address))

	override def then: SpramThen = new SpramThen(builder)

	override def and: SpramWhen = this
}
