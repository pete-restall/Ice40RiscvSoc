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
		io.devices.dbus,
		allowAddressResize=true,
		allowDataResize=true,
		allowTagResize=true)

	io.devices.executable << io.devices.dbusToExecutableBridge

	def dbusDeviceMapFor(
		executableDeviceSelector: Wishbone => Bool,
		dataOnlyDeviceSelectors: (Wishbone, Wishbone => Bool)*): WishboneBusMasterSlaveMap = {

		executableDeviceSelector.mustNotBeNull("executableDeviceSelector", "A selector for multiplexing the dbus to the executable devices must be specified")
		dataOnlyDeviceSelectors.mustNotContainNull("dataOnlyDeviceSelectors", "When data-only devices are used, all data-only dbus selectors must be specified")

		WishboneBusMasterSlaveMap(
			(io.devices.dbus, executableDeviceSelector, io.devices.dbusToExecutableBridge),
			dataOnlyDeviceSelectors.map(selector => (io.devices.dbus, selector._2, selector._1)):_*)
	}

	def executableDeviceMapFor(
		firstDevice: (Wishbone, (Wishbone => Bool, Wishbone => Bool)),
		otherDevices: (Wishbone, (Wishbone => Bool, Wishbone => Bool))*): WishboneBusMasterSlaveMap = {

		firstDevice.mustNotBeNull("firstDevice", "At least one executable device must be specified to provide the CPU with instructions")
		otherDevices.mustNotContainNull("otherDevices", "When more than one executable device is used all of them must be specified")

		val allDevices = firstDevice +: otherDevices
		val allDeviceMappings = allDevices.flatMap { case (slave, (dbusSelector, ibusSelector)) => Seq(
			(io.devices.executable, dbusSelector, slave),
			(io.devices.ibus, ibusSelector, slave))
		}

		WishboneBusMasterSlaveMap(allDeviceMappings.head, allDeviceMappings.tail:_*)
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
			val ibus = master(new Wishbone(deviceBusConfig))
			val dbus = master(new Wishbone(deviceBusConfig))
			val dbusToExecutableBridge = slave(new Wishbone(deviceBusConfig))
			val executable = master(new Wishbone(deviceBusConfig))
		}
	}

	// TODO: def apply(cpu: IHaveCpuBusses, deviceBusConfig: WishboneConfig): CpuBusBridge = ...  Make a trait IHaveCpuBusses with ibus and dbus members, then have Cpu.IoBundle inherit from it as well
}
