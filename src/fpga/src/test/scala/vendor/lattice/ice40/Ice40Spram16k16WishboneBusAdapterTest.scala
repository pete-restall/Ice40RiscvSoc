package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Spram16k16WishboneBusAdapter

class Ice40Spram16k16WishboneBusAdapterTest extends AnyFlatSpec with LightweightSimulationFixture[Ice40Spram16k16WishboneBusAdapterFixture] {
	protected override def dutFactory() = new Ice40Spram16k16WishboneBusAdapterFixture()

	"Ice40Spram16k16WishboneBusAdapter" must "not use the 'io' prefix for signals" in simulator { fixture =>
		val adapter = new Ice40Spram16k16WishboneBusAdapter()
		adapter.io.name must be("")
	}

	"Ice40Spram16k16WishboneBusAdapter Wishbone bus" must "have a 14-bit address width" in simulator { fixture =>
		fixture.io.wishbone.ADR.getWidth must be(14)
	}

	it must "have a 16-bit MISO data width" in simulator { fixture =>
		fixture.io.wishbone.DAT_MISO.getWidth must be(16)
	}

	it must "have a 16-bit MOSI data width" in simulator { fixture =>
		fixture.io.wishbone.DAT_MOSI.getWidth must be(16)
	}

	it must "have a 4-bit write-mask width" in simulator { fixture =>
		fixture.io.wishbone.SEL.getWidth must be(4)
	}

	it must "not have cycle tags" in simulator { fixture =>
		fixture.io.wishbone.TGC must be(null)
	}

	it must "not have address tags" in simulator { fixture =>
		fixture.io.wishbone.TGA must be(null)
	}

	it must "not have MISO data tags" in simulator { fixture =>
		fixture.io.wishbone.TGD_MISO must be(null)
	}

	it must "not have MOSI data tags" in simulator { fixture =>
		fixture.io.wishbone.TGD_MOSI must be(null)
	}

	it must "not have a Burst-Type Extension (BTE)" in simulator { fixture =>
		fixture.io.wishbone.BTE must be(null)
	}

	it must "not have a Cycle Type Identifier (CTI)" in simulator { fixture =>
		fixture.io.wishbone.CTI must be(null)
	}

	it must "not have an error flag" in simulator { fixture =>
		fixture.io.wishbone.ERR must be(null)
	}

	it must "not have a retry flag" in simulator { fixture =>
		fixture.io.wishbone.RTY must be(null)
	}

	it must "not have an lock flag" in simulator { fixture =>
		fixture.io.wishbone.LOCK must be(null)
	}

	it must "not have a stall flag" in simulator { fixture => // TODO: THE STALL FLAG DETERMINES IF THE BUS IS PIPELINED...WE WANT PIPELINING...
		fixture.io.wishbone.STALL must be(null)
	}

	it must "not assert SPRAM CS when Wishbone CYC is low" in simulator { fixture =>
		fixture.io.wishbone.CYC #= false
		sleep(1)
		fixture.io.spram.CS.toBoolean must be(false)
	}

	it must "assert SPRAM CS asynchronously when Wishbone CYC goes high" in simulator { fixture =>
		fixture.io.wishbone.CYC #= true
		sleep(1)
		fixture.io.spram.CS.toBoolean must be(true)
	}

	it must "unassert SPRAM CS asynchronously when Wishbone CYC goes low" in simulator { fixture =>
		fixture.io.wishbone.CYC #= true
		sleep(1)
		fixture.io.wishbone.CYC #= false
		sleep(1)
		fixture.io.spram.CS.toBoolean must be(false)
	}

	it must "set SPRAM AD asynchronously when Wishbone ADR is set" in simulator { fixture =>
		for (address <- List(0x0000, fixture.io.wishbone.ADR.maxValue.toInt, fixture.anyAddress())) {
			fixture.io.wishbone.ADR #= address
			sleep(1)
			fixture.io.spram.AD.toInt must be(address)
		}
	}

	it must "set SPRAM DI asynchronously when Wishbone DAT_MOSI is set" in simulator { fixture =>
		for (data <- List(0x0000, (1 << fixture.io.wishbone.DAT_MOSI.getWidth) - 1, fixture.anyData())) {
			fixture.io.wishbone.DAT_MOSI #= data
			sleep(1)
			fixture.io.spram.DI.toInt must be(data)
		}
	}

	it must "set Wishbone DAT_MISO asynchronously when SPRAM DO is set" in simulator { fixture =>
		for (data <- List(0x0000, (1 << fixture.io.spram.DO.getWidth) - 1, fixture.anyData())) {
			fixture.io.spram.DO #= data
			sleep(1)
			fixture.io.wishbone.DAT_MISO.toInt must be(data)
		}
	}

	it must "assert SPRAM WE asynchronously when Wishbone WE goes high" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		sleep(1)
		fixture.io.spram.WE.toBoolean must be(true)
	}

	it must "unassert SPRAM WE asynchronously when Wishbone WE goes low" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		sleep(1)
		fixture.io.wishbone.WE #= false
		sleep(1)
		fixture.io.spram.WE.toBoolean must be(false)
	}

	it must "set SPRAM MASKWE asynchronously when Wishbone SEL is set" in simulator { fixture =>
		for (nybbleCombination <- 0 to (1 << fixture.io.wishbone.SEL.getWidth) - 1) {
			fixture.io.wishbone.SEL #= nybbleCombination
			sleep(1)
			fixture.io.spram.MASKWE.toInt must be(nybbleCombination)
		}
	}

	it must "not assert Wishbone ACK when Wishbone CYC is low" in simulator { fixture =>
		fixture.io.wishbone.CYC #= false
		sleep(1)
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}

	it must "not assert Wishbone ACK when Wishbone CYC goes high" in simulator { fixture =>
		fixture.io.wishbone.CYC #= true
		sleep(1)
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}

	it must "not assert Wishbone ACK when Wishbone STB is low" in simulator { fixture =>
		fixture.io.wishbone.STB #= false
		sleep(1)
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}

	it must "not assert Wishbone ACK when Wishbone STB goes high" in simulator { fixture =>
		fixture.io.wishbone.STB #= true
		sleep(1)
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}

	it must "assert Wishbone ACK one cycle after Wishbone STB goes high" in simulator { fixture =>
		fixture.clockDomain.forkStimulus(period=10)
		fixture.io.wishbone.CYC #= true
		fixture.io.wishbone.STB #= true
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(false)
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(true)
	}

	it must "unassert Wishbone ACK one cycle after Wishbone STB goes low" in simulator { fixture =>
		fixture.clockDomain.forkStimulus(period=10)
		fixture.io.wishbone.CYC #= true
		fixture.io.wishbone.STB #= true
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.STB #= false
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(true)
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}
}
