package uk.co.lophtware.msfreference.tests

import scala.util.Random

object StringGenerator {
	def anyNonNullNonBlank(): String = {
		val str = anyNonNull()
		if (!str.isBlank) str else anyNonNullNonBlank()
	}

	def anyNonNull(): String = Random.nextString(Random.nextInt(100))

	def anyBlank(): String = String.valueOf(anyNumberOf { blanks(Random.nextInt(blanks.length)) }.toArray)

	private def anyNumberOf[T](x: => T) = for (i <- 0 to Random.nextInt(100)) yield x

	private val blanks = Seq[Char](Character.SPACE_SEPARATOR, Character.LINE_SEPARATOR, '\t', '\n', '\u000b', '\f', '\r', '\u001c', '\u001d', '\u001e', '\u001f')

	def anyWhitespace(): String = String.valueOf(atLeastOneOf { blanks(Random.nextInt(blanks.length)) }.toArray)

	private def atLeastOneOf[T](x: => T) = for (i <- 0 to Random.between(1, 100)) yield x
}
