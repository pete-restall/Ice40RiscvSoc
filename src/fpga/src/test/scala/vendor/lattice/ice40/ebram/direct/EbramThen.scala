package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.ebram.direct

import net.restall.ice40riscvsoc.tests.givenwhenthen._
import net.restall.ice40riscvsoc.tests.simulation._

class EbramThen(builder: EbramStateMachineBuilder, readingFromAddress: Int) extends Then with And[EbramThen] {
	def contentsMustEqual(expectedWords: Seq[Int]): EbramThen = {
		var address = readingFromAddress - 1
		contentsMustEqual(expectedWords.map { word => address += 1; (address -> word) }: _*)
	}

	def contentsMustEqual(expectedAddressesAndWords: (Int, Int)*)(implicit dummy: DummyImplicit): EbramThen = new EbramThen(
		builder.assertContentsEqualTo(expectedAddressesAndWords.map(x => x)),
		readingFromAddress)

	def asStateMachine: Sampling = builder.build()

	override def and: EbramThen = this
}
