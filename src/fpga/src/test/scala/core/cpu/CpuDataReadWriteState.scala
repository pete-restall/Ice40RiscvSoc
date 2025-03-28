package uk.co.lophtware.msfreference.tests.core.cpu

import scala.collection.mutable.Map

import spinal.core.sim._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.core.Cpu
import uk.co.lophtware.msfreference.tests.simulation._

class CpuDataReadWriteState(
	cpu: Cpu.IoBundle,
	data: Map[Long, Long],
	nextState: Sampling) extends WithNextSampling {

	cpu.mustNotBeNull("cpu")
	data.mustNotBeNull("data")
	nextState.mustNotBeNull("nextState")

	override def onSampling(): Sampling = {
		val dbus = cpu.dbus
		if (dbus.CYC.toBoolean && dbus.STB.toBoolean) {
			if (!dbus.WE.toBoolean) {
				data.get(dbus.ADR.toLong).map { data =>
					dbus.DAT_MISO #= data
					dbus.ACK #= true
					this
				}.getOrElse(nextState)
			} else {
				data += (dbus.ADR.toLong -> dbus.DAT_MOSI.toLong) // TODO: SHOULD HAVE A LATENCY OF 0 CYCLES, NOT 1 AS THIS DOES; WOULD ENTAIL THIS LOGIC SAMPLING ON FALLING EDGES OR A NON-SAMPLING EVENT...
				dbus.ACK #= true
				this
			}
		} else {
			dbus.ACK #= false
			this
		}
	}

	override def withNext(nextState: Sampling) = new CpuDataReadWriteState(cpu, data, nextState)
}
