package net.restall.ice40riscvsoc.tests.core.cpu

import scala.collection.immutable.LinearSeq

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.core.Cpu
import net.restall.ice40riscvsoc.tests.simulation._

class CpuStateMachineBuilder(
	cpu: Cpu.IoBundle,
	data: scala.collection.mutable.Map[Long, Long],
	ibusFactoryStack: List[Sampling => WithNextSampling],
	dbusFactoryStack: List[Sampling => WithNextSampling],
	assertionFactoryStack: List[Sampling => WithNextSampling]) {

	cpu.mustNotBeNull("cpu")
	data.mustNotBeNull("data")
	ibusFactoryStack.mustNotBeNull("ibusFactoryStack")
	dbusFactoryStack.mustNotBeNull("dbusFactoryStack")
	assertionFactoryStack.mustNotBeNull("assertionFactoryStack")

	def withInstructions(instructions: Map[Long, Long]) = withIbusFactory(nextState => new CpuInstructionReadState(cpu, instructions, nextState))

	private def withIbusFactory(factory: (Sampling) => WithNextSampling) = new CpuStateMachineBuilder(
		cpu,
		data,
		factory :: ibusFactoryStack,
		dbusFactoryStack,
		assertionFactoryStack)

	def withData(data: Map[Long, Long]) = {
		this.data ++= data
		withDbusFactory(nextState => new CpuDataReadWriteState(cpu, this.data, nextState))
	}

	private def withDbusFactory(factory: (Sampling) => WithNextSampling) = new CpuStateMachineBuilder(
		cpu,
		data,
		ibusFactoryStack,
		factory :: dbusFactoryStack,
		assertionFactoryStack)

	def pushAssertionPredicate(predicate: Cpu.IoBundle => Boolean) = withAssertionFactory(nextState =>
		new CpuAssertionPredicateState(
			cpu,
			predicate,
			nextState))

	private def withAssertionFactory(factory: (Sampling) => WithNextSampling) = new CpuStateMachineBuilder(
		cpu,
		data,
		ibusFactoryStack,
		dbusFactoryStack,
		factory :: assertionFactoryStack)

	def assertDataEqualTo(expectedAddressesAndWords: Map[Long, Long]) = withAssertionFactory(nextState =>
		new CpuDataAssertionState(
			data,
			expectedAddressesAndWords,
			nextState))

	def build(): Sampling = new ConcurrentSamplingState(
		collapsed(new SimulationFailureState("Illegal ibus transaction"), ibusFactoryStack),
		collapsed(new SimulationFailureState("Illegal dbus transaction"), dbusFactoryStack),
		collapsed(SimulationEndsSuccessfullyState, assertionFactoryStack))

	private def collapsed(state: Sampling, factories: LinearSeq[Sampling => WithNextSampling]): Sampling = {
		if (factories.isEmpty) state
		else collapsed(factories.head(state), factories.tail)}
}
