package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._
import spinal.core.sim._
import spinal.lib.wishbone.sim.{WishboneDriver, WishboneTransaction}

import uk.co.lophtware.msfreference.tests.IterableTableExtensions._
import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.{Ice40Spram16k16, Ice40Spram16k16WishboneBusAdapter}

class Ice40Spram16k16WishboneBusAdapterFactoryTest extends AnyFlatSpec
	with SimulationFixture[Ice40Spram16k16WishboneBusAdapterFactoryFixture]
	with TableDrivenPropertyChecks {

	protected override def dutFactory() = new Ice40Spram16k16WishboneBusAdapterFactoryFixture()

	"Ice40Spram16k16WishboneBusAdapter companion's apply() method" must "not accept a null SPRAM" in simulator { fixture =>
		val thrown = the [IllegalArgumentException] thrownBy Ice40Spram16k16WishboneBusAdapter(null)
		thrown.getMessage must (include("arg=spram") and include("null"))
	}

	it must "wire the SPRAM's AD lines" in simulator { fixture =>
		val address = fixture.anyAddress()
		fixture.io.adapter.ADR #= address
		sleep(1)
		fixture.io.spram.AD.toInt must be(address)
	}

	it must "wire the SPRAM's DI lines" in simulator { fixture =>
		val data = fixture.anyData()
		fixture.io.adapter.DAT_MOSI #= data
		sleep(1)
		fixture.io.spram.DI.toInt must be(data)
	}

	it must "wire the SPRAM's MASKWE lines" in simulator { fixture =>
		val mask = fixture.anyWriteMask()
		fixture.io.adapter.SEL #= mask
		sleep(1)
		fixture.io.spram.MASKWE.toInt must be(mask)
	}

	private val booleans = Seq(true, false).asTable("value")

	it must "wire the SPRAM's WE line" in simulator { fixture =>
		fixture.io.adapter.STB #= true
		forAll(booleans) { (value: Boolean) =>
			fixture.io.adapter.WE #= value
			sleep(1)
			fixture.io.spram.WE.toBoolean must be(value)
		}
	}

	it must "wire the SPRAM's CS line" in simulator { fixture =>
		forAll(booleans) { (value: Boolean) =>
			fixture.io.adapter.CYC #= value
			sleep(1)
			fixture.io.spram.CS.toBoolean must be(value)
		}
	}

	it must "wire the SPRAM's DO lines" in simulator { fixture =>
		val address = fixture.anyAddress()
		val word = fixture.anyData()

		fixture.io.adapter.SEL #= (1 << fixture.io.adapter.SEL.getWidth) - 1
		fixture.wireStimuli()
		val driver = new WishboneDriver(fixture.io.adapter, fixture.clockDomain)
		fork {
			fixture.clockDomain.waitSampling()
			driver.sendBlockAsMaster(new WishboneTransaction(address, word), we=true)
			fixture.clockDomain.waitSampling()
			driver.sendBlockAsMaster(new WishboneTransaction(address), we=false)
			val readback = WishboneTransaction.sampleAsMaster(fixture.io.adapter)
			readback.data must be(word)
		}.join()
	}
}
