package uk.co.lophtware.msfreference.tests.core

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import spinal.core._

import uk.co.lophtware.msfreference.core.CpuBusBridge
import uk.co.lophtware.msfreference.tests.bus.wishbone.WishboneConfigTestDoubles
import uk.co.lophtware.msfreference.tests.simulation._

class CpuBusBridgeTest extends AnyFlatSpec with NonSimulationFixture {
	"CpuBusBridge" must "not use the 'io' prefix for signals" in spinalContext { () =>
		val cpu = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		cpu.io.name must be("")
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
		val cpu = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		cpu.io.cpu.dbus.isMasterInterface must be(false)
	}

	it must "have a CPU data bus configuration the same as passed to the constructor" in spinalContext { () =>
		val cpuBusConfig = dummyCpuBusConfig()
		val cpu = new CpuBusBridge(cpuBusConfig, dummyDeviceBusConfig())
		cpu.io.cpu.dbus.config must be(cpuBusConfig)
	}

	it must "have a slave for connecting to the CPU's instruction bus" in spinalContext { () =>
		val cpu = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		cpu.io.cpu.ibus.isMasterInterface must be(false)
	}

	it must "have a CPU instruction bus configuration the same as passed to the constructor" in spinalContext { () =>
		val cpuBusConfig = dummyCpuBusConfig()
		val cpu = new CpuBusBridge(cpuBusConfig, dummyDeviceBusConfig())
		cpu.io.cpu.ibus.config must be(cpuBusConfig)
	}

	it must "not have the same instance for both CPU busses" in spinalContext { () =>
		val cpu = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		cpu.io.cpu.dbus mustNot be(cpu.io.cpu.ibus)
	}

	it must "have a master for connecting to the devices shared between the instruction and data busses" in spinalContext { () =>
		val cpu = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		cpu.io.devices.executable.isMasterInterface must be(true)
	}

	it must "have an executable device bus configuration the same as passed to the constructor" in spinalContext { () =>
		val deviceBusConfig = dummyDeviceBusConfig()
		val cpu = new CpuBusBridge(dummyCpuBusConfig(), deviceBusConfig)
		cpu.io.devices.executable.config must be(deviceBusConfig)
	}

	it must "have a master for connecting to the devices dedicated to the data bus" in spinalContext { () =>
		val cpu = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		cpu.io.devices.dataOnly.isMasterInterface must be(true)
	}

	it must "have a data-only device bus configuration the same as passed to the constructor" in spinalContext { () =>
		val deviceBusConfig = dummyDeviceBusConfig()
		val cpu = new CpuBusBridge(dummyCpuBusConfig(), deviceBusConfig)
		cpu.io.devices.dataOnly.config must be(deviceBusConfig)
	}

	it must "not have the same instance for both device busses" in spinalContext { () =>
		val cpu = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		cpu.io.devices.executable mustNot be(cpu.io.devices.dataOnly)
	}

	it must "have a master for the adapter instruction bus" in spinalContext { () =>
		val cpu = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		cpu.io.devices.ibus.isMasterInterface must be(true)
	}

	it must "have an adapted instruction bus configuration the same as the device bus configuration passed to the constructor" in spinalContext { () =>
		val deviceBusConfig = dummyDeviceBusConfig()
		val cpu = new CpuBusBridge(dummyCpuBusConfig(), deviceBusConfig)
		cpu.io.devices.ibus.config must be(deviceBusConfig)
	}

	it must "have a slave for bridging the data bus to the executable bus" in spinalContext { () =>
		val cpu = new CpuBusBridge(dummyCpuBusConfig(), dummyDeviceBusConfig())
		cpu.io.devices.dbusToExecutableBridge.isMasterInterface must be(false)
	}

	it must "have a data-to-executable bridge configuration the same as the device bus configuration passed to the constructor" in spinalContext { () =>
		val deviceBusConfig = dummyDeviceBusConfig()
		val cpu = new CpuBusBridge(dummyCpuBusConfig(), deviceBusConfig)
		cpu.io.devices.dbusToExecutableBridge.config must be(deviceBusConfig)
	}
}
