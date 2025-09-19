package net.restall.ice40riscvsoc.tests.core

import scala.util.Random

import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import net.restall.ice40riscvsoc.core.CpuBusBridge
import net.restall.ice40riscvsoc.tests.bus.wishbone.WishboneTestDoubles

class CpuBusBridgeFixture() extends Component {
	val io = new Bundle {
		val bridge = new CpuBusBridge.IoBundle(
			cpuBusConfig=new WishboneConfig(addressWidth=16, dataWidth=16, useSTALL=true),
			deviceBusConfig=new WishboneConfig(addressWidth=32, dataWidth=32, useSTALL=false))

		val dbusSlaves = Seq.fill(5) { WishboneTestDoubles.stubSlaveWith(bridge.devices.dbus.config) }
		val dbusSlaveSelects = out Bits((dbusSlaves.length + 1) bits)

		val executableSlaves = Seq.fill(5) { WishboneTestDoubles.stubSlaveWith(bridge.devices.executable.config) }
		val executableFromDbusSlaveSelects = out Bits(executableSlaves.length bits)
		val executableFromIbusSlaveSelects = out Bits(executableSlaves.length bits)
	}

	private val dut = new CpuBusBridge(io.bridge.cpu.dbus.config, io.bridge.devices.dbus.config)
	io.bridge <> dut.io
	io.dbusSlaves.concat(io.executableSlaves).foreach { slave =>
		slave.ACK := False
		slave.DAT_MISO := 0
	}

	val dbusToExecutableBridgeAddress = 0x80
	val firstDbusSlaveAddress = Random.between(0, 10)
	private val dbusSlaveMap = dut.dbusDeviceMapFor(
		(dbus => dbus.ADR >= dbusToExecutableBridgeAddress),
		io.dbusSlaves.zipWithIndex.map { case(slave, index) => (slave -> ((dbus: Wishbone) => dbus.ADR === firstDbusSlaveAddress + index)) }:_*)

	io.dbusSlaveSelects := dbusSlaveMap.io.masters.head.slaveSelects.asBits

	private val dbusSlaveSelectIndexForExecutableBus = dbusSlaveMap.slaves.indexOf(dut.io.devices.dbusToExecutableBridge)

	val dbusSlaveSelectMaskForExecutableBus = 1 << dbusSlaveSelectIndexForExecutableBus

	def dbusSlaveSelectMaskFor(index: Int) = 1 << (if (index < dbusSlaveSelectIndexForExecutableBus) index else index + 1)

	val firstExecutableSlaveAddressFromDbus = dbusToExecutableBridgeAddress + Random.between(10, 20)
	val firstExecutableSlaveAddressFromIbus = Random.between(0, 10)
	private val executableSlaves = io.executableSlaves.zipWithIndex.map { case(slave, index) =>
		(slave -> (
			(dbus: Wishbone) => dbus.ADR === firstExecutableSlaveAddressFromDbus + index,
			(ibus: Wishbone) => ibus.ADR === firstExecutableSlaveAddressFromIbus + index))
	}

	private val executableSlaveMap = dut.executableDeviceMapFor(executableSlaves.head, executableSlaves.tail:_*)
	io.executableFromDbusSlaveSelects := executableSlaveMap.io.masters(0).slaveSelects.asBits
	io.executableFromIbusSlaveSelects := executableSlaveMap.io.masters(1).slaveSelects.asBits

	def executableSlaveSelectMaskFor(index: Int) = 1 << index
}
