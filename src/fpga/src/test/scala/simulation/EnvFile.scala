package uk.co.lophtware.msfreference.tests.simulation

import scala.io.Source
import scala.util.Using

class EnvFile(filename: String) {
	private val envFileLines = Using(Source.fromFile(filename)) { file => file.getLines().toList }
	private val envVarsFromFile = KeyValuePairsFrom(envFileLines.getOrElse(List[String]()))

	def apply(key: String): String = envVarsFromFile(key)
}
