package net.restall.ice40riscvsoc.tests

import spinal.core.Bundle

import net.restall.ice40riscvsoc.IHaveIo

object HaveIoTestDoubles {
	def stubFor[TIo <: Bundle](stubbedIo: TIo): IHaveIo[TIo] = new IHaveIo[TIo] { override val io = stubbedIo }
}
