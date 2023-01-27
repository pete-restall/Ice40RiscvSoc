package uk.co.lophtware.msfreference.tests.core

import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.core.CpuBusBridge
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

class CpuBusBridgeFixture() extends Component {
	val io = new CpuBusBridge.IoBundle(
		cpuBusConfig=new WishboneConfig(addressWidth=16, dataWidth=16, useSTALL=true),
		deviceBusConfig=new WishboneConfig(addressWidth=32, dataWidth=32, useSTALL=false))

	private val dut = new CpuBusBridge(io.cpu.dbus.config, io.devices.dataOnly.config)
	io <> dut.io

	def dbusDeviceMapFor(
		executableDeviceSelector: Wishbone => Bool,
		dataOnlyDeviceSelectors: (Wishbone, Wishbone => Bool)*) = dut.dbusDeviceMapFor(executableDeviceSelector, dataOnlyDeviceSelectors:_*)

	def executableDeviceMapFor(
		firstDevice: (Wishbone, (Wishbone => Bool, Wishbone => Bool)),
		otherDevices: (Wishbone, (Wishbone => Bool, Wishbone => Bool))*) = dut.executableDeviceMapFor(firstDevice, otherDevices:_*)
}
