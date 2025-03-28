package uk.co.lophtware.msfreference.tests.core.cpu

import spinal.core.sim._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.core.Cpu
import uk.co.lophtware.msfreference.tests.simulation._

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
