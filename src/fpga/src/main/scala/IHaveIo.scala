package uk.co.lophtware.msfreference

import spinal.core.Bundle

abstract trait IHaveIo[TIo <: Bundle] {
	def io: TIo = ???
}
