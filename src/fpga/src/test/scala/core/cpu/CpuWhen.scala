package net.restall.ice40riscvsoc.tests.core.cpu

import spinal.core.sim._

import net.restall.ice40riscvsoc.core.Cpu
import net.restall.ice40riscvsoc.tests.givenwhenthen._

class CpuWhen(builder: CpuStateMachineBuilder) extends WhenAnd[CpuWhen, CpuThen] {
	def dataWrittenTo(address: Long): WhenAnd[CpuWhen, CpuThen] = new CpuWhen(builder.pushAssertionPredicate(cpu =>
		cpu.dbus.CYC.toBoolean &&
		cpu.dbus.WE.toBoolean &&
		cpu.dbus.STB.toBoolean &&
		cpu.dbus.ACK.toBoolean &&
		cpu.dbus.ADR.toLong == address))

	override def then: CpuThen = new CpuThen(builder)

	override def and: CpuWhen = this
}
