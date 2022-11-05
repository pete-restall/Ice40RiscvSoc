package uk.co.lophtware.msfreference.tests.simulation

object KeyValuePairsFrom {
	private val comment = """^\s*(#.*)?$""".r
	private val keyValue = """^\s*([^=]+)\s*=\s*(.+)\s*$""".r

	def apply(lines: Seq[String]): Map[String, String] = lines.map(line => line match {
		case comment(_) => None
		case keyValue(key, value) => Some(key -> value)
		case _ => throw new UnsupportedOperationException(s"Expected a comment, blank or key=value but got something unparseable; line=${line}")
	}).flatten.toMap
}
