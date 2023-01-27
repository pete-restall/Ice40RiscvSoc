package uk.co.lophtware.msfreference.core

import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneAdapter, WishboneConfig}
import spinal.lib.{master, slave}

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusMasterSlaveMap

class CpuBusBridge(cpuBusConfig: WishboneConfig, deviceBusConfig: WishboneConfig) extends Component {
	val io = new CpuBusBridge.IoBundle(cpuBusConfig, deviceBusConfig)
	noIoPrefix()

	private val ibusAdapter = WishboneAdapter(
		io.cpu.ibus,
		io.devices.ibus,
		allowAddressResize=true,
		allowDataResize=true,
		allowTagResize=true)

	private val dbusAdapter = WishboneAdapter(
		io.cpu.dbus,
		io.devices.dataOnly,
		allowAddressResize=true,
		allowDataResize=true,
		allowTagResize=true)

	io.devices.executable << io.devices.dbusToExecutableBridge

	def dbusDeviceMapFor(
		executableDeviceSelector: Wishbone => Bool,
		dataOnlyDeviceSelectors: (Wishbone, Wishbone => Bool)*): WishboneBusMasterSlaveMap = {

/* TODO: THIS SORT OF THING:
		val instructions = slave(new Wishbone(bridge.io.devices.executable.config))
		val data = slave(new Wishbone(bridge.io.devices.dataOnly.config))

		val dbusDeviceMap = bridge.dbusDeviceMapFor(
			executableDeviceSelector=dbus => dbus.ADR(3),
			(data -> (dbus => !dbus.ADR(3))))

		val executableDeviceMap = bridge.executableDeviceMapFor(
			(instructions -> (dbus => dbus.ADR(3), ibus => ibus.ADR(3))))
*/
		null
	}

	def executableDeviceMapFor(
		firstDevice: (Wishbone, (Wishbone => Bool, Wishbone => Bool)),
		otherDevices: (Wishbone, (Wishbone => Bool, Wishbone => Bool))*): WishboneBusMasterSlaveMap = {

		null
	}
}

object CpuBusBridge {
	case class IoBundle(private val cpuBusConfig: WishboneConfig, private val deviceBusConfig: WishboneConfig) extends Bundle {
		cpuBusConfig.mustNotBeNull("cpuBusConfig", "Wishbone CPU bus configuration must be specified, with I and D busses both having the same configuration")
		deviceBusConfig.mustNotBeNull("deviceBusConfig", "Wishbone device bus configuration must be specified")

		val cpu = new Bundle {
			val ibus = slave(new Wishbone(cpuBusConfig))
			val dbus = slave(new Wishbone(cpuBusConfig))
		}

		val devices = new Bundle {
			val dataOnly = master(new Wishbone(deviceBusConfig))
			val executable = master(new Wishbone(deviceBusConfig))

			val ibus = master(new Wishbone(deviceBusConfig))
			val dbusToExecutableBridge = slave(new Wishbone(deviceBusConfig))
		}
	}

	// TODO: def apply(cpu: IHaveCpuBusses, deviceBusConfig: WishboneConfig): CpuBusBridge = ...  Make a trait IHaveCpuBusses with ibus and dbus members, then have Cpu.IoBundle inherit from it as well
}
