package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram.direct

import uk.co.lophtware.msfreference.tests.givenwhenthen._
import uk.co.lophtware.msfreference.tests.simulation._

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
