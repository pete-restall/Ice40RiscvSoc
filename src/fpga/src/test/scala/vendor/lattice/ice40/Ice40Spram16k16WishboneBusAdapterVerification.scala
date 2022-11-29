package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.formal._

import uk.co.lophtware.msfreference.tests.formal._

class Ice40Spram16k16WishboneBusAdapterVerification extends AnyFlatSpec with FormalVerificationFixture[Ice40Spram16k16WishboneBusAdapterFormalFixture] {
	protected override def dutFactory() = new Ice40Spram16k16WishboneBusAdapterFormalFixture().withStimuli

	"Ice40Spram16k16WishboneBusAdapter Wishbone master interface" must "hold CYC inactive in reset" in verification { dut =>
		when (dut.clockDomain.isResetActive) {
			formallyAssume(!dut.io.wishbone.CYC)
		}
	}

	it must "hold WE inactive in reset" in verification { dut =>
		when (dut.clockDomain.isResetActive) {
			formallyAssume(!dut.io.wishbone.WE)
		}
	}

	it must "hold STB inactive in reset" in verification { dut =>
		when (dut.clockDomain.isResetActive) {
			formallyAssume(!dut.io.wishbone.STB)
		}
	}

	it must "not activate CYC within the first clock edge after reset" in verification { dut =>
		formallyAssume(!pastValid && !dut.io.wishbone.CYC)
		formallyAssume(pastValid && past(dut.clockDomain.isResetActive) && !dut.io.wishbone.CYC)
	}

	it must "activate STB only during a cycle" in verification { dut =>
		formallyAssume(!dut.io.wishbone.STB || (dut.io.wishbone.STB && dut.io.wishbone.CYC))
	}

	it must "not change WE during STB" in verification { dut =>
		when (pastValid && past(dut.io.wishbone.STB)) {
			formallyAssume(dut.io.wishbone.STB === past(dut.io.wishbone.STB))
		}
	}

	it must "not change STB during STALL" in verification { dut =>
		if (dut.io.wishbone.STALL != null) {
			when (pastValid && past(dut.io.wishbone.STALL)) {
				formallyAssume(dut.io.wishbone.STB === past(dut.io.wishbone.STB))
			}
		}
	}

	it must "not change WE during STALL" in verification { dut =>
		if (dut.io.wishbone.STALL != null) {
			when (pastValid && past(dut.io.wishbone.STALL)) {
				formallyAssume(dut.io.wishbone.WE === past(dut.io.wishbone.WE))
			}
		}
	}

	it must "not change ADR during STALL" in verification { dut =>
		if (dut.io.wishbone.STALL != null) {
			when (pastValid && past(dut.io.wishbone.STALL)) {
				formallyAssume(dut.io.wishbone.ADR === past(dut.io.wishbone.ADR))
			}
		}
	}

	it must "not change MOSI during STALL" in verification { dut =>
		if (dut.io.wishbone.STALL != null) {
			when (pastValid && past(dut.io.wishbone.STALL)) {
				formallyAssume(dut.io.wishbone.DAT_MOSI === past(dut.io.wishbone.DAT_MOSI))
			}
		}
	}

	"Ice40Spram16k16WishboneBusAdapter Wishbone slave interface" must "hold ACK inactive in reset" in verification { dut =>
		when (dut.clockDomain.isResetActive) {
			formallyAssert(!dut.io.wishbone.ACK)
		}
	}

	it must "hold ERR inactive in reset" in verification { dut =>
		if (dut.io.wishbone.ERR != null) {
			when (dut.clockDomain.isResetActive) {
				formallyAssert(!dut.io.wishbone.ERR)
			}
		}
	}

	it must "hold RTY inactive in reset" in verification { dut =>
		if (dut.io.wishbone.RTY != null) {
			when (dut.clockDomain.isResetActive) {
				formallyAssert(!dut.io.wishbone.RTY)
			}
		}
	}

	it must "not assert ACK and ERR at the same time" in verification { dut =>
		if (dut.io.wishbone.ERR != null) {
			formallyAssert((!dut.io.wishbone.ACK && !dut.io.wishbone.ERR) || (dut.io.wishbone.ACK ^ dut.io.wishbone.ERR))
		}
	}

	it must "not assert ACK and RTY at the same time" in verification { dut =>
		if (dut.io.wishbone.RTY != null) {
			formallyAssert((!dut.io.wishbone.ACK && !dut.io.wishbone.RTY) || (dut.io.wishbone.ACK ^ dut.io.wishbone.RTY))
		}
	}

	it must "not assert ERR and RTY at the same time" in verification { dut =>
		if (dut.io.wishbone.ERR != null && dut.io.wishbone.RTY != null) {
			formallyAssert((!dut.io.wishbone.ERR && !dut.io.wishbone.RTY) || (dut.io.wishbone.ERR ^ dut.io.wishbone.RTY))
		}
	}

	it must "not assert ACK without CYC" in verification { dut =>
		formallyAssert(!dut.io.wishbone.ACK || (dut.io.wishbone.ACK && dut.io.wishbone.CYC))
	}

	it must "not assert ERR without CYC" in verification { dut =>
		if (dut.io.wishbone.ERR != null) {
			formallyAssert(!dut.io.wishbone.ERR || (dut.io.wishbone.ERR && dut.io.wishbone.CYC))
		}
	}

	it must "not assert RTY without CYC" in verification { dut =>
		if (dut.io.wishbone.RTY != null) {
			formallyAssert(!dut.io.wishbone.RTY || (dut.io.wishbone.RTY && dut.io.wishbone.CYC))
		}
	}

	it must "not assert ACK without a previous STB" in verification { dut =>
		formallyAssert(!dut.io.wishbone.ACK || (pastValid && dut.io.wishbone.ACK && past(dut.io.wishbone.STB)))
	}

	it must "not assert ERR without a previous STB" in verification { dut =>
		if (dut.io.wishbone.ERR != null) {
			formallyAssert(!dut.io.wishbone.ERR || (pastValid && dut.io.wishbone.ERR && past(dut.io.wishbone.STB)))
		}
	}

	it must "not assert RTY without a previous STB" in verification { dut =>
		if (dut.io.wishbone.RTY != null) {
			formallyAssert(!dut.io.wishbone.RTY || (pastValid && dut.io.wishbone.RTY && past(dut.io.wishbone.STB)))
		}
	}

	it must "contain the same number of ACKs as STBs when STB is not active on the last clock of CYC" in verification { implicit dut =>
		val numberOfStbs = Reg(UInt(16 bits)) init(0)
		val numberOfAcks = Reg(UInt(16 bits)) init(0)

		when (cycleStarted) {
			numberOfAcks := 0
			numberOfStbs := 0
		}

		when (dut.io.wishbone.CYC) {
			when (dut.io.wishbone.STB) {
				numberOfStbs := numberOfStbs + 1
			}

			when (dut.io.wishbone.ACK) {
				numberOfAcks := numberOfAcks + 1
			}
		}

		when (pastValid && cycleEnded && !cycleEndedWithStb) {
			formallyAssert(numberOfAcks === numberOfStbs)
		}
	}

	private def cycleStarted(implicit dut: Ice40Spram16k16WishboneBusAdapterFormalFixture) = pastValid && dut.io.wishbone.CYC && !past(dut.io.wishbone.CYC)

	private def cycleEndedWithStb(implicit dut: Ice40Spram16k16WishboneBusAdapterFormalFixture) = cycleEnded && past(dut.io.wishbone.STB)

	private def cycleEnded(implicit dut: Ice40Spram16k16WishboneBusAdapterFormalFixture) = pastValid && past(dut.io.wishbone.CYC) && !dut.io.wishbone.CYC

	it must "contain the one less ACK than there were STBs when STB is active on the last clock of CYC" in verification { implicit dut =>
		val numberOfStbs = Reg(UInt(16 bits)) init(0)
		val numberOfAcks = Reg(UInt(16 bits)) init(0)

		when (cycleStarted) {
			numberOfAcks := 0
			numberOfStbs := 0
		}

		when (dut.io.wishbone.CYC) {
			when (dut.io.wishbone.STB) {
				numberOfStbs := numberOfStbs + 1
			}

			when (dut.io.wishbone.ACK) {
				numberOfAcks := numberOfAcks + 1
			}
		}

		when (pastValid && cycleEndedWithStb) {
			formallyAssert(numberOfAcks === numberOfStbs - 1)
		}
	}

	it must "only assert STALL during STB" in verification { dut =>
		if (dut.io.wishbone.STALL != null) {
			formallyAssert(dut.io.wishbone.STB)
		}
	}
}
