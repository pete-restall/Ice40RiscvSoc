package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.ebram.direct

import net.restall.ice40riscvsoc.tests.givenwhenthen._

class EbramWhen(builder: EbramStateMachineBuilder, readingFromAddress: Int = 0) extends WhenAnd[EbramWhen, EbramThen] {
	def readingFrom(address: Int): WhenAnd[EbramWhen, EbramThen] = new EbramWhen(builder.startReadingFrom(address))

	override def then: EbramThen = new EbramThen(builder, readingFromAddress)

	override def and: EbramWhen = this
}
