package uk.co.lophtware.msfreference.tests.simulation

import org.scalatest._
import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.EnvFile
import uk.co.lophtware.msfreference.tests.TestsPackage

abstract trait SimulationFixture[TDut <: Component] extends SimulationFixtureBase[TDut] { this: TestSuite =>
	protected abstract override def createSimulation(): SpinalSimConfig = SimulationFixture
		.createSimulation()
		.workspaceName(TestsPackage.relativeClassNameOf(getClass))
}

object SimulationFixture {
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
		.withIVerilog
		.workspacePath(envVars("SPINALSIM_WORKSPACE"))
		.cachePath(s"${envVars("SPINALSIM_WORKSPACE")}/.pluginsCache")
		.withConfig(config)
		.allOptimisation
		.addSimulatorFlag(s"-y ${envVars("SIMULATOR_VERILOG_LIBRARY_PATH")}")
		.addIncludeDir(envVars("SIMULATOR_VERILOG_PATCHED_INCLUDE_PATH"))
		.addIncludeDir(envVars("SIMULATOR_VERILOG_INCLUDE_PATH"))
}
