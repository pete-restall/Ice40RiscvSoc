package net.restall.ice40riscvsoc.tests.simulation

import org.scalatest._
import spinal.core._
import spinal.core.sim._

import net.restall.ice40riscvsoc.tests.EnvFile
import net.restall.ice40riscvsoc.tests.TestsPackage

trait VerilatorSimulationFixture[TDut <: Component] extends SimulationFixtureBase[TDut] { this: TestSuite =>
	protected abstract override def createSimulation(): SpinalSimConfig = VerilatorSimulationFixture
		.createSimulation()
		.workspaceName(TestsPackage.relativeClassNameOf(getClass))
}

object VerilatorSimulationFixture {
	private val envVars = sys.env.withDefault(unknown => EnvFile.default(unknown))

	private val config = SpinalConfig(
		defaultClockDomainFrequency = FixedFrequency(100 MHz),
		defaultConfigForClockDomains = ClockDomainConfig(
			clockEdge = RISING,
			resetKind = ASYNC,
			resetActiveLevel = LOW,
			softResetActiveLevel = LOW,
			clockEnableActiveLevel = HIGH))

	def createSimulation() = SimConfig
		.withWave
		.withVerilator
		.workspacePath(envVars("SPINALSIM_WORKSPACE"))
		.cachePath(s"${envVars("SPINALSIM_WORKSPACE")}/.pluginsCache")
		.withConfig(config)
		.allOptimisation
}
