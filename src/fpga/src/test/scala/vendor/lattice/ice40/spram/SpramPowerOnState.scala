package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.spram

import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Spram16k16

class SpramPowerOnState(
	private val spram: Ice40Spram16k16.IoBundle,
	private val nextState: Sampling) extends WithNextSampling {

	override def onSampling(): Sampling = {
		spram.CS #= false
		spram.WE #= false
		spram.MASKWE #= 0
		spram.AD #= 0
		spram.DI #= 0
		spram.STDBY #= false
		spram.SLEEP #= false
		spram.PWROFF_N #= true
		nextState
	}

	override def withNext(nextState: Sampling) = new SpramPowerOnState(spram, nextState)
}
