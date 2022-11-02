package uk.co.lophtware.msfreference.tests.simulation

import org.scalatest._
import spinal.core._
import spinal.core.sim._

trait SimulationFixture[TDut <: Component] extends TestSuiteMixin { this: TestSuite =>
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
			.workspacePath(sys.env("SPINALSIM_WORKSPACE"))
			.cachePath(s"${sys.env("SPINALSIM_WORKSPACE")}/.pluginsCache")
			.withConfig(config)
			.allOptimisation
			.addSimulatorFlag(s"-y ${sys.env("SIMULATOR_VERILOG_INCLUDE_PATH")}")
			.addIncludeDir(sys.env("SIMULATOR_VERILOG_INCLUDE_PATH"))
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
