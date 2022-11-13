ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "2.13.10"
ThisBuild / organization := "uk.co.lophtware"

val spinalVersion = "1.7.3"

lazy val root = (project in file("."))
	.settings(
		name := "MsfReference",
		description := "MSF Frequency Reference",
		fork := true,
		envVars ++= Map(
			"SPINALSIM_WORKSPACE" -> "target/sim-workspace",
			"SIMULATOR_VERILOG_LIBRARY_PATH" -> "/opt/lattice/radiant/3.1/cae_library/simulation/verilog/iCE40UP",
			"SIMULATOR_VERILOG_INCLUDE_PATH" -> "/opt/lattice/radiant/3.1/cae_library/simulation/verilog/iCE40UP",
			"SIMULATOR_VERILOG_PATCHED_INCLUDE_PATH" -> "src/thirdparty/lattice/radiant"),
		libraryDependencies ++= Seq(
			"org.scalatest" %% "scalatest" % "3.2.14" % Test,
			"com.github.spinalhdl" %% "spinalhdl-core" % spinalVersion,
			"com.github.spinalhdl" %% "spinalhdl-lib" % spinalVersion,
			compilerPlugin("com.github.spinalhdl" %% "spinalhdl-idsl-plugin" % spinalVersion)))
	.dependsOn(vexriscv)

lazy val vexriscv = ProjectRef(file("src/thirdparty/vexriscv"), "vexriscv")

// TODO: When vexriscv upgrades to 2.13 then it would be good to try referencing the project directly, like:
// lazy val vexriscv = RootProject(uri("https://github.com/SpinalHDL/VexRiscv.git#87c8822f55c0674d24a55b2d83255b60f8a6146e"))
