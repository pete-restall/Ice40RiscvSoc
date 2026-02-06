ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "2.13.18"
ThisBuild / organization := "net.restall"

val spinalVersion = "1.13.0"

lazy val root = (project in file("."))
	.settings(
		name := "Ice40RiscvSoc",
		description := "A RISC-V SoC for the iCE40 FPGA Family",
		fork := true,
		crossScalaVersions := Nil,
		envVars ++= Map(
			"SPINALSIM_WORKSPACE" -> "target/sim-workspace",
			"SIMULATOR_VERILOG_LIBRARY_PATH" -> "src/thirdparty/lattice/radiant-sim/3.1/cae_library/simulation/verilog/iCE40UP",
			"SIMULATOR_VERILOG_INCLUDE_PATH" -> "src/thirdparty/lattice/radiant-sim/3.1/cae_library/simulation/verilog/iCE40UP",
			"SIMULATOR_VERILOG_PATCHED_INCLUDE_PATH" -> "src/thirdparty/lattice/radiant"),
		libraryDependencies ++= Seq(
			"org.scalatest" %% "scalatest" % "3.2.17" % Test,
			"com.github.spinalhdl" %% "spinalhdl-core" % spinalVersion,
			"com.github.spinalhdl" %% "spinalhdl-lib" % spinalVersion,
			compilerPlugin("com.github.spinalhdl" %% "spinalhdl-idsl-plugin" % spinalVersion)))

	.dependsOn(vexriscv)

lazy val vexriscv = RootProject(uri("https://github.com/pete-restall/fork-VexRiscv.git#7cdc0dfa74551f0e9e46074397499d88e1694683"))
