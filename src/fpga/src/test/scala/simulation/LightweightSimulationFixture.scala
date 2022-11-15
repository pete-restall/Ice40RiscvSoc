package uk.co.lophtware.msfreference.tests.simulation

import org.scalatest._
import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation.EnvFile

trait LightweightSimulationFixture[TDut <: Component] extends SimulationFixtureBase[TDut] { this: TestSuite =>
	protected abstract override def createSimulation(): SpinalSimConfig = LightweightSimulationFixture.createSimulation()
}

object LightweightSimulationFixture {
	private val envFile = new EnvFile(".env")
	private val envVars = sys.env.withDefault(unknown => envFile(unknown))

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
