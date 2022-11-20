package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.wishbone

import uk.co.lophtware.msfreference.tests.givenwhenthen._
import uk.co.lophtware.msfreference.tests.simulation._

class SpramThen(private val builder: SpramStateMachineBuilder) extends Then with And[SpramThen] {
	def singleWordMustEqual(word: Int, atAddress: Int): SpramThen = contentsMustEqual(List(word), atAddress)

	def contentsMustEqual(expectedWords: Seq[Int], startingFromAddress: Int = 0): SpramThen = new SpramThen(
		builder.assertContentsEqualTo(expectedWords, startingFromAddress))

	def asStateMachine: Sampling = builder.build()

	override def and: SpramThen = this
}
