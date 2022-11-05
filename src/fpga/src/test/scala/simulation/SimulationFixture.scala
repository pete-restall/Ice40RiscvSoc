package uk.co.lophtware.msfreference.tests.simulation

import scala.io.Source
import scala.util.Using

import org.scalatest._
import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation.EnvFile

trait SimulationFixture[TDut <: Component] extends TestSuiteMixin { this: TestSuite =>
	private val envFile = new EnvFile(".env")
	private val envVars = sys.env.withDefault(unknown => envFile(unknown))

	private var sim: SimCompiled[TDut] = _
	private val config = SpinalConfig(
		defaultClockDomainFrequency = FixedFrequency(100 MHz),
		defaultConfigForClockDomains = ClockDomainConfig(
			clockEdge = RISING,
			resetKind = ASYNC,
			resetActiveLevel = LOW,
			softResetActiveLevel = LOW,
			clockEnableActiveLevel = HIGH))

	abstract override def withFixture(test: NoArgTest): Outcome = {
		sim = SimConfig
			.withWave
			.withIVerilog
			.workspacePath(envVars("SPINALSIM_WORKSPACE"))
			.cachePath(s"${envVars("SPINALSIM_WORKSPACE")}/.pluginsCache")
			.withConfig(config)
			.allOptimisation
			.addSimulatorFlag(s"-y ${envVars("SIMULATOR_VERILOG_LIBRARY_PATH")}")
			.addIncludeDir(envVars("SIMULATOR_VERILOG_PATCHED_INCLUDE_PATH"))
			.addIncludeDir(envVars("SIMULATOR_VERILOG_INCLUDE_PATH"))
			.compile(dutFactory())

		super.withFixture(test)
	}

	protected def dutFactory(): TDut

	protected def simulator(test: TDut => Any) = {
		sim.doSim { dut =>
			SimTimeout(1_000_000)
			test(dut)
			while (true) {
				sleep(10)
			}
		}
	}
}
