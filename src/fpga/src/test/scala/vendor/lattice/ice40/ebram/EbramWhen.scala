package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram

import uk.co.lophtware.msfreference.tests.givenwhenthen._

class EbramWhen(private val builder: EbramStateMachineBuilder, private val readingFromAddress: Int = 0) extends WhenAnd[EbramWhen, EbramThen] {
	def readingFrom(address: Int): WhenAnd[EbramWhen, EbramThen] = new EbramWhen(builder.startReadingFrom(address))

	override def then: EbramThen = new EbramThen(builder, readingFromAddress)

	override def and: EbramWhen = this
}
