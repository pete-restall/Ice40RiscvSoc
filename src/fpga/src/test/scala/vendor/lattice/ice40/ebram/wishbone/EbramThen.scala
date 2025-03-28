package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram.wishbone

import uk.co.lophtware.msfreference.tests.givenwhenthen._
import uk.co.lophtware.msfreference.tests.simulation._

class EbramThen(builder: EbramStateMachineBuilder) extends Then with And[EbramThen] {
	def singleWordMustEqual(word: Int, atAddress: Int): EbramThen = contentsMustEqual(List(word), atAddress)

	def contentsMustEqual(expectedWords: Seq[Int], startingFromAddress: Int = 0): EbramThen = new EbramThen(
		builder.assertContentsEqualTo(expectedWords, startingFromAddress))

	def asStateMachine: Sampling = builder.build()

	override def and: EbramThen = this
}
