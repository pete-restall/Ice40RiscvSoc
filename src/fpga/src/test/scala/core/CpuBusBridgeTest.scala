package uk.co.lophtware.msfreference.tests.core

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import uk.co.lophtware.msfreference.core.CpuBusBridge
import uk.co.lophtware.msfreference.tests.bus.wishbone.{WishboneTestDoubles, WishboneConfigTestDoubles}
import uk.co.lophtware.msfreference.tests.simulation._

class CpuBusBridgeTest extends AnyFlatSpec with NonSimulationFixture with Inspectors {
	"CpuBusBridge" must "not use the 'io' prefix for signals" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		bridge.io.name must be("")
	}

	private def dummyCpuBusConfig() = WishboneConfigTestDoubles.dummy()

	private def dummyDeviceBusConfig() = WishboneConfigTestDoubles.dummy()

	it must "not accept a null cpuBusConfig" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new CpuBusBridge(null, dummyDeviceBusConfig()))
		thrown.getMessage must (include("arg=cpuBusConfig") and include("null"))
	}

	it must "not accept a null deviceBusConfig" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new CpuBusBridge(dummyCpuBusConfig(), null))
		thrown.getMessage must (include("arg=deviceBusConfig") and include("null"))
	}

	it must "have a slave for connecting to the CPU's data bus" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		bridge.io.cpu.dbus.isMasterInterface must be(false)
	}

	it must "have a CPU data bus configuration the same as passed to the constructor" in spinalContext { () =>
		val cpuBusConfig = dummyCpuBusConfig()
		val bridge = new CpuBusBridge(cpuBusConfig, dummyDeviceBusConfig())
		bridge.io.cpu.dbus.config must be(cpuBusConfig)
	}

	it must "have a slave for connecting to the CPU's instruction bus" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		bridge.io.cpu.ibus.isMasterInterface must be(false)
	}

	it must "have a CPU instruction bus configuration the same as passed to the constructor" in spinalContext { () =>
		val cpuBusConfig = dummyCpuBusConfig()
		val bridge = new CpuBusBridge(cpuBusConfig, dummyDeviceBusConfig())
		bridge.io.cpu.ibus.config must be(cpuBusConfig)
	}

	it must "not have the same instance for both CPU busses" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		bridge.io.cpu.dbus mustNot be(bridge.io.cpu.ibus)
	}

	it must "have a master for connecting to the devices shared between the instruction and data busses" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		bridge.io.devices.executable.isMasterInterface must be(true)
	}

	it must "have an executable device bus configuration the same as passed to the constructor" in spinalContext { () =>
		val deviceBusConfig = dummyDeviceBusConfig()
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), deviceBusConfig)
		bridge.io.devices.executable.config must be(deviceBusConfig)
	}

	it must "have a master for connecting to the devices dedicated to the data bus" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		bridge.io.devices.dataOnly.isMasterInterface must be(true)
	}

	it must "have a data-only device bus configuration the same as passed to the constructor" in spinalContext { () =>
		val deviceBusConfig = dummyDeviceBusConfig()
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), deviceBusConfig)
		bridge.io.devices.dataOnly.config must be(deviceBusConfig)
	}

	it must "not have the same instance for both device busses" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		bridge.io.devices.executable mustNot be(bridge.io.devices.dataOnly)
	}

	it must "have a master for the adapter instruction bus" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		bridge.io.devices.ibus.isMasterInterface must be(true)
	}

	it must "have an adapted instruction bus configuration the same as the device bus configuration passed to the constructor" in spinalContext { () =>
		val deviceBusConfig = dummyDeviceBusConfig()
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), deviceBusConfig)
		bridge.io.devices.ibus.config must be(deviceBusConfig)
	}

	it must "have a slave for bridging the data bus to the executable bus" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		bridge.io.devices.dbusToExecutableBridge.isMasterInterface must be(false)
	}

	it must "have a data-to-executable bridge configuration the same as the device bus configuration passed to the constructor" in spinalContext { () =>
		val deviceBusConfig = dummyDeviceBusConfig()
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), deviceBusConfig)
		bridge.io.devices.dbusToExecutableBridge.config must be(deviceBusConfig)
	}

	"CpuBusBridge dbusDeviceMapFor" must "not accept a null executableDeviceSelector" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		val thrown = the [IllegalArgumentException] thrownBy(bridge.dbusDeviceMapFor(null))
		thrown.getMessage must (include("arg=executableDeviceSelector") and include("null"))
	}

	it must "not accept null dataOnlyDeviceSelectors" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		val thrown = the [IllegalArgumentException] thrownBy(bridge.dbusDeviceMapFor(dummyDbusSelector(), null))
		thrown.getMessage must (include("arg=dataOnlyDeviceSelectors") and include("null"))
	}

	private def dummyDbusSelector() = (_: Wishbone) => False

	it must "not accept any null dataOnlyDeviceSelectors" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		val selectorsContainingNull = Random.shuffle(Seq.fill(Random.between(2, 10)) { dummyDataOnlySelector() } :+ null)
		val thrown = the [IllegalArgumentException] thrownBy(bridge.dbusDeviceMapFor(dummyDbusSelector(), selectorsContainingNull:_*))
		thrown.getMessage must (include("arg=dataOnlyDeviceSelectors") and include("null"))
	}

	private def dummyDataOnlySelector() = (WishboneTestDoubles.dummy(), (_: Wishbone) => False)

	it must "return a WishboneBusMasterSlaveMap containing the data-only bus as the only master" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		val map = bridge.dbusDeviceMapFor(dbus => dbus.ADR =/= 0, anyNumberOfStubDataOnlySelectorsFor(bridge):_*)
		map.masters must equal(Seq(bridge.io.devices.dataOnly))
	}

	private def anyNumberOfStubDataOnlySelectorsFor(bridge: CpuBusBridge): Seq[(Wishbone, Wishbone => Bool)] = anyNumberOfStubDataOnlySelectorsFor(bridge.io.devices.dataOnly.config)

	private def anyNumberOfStubDataOnlySelectorsFor(slaveConfig: WishboneConfig) = Seq.fill(Random.between(0, 10)) { stubDataOnlySelectorFor(slaveConfig) }

	private def stubDataOnlySelectorFor(slaveConfig: WishboneConfig) = (WishboneTestDoubles.stubSlaveWith(slaveConfig), (_: Wishbone) => False)

	it must "return a WishboneBusMasterSlaveMap containing the dbus-to-executable bridge bus as a slave" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		val map = bridge.dbusDeviceMapFor(dbus => dbus.ADR =/= 0, anyNumberOfStubDataOnlySelectorsFor(bridge):_*)
		map.slaves must contain(bridge.io.devices.dbusToExecutableBridge)
	}

	it must "return a WishboneBusMasterSlaveMap containing all data-only slaves" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		val dataOnlySelectors = anyNumberOfStubDataOnlySelectorsFor(bridge)
		val map = bridge.dbusDeviceMapFor(dbus => dbus.ADR =/= 0, dataOnlySelectors:_*)
		forAll(dataOnlySelectors) { selector => map.slaves must contain(selector._1) }
	}

	"CpuBusBridge executableDeviceMapFor" must "not accept a null firstDevice" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		val thrown = the [IllegalArgumentException] thrownBy(bridge.executableDeviceMapFor(null))
		thrown.getMessage must (include("arg=firstDevice") and include("null"))
	}

	it must "not accept null otherDevices" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		val thrown = the [IllegalArgumentException] thrownBy(bridge.executableDeviceMapFor(dummyExecutableDevice(), null))
		thrown.getMessage must (include("arg=otherDevices") and include("null"))
	}

	private def dummyExecutableDevice() = (WishboneTestDoubles.dummy(), ((_: Wishbone) => False, (_: Wishbone) => False))

	it must "not accept any null otherDevices" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		val devicesContainingNull = Random.shuffle(Seq.fill(Random.between(2, 10)) { dummyExecutableDevice() } :+ null)
		val thrown = the [IllegalArgumentException] thrownBy(bridge.executableDeviceMapFor(dummyExecutableDevice(), devicesContainingNull:_*))
		thrown.getMessage must (include("arg=otherDevices") and include("null"))
	}

	it must "return a WishboneBusMasterSlaveMap containing the executable bus and ibus as the only two masters, in that order" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		val map = bridge.executableDeviceMapFor(
			stubExecutableDeviceFor(bridge),
			anyNumberOfStubExecutableDevicesFor(bridge):_*)

		map.masters must equal(Seq(bridge.io.devices.executable, bridge.io.devices.ibus))
	}

	private def stubExecutableDeviceFor(bridge: CpuBusBridge): (Wishbone, (Wishbone => Bool, Wishbone => Bool)) = stubExecutableDeviceFor(bridge.io.devices.executable.config)

	private def stubExecutableDeviceFor(slaveConfig: WishboneConfig) = (WishboneTestDoubles.stubSlaveWith(slaveConfig), ((_: Wishbone) => False, (_: Wishbone) => False))

	private def anyNumberOfStubExecutableDevicesFor(bridge: CpuBusBridge) = atLeastNumberOfStubExecutableDevicesFor(0, bridge)

	private def atLeastNumberOfStubExecutableDevicesFor(atLeast: Int, bridge: CpuBusBridge) = Seq.fill(Random.between(atLeast, atLeast + 10)) { stubExecutableDeviceFor(bridge) }

	it must "return a WishboneBusMasterSlaveMap containing all slaves" in spinalContext { () =>
		val bridge = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		val devices = atLeastNumberOfStubExecutableDevicesFor(2, bridge)
		val map = bridge.executableDeviceMapFor(devices.head, devices.tail:_*)
		forAll(devices) { device => map.slaves must contain(device._1) }
	}
}
