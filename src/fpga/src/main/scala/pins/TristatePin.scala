package uk.co.lophtware.msfreference.pins

import spinal.core._

object TristatePin {
	case class IoBundle() extends Bundle {
		val inValue = in Bool()
		val outValue = out Bool()
		val isTristated = out Bool()
	}
}
