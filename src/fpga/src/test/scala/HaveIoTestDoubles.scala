package uk.co.lophtware.msfreference.tests

import spinal.core.Bundle

import uk.co.lophtware.msfreference.IHaveIo

object HaveIoTestDoubles {
	def stubFor[TIo <: Bundle](stubbedIo: TIo): IHaveIo[TIo] = new IHaveIo[TIo] { override val io = stubbedIo }
}
