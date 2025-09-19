# A Work-in-Progress RISC-V System-on-Chip (SoC) for the iCE40 Family of FPGAs

This repository is an attempt to resurrect the SoC portion of an old project.  It started life back in 2022 as part of an MSF-based frequency reference.

The frequency reference project was basically abandoned in 2022 when other things got in the way.  Unfortunately I was in the middle of writing the state machine and formal proofs for the flash controller so the abandonment wasn't even on a commit boundary.  Three years later and I'm scratching my head somewhat, so the first big effort is to get this whole thing into a known-good state that builds and tests successfully, before finishing the flash controller and upgrading all versions of third-party software (SpinalHDL, Radiant, Scala, ...) to the latest versions.

The original project had some hand-cranked RISC-V assembly / machine code residing in the BRAM that could blink the LEDs on the iCE Sugar dev board, so if I can at least get the flash controller finished then it ought to be a good starting point to build out and experimenting with software - ie. [another one of my long-running background projects](https://github.com/pete-restall/smeg).

Uses SpinalHDL to produce Verilog for both simulation and synthesis.  Yosys and Icarus Verilog handle the co-simulation, tests and formal proofs.  Lattice Radiant handles the synthesis workflow and bitstream generation.
