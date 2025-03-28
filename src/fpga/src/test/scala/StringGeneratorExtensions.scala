package uk.co.lophtware.msfreference.tests

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._

object StringGeneratorExtensions {
	implicit class WhitespaceExtensions(str: String) {
		str.mustNotBeNull("str")

		def wrappedInWhitespace: String = StringGenerator.anyWhitespace() + str + StringGenerator.anyWhitespace()
	}
}
