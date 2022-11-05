package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram

import uk.co.lophtware.msfreference.tests.givenwhenthen._

class EbramGiven(private val builder: EbramStateMachineBuilder) extends GivenAnd[EbramGiven, EbramWhen] {
	def writeMaskOf(mask: Int): GivenAnd[EbramGiven, EbramWhen] = new EbramGiven(builder.setWriteMaskTo(mask))

	def populatedWith(words: Seq[Int]): GivenAnd[EbramGiven, EbramWhen] = new EbramGiven(builder.populateWith(words))

	override def when: EbramWhen = new EbramWhen(builder)

	override def and: EbramGiven = this
}
