package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.ebram.direct

import net.restall.ice40riscvsoc.tests.givenwhenthen._

class EbramGiven(builder: EbramStateMachineBuilder) extends GivenAnd[EbramGiven, EbramWhen] {
	def contentsHaveNotBeenInitialised: GivenAnd[EbramGiven, EbramWhen] = new EbramGiven(builder)

	def contentsHaveBeenInitialised: GivenAnd[EbramGiven, EbramWhen] = new EbramGiven(builder)

	def writeMaskOf(mask: Int): GivenAnd[EbramGiven, EbramWhen] = new EbramGiven(builder.setWriteMaskTo(mask))

	def populatedWith(words: Int*): GivenAnd[EbramGiven, EbramWhen] = populatedWith(words)

	def populatedWith(words: Seq[Int])(implicit dummy: DummyImplicit): GivenAnd[EbramGiven, EbramWhen] = {
		var address = -1
		populatedWith(words.map { word => address += 1; (address -> word) }: _*)
	}

	def populatedWith(addressesAndWords: (Int, Int)*)(implicit dummy1: DummyImplicit, dummy2: DummyImplicit): GivenAnd[EbramGiven, EbramWhen] =
		new EbramGiven(builder.populateWith(addressesAndWords))

	override def when: EbramWhen = new EbramWhen(builder)

	override def and: EbramGiven = this
}
