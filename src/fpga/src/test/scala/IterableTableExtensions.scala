package uk.co.lophtware.msfreference.tests

import org.scalatest.prop.TableDrivenPropertyChecks._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import org.scalatest.prop.TableFor1

object IterableTableExtensions {
	implicit class OneColumn[A](rows: Iterable[A]) {
		rows.mustNotBeNull("rows")

		def asTable(header: String) = {
			header.mustBeSpecified("header")
			Table(header) ++ rows
		}
	}

	implicit class TwoColumns[A, B](rows: Iterable[(A, B)]) {
		rows.mustNotBeNull("rows")

		def asTable(header0: String, header1: String) = {
			header0.mustBeSpecified("header0")
			header1.mustBeSpecified("header1")

			Table((header0, header1)) ++ rows
		}
	}

	implicit class ThreeColumns[A, B, C](rows: Iterable[(A, B, C)]) {
		rows.mustNotBeNull("rows")

		def asTable(header0: String, header1: String, header2: String) = {
			header0.mustBeSpecified("header0")
			header1.mustBeSpecified("header1")
			header2.mustBeSpecified("header2")

			Table((header0, header1, header2)) ++ rows
		}
	}
}
