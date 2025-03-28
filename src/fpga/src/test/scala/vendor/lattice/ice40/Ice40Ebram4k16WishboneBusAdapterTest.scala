package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._

import uk.co.lophtware.msfreference.tests.IterableTableExtensions._
import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.{Ice40Ebram4k, Ice40Ebram4k16WishboneBusAdapter}

class Ice40Ebram4k16WishboneBusAdapterTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks {
	"Ice40Ebram4k16WishboneBusAdapter" must "not use the 'io' prefix for signals" in spinalContext {
		val adapter = new Ice40Ebram4k16WishboneBusAdapter()
		adapter.io.name must be("")
	}

	"Ice40Ebram4k16WishboneBusAdapter Wishbone bus" must "have an 8-bit address width" in spinalContext {
		val adapter = new Ice40Ebram4k16WishboneBusAdapter()
		adapter.io.wishbone.ADR.getWidth must be(8)
	}

	it must "have a 16-bit MISO data width" in spinalContext {
		val adapter = new Ice40Ebram4k16WishboneBusAdapter()
		adapter.io.wishbone.DAT_MISO.getWidth must be(16)
	}

	it must "have a 16-bit MOSI data width" in spinalContext {
		val adapter = new Ice40Ebram4k16WishboneBusAdapter()
		adapter.io.wishbone.DAT_MOSI.getWidth must be(16)
	}

	it must "have a 16-bit write-mask width" in spinalContext {
		val adapter = new Ice40Ebram4k16WishboneBusAdapter()
		adapter.io.wishbone.SEL.getWidth must be(16)
	}

	it must "not have cycle tags" in spinalContext {
		val adapter = new Ice40Ebram4k16WishboneBusAdapter()
		adapter.io.wishbone.TGC must be(null)
	}

	it must "not have address tags" in spinalContext {
		val adapter = new Ice40Ebram4k16WishboneBusAdapter()
		adapter.io.wishbone.TGA must be(null)
	}

	it must "not have MISO data tags" in spinalContext {
		val adapter = new Ice40Ebram4k16WishboneBusAdapter()
		adapter.io.wishbone.TGD_MISO must be(null)
	}

	it must "not have MOSI data tags" in spinalContext {
		val adapter = new Ice40Ebram4k16WishboneBusAdapter()
		adapter.io.wishbone.TGD_MOSI must be(null)
	}

	it must "not have a Burst-Type Extension (BTE)" in spinalContext {
		val adapter = new Ice40Ebram4k16WishboneBusAdapter()
		adapter.io.wishbone.BTE must be(null)
	}

	it must "not have a Cycle Type Identifier (CTI)" in spinalContext {
		val adapter = new Ice40Ebram4k16WishboneBusAdapter()
		adapter.io.wishbone.CTI must be(null)
	}

	it must "not have an error flag" in spinalContext {
		val adapter = new Ice40Ebram4k16WishboneBusAdapter()
		adapter.io.wishbone.ERR must be(null)
	}

	it must "not have a retry flag" in spinalContext {
		val adapter = new Ice40Ebram4k16WishboneBusAdapter()
		adapter.io.wishbone.RTY must be(null)
	}

	it must "not have an lock flag" in spinalContext {
		val adapter = new Ice40Ebram4k16WishboneBusAdapter()
		adapter.io.wishbone.LOCK must be(null)
	}

	it must "not have a stall flag" in spinalContext { // TODO: THE STALL FLAG DETERMINES IF THE BUS IS PIPELINED...WE WANT PIPELINING...
		val adapter = new Ice40Ebram4k16WishboneBusAdapter()
		adapter.io.wishbone.STALL must be(null)
	}

	"Ice40Ebram4k16WishboneBusAdapter companion's apply() method" must "not accept a null EBRAM" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy Ice40Ebram4k16WishboneBusAdapter(null)
		thrown.getMessage must (include("arg=ebram") and include("null"))
	}

	private val invalidWidths = Seq(2 bits, 4 bits, 8 bits).asTable("invalidWidth")

	it must "not accept EBRAMs with read widths other than 16 bits" in spinalContext {
		forAll(invalidWidths) { (invalidReadWidth: BitCount) =>
			val invalidEbram = new Ice40Ebram4k(invalidReadWidth, 16 bits)
			val thrown = the [IllegalArgumentException] thrownBy(Ice40Ebram4k16WishboneBusAdapter(invalidEbram))
			thrown.getMessage must (include("arg=ebram") and include("16 bits"))
		}
	}

	it must "not accept EBRAMs with write widths other than 16 bits" in spinalContext {
		forAll(invalidWidths) { (invalidWriteWidth: BitCount) =>
			val invalidEbram = new Ice40Ebram4k(16 bits, invalidWriteWidth)
			val thrown = the [IllegalArgumentException] thrownBy(Ice40Ebram4k16WishboneBusAdapter(invalidEbram))
			thrown.getMessage must (include("arg=ebram") and include("16 bits"))
		}
	}
}
