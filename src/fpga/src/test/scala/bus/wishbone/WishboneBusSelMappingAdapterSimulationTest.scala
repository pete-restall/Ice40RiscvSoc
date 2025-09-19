package net.restall.ice40riscvsoc.tests.bus.wishbone

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import net.restall.ice40riscvsoc.bus.wishbone.WishboneBusSelMappingAdapter
import net.restall.ice40riscvsoc.tests.IterableTableExtensions._
import net.restall.ice40riscvsoc.tests.simulation._

class WishboneBusSelMappingAdapterSimulationTest(masterConfig: WishboneConfig, slaveSelWidth: BitCount, slaveSelAddend: Int, dutCreationMethod: WishboneBusSelMappingAdapterFixture.DutCreationMethod.Enum)
	extends AnyFlatSpec
	with LightweightSimulationFixture[WishboneBusSelMappingAdapterFixture]
	with TableDrivenPropertyChecks
	with Inspectors {

	protected override def dutFactory() = new WishboneBusSelMappingAdapterFixture(masterConfig, slaveSelWidth, slaveSelAddend, dutCreationMethod)

	"WishboneBusSelMappingAdapter slave" must "have the SEL mapped by the slaveSelMapper function" in simulator { fixture =>
		if (fixture.io.slave.SEL != null) {
			val masterSelValue = fixture.anyMasterSelValue()
			fixture.io.master.SEL #= masterSelValue
			sleep(1)
			fixture.io.slave.SEL.toLong must be((masterSelValue + slaveSelAddend) & fixture.slaveSelMask)
		}
	}

	it must "have the same ADR as the master" in simulator { fixture =>
		val address = fixture.anyAddress()
		fixture.io.master.ADR #= address
		sleep(1)
		fixture.io.slave.ADR.toLong must be(address)
	}

	private val booleans = Seq(true, false).asTable("value")

	it must "have the same CYC as the master" in simulator { fixture =>
		forAll(booleans) { (value: Boolean) =>
			fixture.io.master.CYC #= value
			sleep(1)
			fixture.io.slave.CYC.toBoolean must be(value)
		}
	}

	it must "have the same STB as the master" in simulator { fixture =>
		forAll(booleans) { (value: Boolean) =>
			fixture.io.master.STB #= value
			sleep(1)
			fixture.io.slave.STB.toBoolean must be(value)
		}
	}

	it must "have the same WE as the master" in simulator { fixture =>
		forAll(booleans) { (value: Boolean) =>
			fixture.io.master.WE #= value
			sleep(1)
			fixture.io.slave.WE.toBoolean must be(value)
		}
	}

	it must "have the same DAT_MOSI as the master" in simulator { fixture =>
		val data = fixture.anyData()
		fixture.io.master.DAT_MOSI #= data
		sleep(1)
		fixture.io.slave.DAT_MOSI.toLong must be(data)
	}

	it must "have the same LOCK as the master" in simulator { fixture =>
		if (masterConfig.useLOCK) {
			forAll(booleans) { (value: Boolean) =>
				fixture.io.master.LOCK #= value
				sleep(1)
				fixture.io.slave.LOCK.toBoolean must be(value)
			}
		}
	}

	"WishboneBusSelMappingAdapter master" must "have the same DAT_MISO as the slave" in simulator { fixture =>
		val data = fixture.anyData()
		fixture.io.slave.DAT_MISO #= data
		sleep(1)
		fixture.io.master.DAT_MISO.toLong must be(data)
	}

	it must "have the same ACK as the slave" in simulator { fixture =>
		forAll(booleans) { (value: Boolean) =>
			fixture.io.slave.ACK #= value
			sleep(1)
			fixture.io.master.ACK.toBoolean must be(value)
		}
	}

	it must "have the same RTY as the slave" in simulator { fixture =>
		if (masterConfig.useRTY) {
			forAll(booleans) { (value: Boolean) =>
				fixture.io.slave.RTY #= value
				sleep(1)
				fixture.io.master.RTY.toBoolean must be(value)
			}
		}
	}

	it must "have the same ERR as the slave" in simulator { fixture =>
		if (masterConfig.useERR) {
			forAll(booleans) { (value: Boolean) =>
				fixture.io.slave.ERR #= value
				sleep(1)
				fixture.io.master.ERR.toBoolean must be(value)
			}
		}
	}

	it must "have the same STALL as the slave" in simulator { fixture =>
		if (masterConfig.useSTALL) {
			forAll(booleans) { (value: Boolean) =>
				fixture.io.slave.STALL #= value
				sleep(1)
				fixture.io.master.STALL.toBoolean must be(value)
			}
		}
	}
}
