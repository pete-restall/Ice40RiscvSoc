package net.restall.ice40riscvsoc.tests.core.cpu

import spinal.core.sim._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.core.Cpu
import net.restall.ice40riscvsoc.tests.simulation._

class CpuAssertionPredicateState(
	cpu: Cpu.IoBundle,
	predicate: Cpu.IoBundle => Boolean,
	nextState: Sampling) extends WithNextSampling {

	cpu.mustNotBeNull("cpu")
	predicate.mustNotBeNull("predicate")
	nextState.mustNotBeNull("nextState")

	override def onSampling(): Sampling = {
		if (!predicate(cpu)) this else nextState.onSampling()
	}

	override def withNext(nextState: Sampling) = new CpuAssertionPredicateState(cpu, predicate, nextState)
}
