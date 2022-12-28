package uk.co.lophtware.msfreference.tests

import spinal.core._

package object simulation {
/*
	type LightweightSimulationFixture[TDut <: Component] = VerilatorSimulationFixture[TDut]
	val LightweightSimulationFixture = VerilatorSimulationFixture
*/
	type LightweightSimulationFixture[TDut <: Component] = IcarusSimulationFixture[TDut]
	val LightweightSimulationFixture = IcarusSimulationFixture

	type SimulationFixture[TDut <: Component] = IcarusSimulationFixture[TDut]
	val SimulationFixture = IcarusSimulationFixture
}
