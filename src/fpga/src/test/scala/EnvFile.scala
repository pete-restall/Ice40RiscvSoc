package net.restall.ice40riscvsoc.tests

import scala.io.Source
import scala.util.Using

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._

class EnvFile(filename: String) {
	filename.mustBeSpecified("filename")

	private val envFileLines = Using(Source.fromFile(filename)) { file => file.getLines().toList }
	private val envVarsFromFile = KeyValuePairsFrom(envFileLines.getOrElse(List[String]()))

	def apply(key: String): String = {
		key.mustBeSpecified("key")
		envVarsFromFile(key)
	}
}

object EnvFile {
	lazy val default = new EnvFile(".env")
}
