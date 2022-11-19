package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram.direct

import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Spram16k16

class SpramPrimeReadState(
	private val spram: Ice40Spram16k16.IoBundle,
	private val address: Int,
	private val nextState: Sampling) extends WithNextSampling {

	override def onSampling(): Sampling = {
		spram.CS #= true
		spram.WE #= false
		spram.AD #= address
		nextState
	}

	override def withNext(nextState: Sampling) = new SpramPrimeReadState(spram, address, nextState)
}
