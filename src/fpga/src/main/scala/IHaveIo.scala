package net.restall.ice40riscvsoc

import spinal.core.Bundle

abstract trait IHaveIo[TIo <: Bundle] {
	def io: TIo = ???
}
