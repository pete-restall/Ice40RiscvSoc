ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "2.12.18"
ThisBuild / organization := "net.restall"

val spinalVersion = "1.13.0"

lazy val root = (project in file("."))
	.settings(
		name := "Ice40RiscvSoc",
		description := "A RISC-V SoC for the iCE40 FPGA Family",
		fork := true,
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

lazy val vexriscv = RootProject(uri("https://github.com/SpinalHDL/VexRiscv.git#2130484fe93c04edc0f17a4991108fdef9db89b3"))

// TODO: Referencing the SpinalHDL/VexRiscv as a sub-project means that we need to match its Scala version, otherwise the
// dependency will _not_ be found.  A whole afternoon wasted on this - the error message complains about missing remote
// Maven and local Ivy packages, despite referencing the project as source not a library.
//
// If the versions need to differ, consider adding the git submodule back in and modify the build definition:
//
//     lazy val vexriscv = ProjectRef(file("src/thirdparty/vexriscv"), "root")
//
// This is not great either.  Ideally we want to take SpinalHDL as a cross-compiled package.
