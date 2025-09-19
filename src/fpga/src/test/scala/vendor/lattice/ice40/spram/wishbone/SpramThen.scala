package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.spram.wishbone

import net.restall.ice40riscvsoc.tests.givenwhenthen._
import net.restall.ice40riscvsoc.tests.simulation._

class SpramThen(builder: SpramStateMachineBuilder) extends Then with And[SpramThen] {
	def singleWordMustEqual(word: Int, atAddress: Int): SpramThen = contentsMustEqual(List(word), atAddress)

	def contentsMustEqual(expectedWords: Seq[Int], startingFromAddress: Int = 0): SpramThen = new SpramThen(
		builder.assertContentsEqualTo(expectedWords, startingFromAddress))

	def asStateMachine: Sampling = builder.build()

	override def and: SpramThen = this
}
