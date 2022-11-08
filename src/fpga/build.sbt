ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "2.13.10"
ThisBuild / organization := "uk.co.lophtware"

lazy val msfReference = (project in file("."))
	.settings(
		name := "MSF Reference",
		fork := true,
		envVars ++= Map(
			"SPINALSIM_WORKSPACE" -> "target/sim-workspace",
			"SIMULATOR_VERILOG_LIBRARY_PATH" -> "/opt/lattice/radiant/3.1/cae_library/simulation/verilog/iCE40UP",
			"SIMULATOR_VERILOG_INCLUDE_PATH" -> "/opt/lattice/radiant/3.1/cae_library/simulation/verilog/iCE40UP",
			"SIMULATOR_VERILOG_PATCHED_INCLUDE_PATH" -> "src/thirdparty/lattice/radiant"),
		libraryDependencies ++= Seq(
			"org.scalatest" %% "scalatest" % "latest.release" % Test,
			"com.github.spinalhdl" % "spinalhdl-core_2.13" % "latest.release",
			"com.github.spinalhdl" % "spinalhdl-lib_2.13" % "latest.release",
			compilerPlugin("com.github.spinalhdl" % "spinalhdl-idsl-plugin_2.13" % "latest.release")
		)
	)
