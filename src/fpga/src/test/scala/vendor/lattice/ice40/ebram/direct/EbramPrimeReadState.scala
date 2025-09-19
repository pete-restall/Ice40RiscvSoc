package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40.ebram.direct

import spinal.core.sim._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.tests.simulation._
import net.restall.ice40riscvsoc.vendor.lattice.ice40.Ice40Ebram4k

class EbramPrimeReadState(ebram: Ice40Ebram4k.IoBundle, address: Int, nextState: Sampling) extends WithNextSampling {
	ebram.mustNotBeNull("ebram")
	nextState.mustNotBeNull("nextState")

	override def onSampling(): Sampling = {
		ebram.CER #= true
		ebram.RE #= true
		ebram.ADR #= address
		nextState
	}

	override def withNext(nextState: Sampling) = new EbramPrimeReadState(ebram, address, nextState)
}
