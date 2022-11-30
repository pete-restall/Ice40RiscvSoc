package uk.co.lophtware.msfreference.tests.simulation

import org.scalatest._
import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.EnvFile
import uk.co.lophtware.msfreference.tests.TestsPackage

trait LightweightSimulationFixture[TDut <: Component] extends SimulationFixtureBase[TDut] { this: TestSuite =>
	protected abstract override def createSimulation(): SpinalSimConfig = LightweightSimulationFixture
		.createSimulation()
		.workspaceName(TestsPackage.relativeClassNameOf(getClass))
}

object LightweightSimulationFixture {
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
