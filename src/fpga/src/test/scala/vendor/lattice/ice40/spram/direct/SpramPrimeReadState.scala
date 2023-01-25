package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.direct

import spinal.core.sim._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Spram16k16

class SpramPrimeReadState(spram: Ice40Spram16k16.IoBundle, address: Int, nextState: Sampling) extends WithNextSampling {
	spram.mustNotBeNull("spram")
	nextState.mustNotBeNull("nextState")

	override def onSampling(): Sampling = {
		spram.CS #= true
		spram.WE #= false
		spram.AD #= address
		nextState
	}

	override def withNext(nextState: Sampling) = new SpramPrimeReadState(spram, address, nextState)
}
