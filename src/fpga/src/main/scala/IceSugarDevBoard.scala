package net.restall.ice40riscvsoc

import spinal._
import spinal.core._

import net.restall.ice40riscvsoc.core.Core
import spinal.lib.Counter
import spinal.lib.ResetCtrl

class IceSugarDevBoard extends Component {
	val io = new Bundle {
		val p23 = out Bool()
		val ledR = out Bool()
		val ledG = out Bool()
		val ledB = out Bool()
	}

	private val reset = ResetCtrl.asyncAssertSyncDeassert(clockDomain.reset, clockDomain=clockDomain, inputPolarity=LOW, outputPolarity=HIGH)
	private val resetClockDomain = ClockDomain(clockDomain.clock, reset=reset, config=ClockDomainConfig(clockEdge=RISING, resetKind=SYNC, resetActiveLevel=HIGH))
	private val resetCounter = new ClockingArea(resetClockDomain) {
		val resetCounter = Counter(16 bits)
		resetCounter.willIncrement := !reset && !resetCounter.willOverflowIfInc
	}.resetCounter

	private val sanitisedClockDomain = ClockDomain(
		clockDomain.clock,
		reset=reset || resetCounter.willIncrement,
		frequency=FixedFrequency(12 MHz),
		config=ClockDomainConfig(
			clockEdge=RISING,
			resetKind=SYNC,
			resetActiveLevel=HIGH))

	private val sanitisedClockArea = new ClockingArea(sanitisedClockDomain) {
		private val core = Core.asBlackBox()
		io.p23 := core.io.p23
		io.ledR := core.io.ledR
		io.ledG := core.io.ledG
		io.ledB := core.io.ledB
	}
}

object IceSugarDevBoard {
	def main(args: Array[String]): Unit = {
		val externalClockDomain = ClockDomainConfig(
			clockEdge=RISING,
			resetKind=ASYNC,
			resetActiveLevel=LOW)

		val internalClockDomain = ClockDomainConfig(
			clockEdge=RISING,
			resetKind=SYNC,
			resetActiveLevel=HIGH)

		val hdl = new SpinalConfig(
			mode=Verilog,
			targetDirectory="src/radiant/hdl",
			defaultConfigForClockDomains=ClockDomainConfig(
				clockEdge=RISING,
				resetKind=ASYNC,
				resetActiveLevel=LOW))

		val topLevelModule = (externalClockDomain, () => new IceSugarDevBoard)
		val modules = topLevelModule +: List(
			() => new Core
		).map((internalClockDomain, _))

		modules.foreach { case(clockDomain, component) =>
			hdl.copy(defaultConfigForClockDomains=clockDomain).generate(component())
		}
	}
}
