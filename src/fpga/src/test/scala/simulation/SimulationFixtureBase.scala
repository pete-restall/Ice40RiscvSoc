package net.restall.ice40riscvsoc.tests.simulation

import org.scalatest._
import spinal.core._
import spinal.core.sim._
import spinal.sim.JvmThreadUnschedule

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._

abstract trait SimulationFixtureBase[TDut <: Component] extends TestSuiteMixin { this: TestSuite =>
	private lazy val sim: SimCompiled[TDut] = createSimulation().compile(dutFactory())

	protected def createSimulation(): SpinalSimConfig = ???

	protected def simulator(test: TDut => Any) = {
		test.mustNotBeNull("test")
		sim.doSim { dut =>
			dut.mustNotBeNull("dut")
			SimTimeout(1_000_000)
			try {
				val result = test(dut)
				if (result == SimulationFixtureBase.waitForExplicitSimulationTermination) {
					while (true) {
						sleep(10)
					}
				}
			} catch {
				case exception: JvmThreadUnschedule => throw exception
				case exception: Throwable => { sleep(1); throw exception }
			}
		}
	}

	protected def dutFactory(): TDut = ???
}

object SimulationFixtureBase {
	val waitForExplicitSimulationTermination = "Wait for explicit simSuccess(), simFailure() or timeout"
}
