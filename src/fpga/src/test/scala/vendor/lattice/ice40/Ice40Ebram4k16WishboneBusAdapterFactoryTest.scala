package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._
import spinal.core.sim._

import net.restall.ice40riscvsoc.tests.IterableTableExtensions._
import net.restall.ice40riscvsoc.tests.simulation._
import net.restall.ice40riscvsoc.vendor.lattice.ice40.{Ice40Ebram4k, Ice40Ebram4k16WishboneBusAdapter}

class Ice40Ebram4k16WishboneBusAdapterFactoryTest extends AnyFlatSpec
	with SimulationFixture[Ice40Ebram4k16WishboneBusAdapterFactoryFixture]
	with TableDrivenPropertyChecks {

	protected override def dutFactory() = new Ice40Ebram4k16WishboneBusAdapterFactoryFixture()

	"Ice40Ebram4k16WishboneBusAdapter companion's apply() method" must "wire the EBRAM's DI lines" in simulator { fixture =>
		val data = fixture.anyData()
		fixture.io.adapter.DAT_MOSI #= data
		sleep(1)
		fixture.io.ebram.DI.toInt must be(data)
	}

	it must "wire the EBRAM's ADR lines" in simulator { fixture =>
		val address = fixture.anyAddress()
		fixture.io.adapter.ADR #= address
		sleep(1)
		fixture.io.ebram.ADR.toInt must be(address)
	}

	it must "wire the EBRAM's ADW lines" in simulator { fixture =>
		val address = fixture.anyAddress()
		fixture.io.adapter.ADR #= address
		sleep(1)
		fixture.io.ebram.ADW.toInt must be(address)
	}

	it must "wire the EBRAM's MASK_N lines" in simulator { fixture =>
		val allMaskBits = ((1 << fixture.io.ebram.MASK_N.getWidth) - 1)
		val mask = fixture.anyWriteMask()
		fixture.io.adapter.SEL #= mask
		sleep(1)
		fixture.io.ebram.MASK_N.toInt must be(~mask & allMaskBits)
	}

	private val booleans = Seq(true, false).asTable("value")

	it must "wire the EBRAM's CER line" in simulator { fixture =>
		forAll(booleans) { (value: Boolean) =>
			fixture.io.adapter.WE #= !value
			fixture.io.adapter.CYC #= true
			sleep(1)
			fixture.io.ebram.CER.toBoolean must be(value)
		}
	}

	it must "wire the EBRAM's CEW line" in simulator { fixture =>
		forAll(booleans) { (value: Boolean) =>
			fixture.io.adapter.WE #= value
			fixture.io.adapter.CYC #= true
			sleep(1)
			fixture.io.ebram.CEW.toBoolean must be(value)
		}
	}

	it must "wire the EBRAM's RE line" in simulator { fixture =>
		forAll(booleans) { (value: Boolean) =>
			fixture.io.adapter.WE #= false
			fixture.io.adapter.STB #= value
			sleep(1)
			fixture.io.ebram.RE.toBoolean must be(value)
		}
	}

	it must "wire the EBRAM's WE line" in simulator { fixture =>
		forAll(booleans) { (value: Boolean) =>
			fixture.io.adapter.WE #= true
			fixture.io.adapter.STB #= value
			sleep(1)
			fixture.io.ebram.WE.toBoolean must be(value)
		}
	}
}
