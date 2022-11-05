package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram

import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class EbramPrimeReadState(
	private val ebram: Ice40Ebram4k.IoBundle,
	private val address: Int,
	private val nextState: Sampling) extends WithNextSampling {

	override def onSampling(): Sampling = {
		ebram.CER #= true
		ebram.RE #= true
		ebram.ADR #= address
		nextState
	}

	override def withNext(nextState: Sampling) = new EbramPrimeReadState(ebram, address, nextState)
}
