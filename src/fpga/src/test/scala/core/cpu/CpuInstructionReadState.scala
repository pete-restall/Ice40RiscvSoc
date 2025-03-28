package uk.co.lophtware.msfreference.tests.core.cpu

import spinal.core.sim._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.core.Cpu
import uk.co.lophtware.msfreference.tests.simulation._

class CpuInstructionReadState(
	cpu: Cpu.IoBundle,
	instructions: Map[Long, Long],
	nextState: Sampling) extends WithNextSampling {

	cpu.mustNotBeNull("cpu")
	instructions.mustNotBeNull("instructions")
	nextState.mustNotBeNull("nextState")

	override def onSampling(): Sampling = {
		val ibus = cpu.ibus
		if (ibus.CYC.toBoolean && ibus.STB.toBoolean) {
			if (!ibus.WE.toBoolean) {
				instructions.get(ibus.ADR.toLong).map { instruction =>
					ibus.DAT_MISO #= instruction
					ibus.ACK #= true
					this
				}.getOrElse(nextState)
			} else {
				nextState
			}
		} else {
			ibus.ACK #= false
			this
		}
	}

	override def withNext(nextState: Sampling) = new CpuInstructionReadState(cpu, instructions, nextState)
}
