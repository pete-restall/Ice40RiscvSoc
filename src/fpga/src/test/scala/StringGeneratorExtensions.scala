package net.restall.ice40riscvsoc.tests

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._

object StringGeneratorExtensions {
	implicit class WhitespaceExtensions(str: String) {
		str.mustNotBeNull("str")

		def wrappedInWhitespace: String = StringGenerator.anyWhitespace() + str + StringGenerator.anyWhitespace()
	}
}
