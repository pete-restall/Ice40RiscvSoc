package net.restall.ice40riscvsoc.tests.simulation

import scala.reflect.io.File

import org.scalatest.TestSuite
import spinal.core._
import spinal.core.sim._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.tests.{EnvFile, TestsPackage}

trait NonSimulationFixture {
	protected def spinalContext(test: => Any) = {
		NonSimulationFixture.dummySim.doSim { dut =>
			dut.mustNotBeNull("dut")
			ClockDomain(Bool(), Bool()) {
				new Component { test }
			}
			simSuccess()
		}
	}
}

object NonSimulationFixture {
	private case class DummyComponent() extends Component {
	}

	private val envVars = sys.env.withDefault(unknown => EnvFile.default(unknown))

	private val config = SpinalConfig(
		singleTopLevel = false,
		defaultClockDomainFrequency = FixedFrequency(100 MHz),
		defaultConfigForClockDomains = ClockDomainConfig(
			clockEdge = RISING,
			resetKind = ASYNC,
			resetActiveLevel = LOW,
			softResetActiveLevel = LOW,
			clockEnableActiveLevel = HIGH))

	private lazy val dummySim = createSimulation().compile(new DummyComponent())

	private def createSimulation() = SimConfig
		.withIVerilog
		.workspacePath(envVars("SPINALSIM_WORKSPACE"))
		.cachePath(s"${envVars("SPINALSIM_WORKSPACE")}/.pluginsCache")
		.withConfig(config)
		.allOptimisation
		.addSimulatorFlag(s"-y ${File(envVars("SIMULATOR_VERILOG_LIBRARY_PATH")).toCanonical}")
		.addIncludeDir(File(envVars("SIMULATOR_VERILOG_PATCHED_INCLUDE_PATH")).toCanonical.toString)
		.addIncludeDir(File(envVars("SIMULATOR_VERILOG_INCLUDE_PATH")).toCanonical.toString)
		.workspaceName(TestsPackage.relativeClassNameOf(getClass))
}
