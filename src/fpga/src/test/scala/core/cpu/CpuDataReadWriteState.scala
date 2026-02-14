package net.restall.ice40riscvsoc.tests.core.cpu

import scala.collection.mutable.Map

import spinal.core.sim._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.core.Cpu
import net.restall.ice40riscvsoc.tests.simulation._

class CpuDataReadWriteState(
	cpu: Cpu.IoBundle,
	data: Map[Long, Long],
	nextState: Sampling) extends WithNextSampling {

	cpu.mustNotBeNull("cpu")
	data.mustNotBeNull("data")
	nextState.mustNotBeNull("nextState")

	override def onActiveEdge(): Sampling = this

	override def onInactiveEdge(): Sampling = {
		val dbus = cpu.dbus
		if (dbus.CYC.toBoolean && dbus.STB.toBoolean) {
			if (!dbus.WE.toBoolean) {
				data.get(dbus.ADR.toLong).map { data =>
					dbus.DAT_MISO #= data
					dbus.ACK #= true
					this
				}.getOrElse(nextState)
			} else {
				data += (dbus.ADR.toLong -> dbus.DAT_MOSI.toLong)
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
