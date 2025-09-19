package net.restall.ice40riscvsoc.tests

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._

object TestsPackage {
	private val pkg = getClass.getPackage
	private val packageName = pkg.getName

	def relativeClassNameOf(clazz: Class[_ <: Object]): String = {
		clazz.mustNotBeNull("clazz")
		clazz.getName.startsWith(packageName) match {
			case true => clazz.getName.substring(packageName.length + 1)
			case _ => throw new IllegalArgumentException(s"Class is not part of package; class=${clazz}, package=${pkg}")
		}
	}
}
