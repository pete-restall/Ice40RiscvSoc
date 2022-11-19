package uk.co.lophtware.msfreference.vendor.lattice.ice40

import spinal.core._

class Ice40Spram16k16 extends BlackBox {
	val io = new Ice40Spram16k16.IoBundle

	noIoPrefix()
	setDefinitionName("SP256K")
	mapCurrentClockDomain(clock=io.CK)
}

object Ice40Spram16k16 {
	case class IoBundle() extends Bundle {
		val AD = in UInt(14 bits)
		val DI = in Bits(16 bits)
		val MASKWE = in UInt(4 bits)
		val WE = in Bool()
		val CS = in Bool()
		val CK = in Bool()
		val STDBY = in Bool()
		val SLEEP = in Bool()
		val PWROFF_N = in Bool()
		val DO = out Bits(16 bits)
	}
}
