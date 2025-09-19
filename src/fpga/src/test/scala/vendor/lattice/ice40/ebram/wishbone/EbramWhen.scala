package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.ebram.wishbone

import net.restall.ice40riscvsoc.tests.givenwhenthen._

class EbramWhen(builder: EbramStateMachineBuilder) extends WhenAnd[EbramWhen, EbramThen] {
	def readingFrom(address: Int): WhenAnd[EbramWhen, EbramThen] = new EbramWhen(builder.startReadingFrom(address))

	def accessedDirectly: WhenAnd[EbramWhen, EbramThen] = new EbramWhen(builder.usingDirectEbramAccess)

	def accessedOverWishbone: WhenAnd[EbramWhen, EbramThen] = new EbramWhen(builder.usingWishboneEbramAccess)

	override def then: EbramThen = new EbramThen(builder)

	override def and: EbramWhen = this
}
