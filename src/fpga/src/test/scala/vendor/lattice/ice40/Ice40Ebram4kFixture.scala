package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import spinal.core._

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.tests.vendor.lattice.ice40.ebram.{EbramGiven, EbramStateMachineBuilder}
import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k

class Ice40Ebram4kFixture(readWidth: BitCount, writeWidth: BitCount) extends Component {
	private val dut = new Ice40Ebram4k(readWidth, writeWidth)

	val io = new Ice40Ebram4k.IoBundle(readWidth, writeWidth)
	io <> dut.io
	noIoPrefix()

	val readClockDomain = ClockDomain(clock=io.CKR, clockEnable=io.CER)
	val writeClockDomain = ClockDomain(clock=io.CKW, clockEnable=io.CEW)

	def given = new EbramGiven(new EbramStateMachineBuilder(io, List[Sampling => WithNextSampling]()))
}
