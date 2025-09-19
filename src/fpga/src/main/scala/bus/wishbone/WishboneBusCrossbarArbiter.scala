package net.restall.ice40riscvsoc.bus.wishbone

import spinal.core._
import spinal.lib.bus.wishbone.Wishbone

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.bus.{CrossbarArbiter, MasterSlaveMap}
import net.restall.ice40riscvsoc.multiplexing.Encoder

object WishboneBusCrossbarArbiter {
	def apply(busMap: MasterSlaveMap[Wishbone], encoderFactory: Vec[Bool] => Encoder.IoBundle): CrossbarArbiter = {
		encoderFactory.mustNotBeNull("encoderFactory", "Use None (or omit the argument entirely) if you wish to wire encoders to the arbiter manually")

		val arbiter = apply(busMap)
		arbiter.io.slaves.map { slave => // TODO: THIS BLOCK COULD BE AN INSTANCE METHOD ON CrossbarArbiter; IT'S NOT DEPENDENT ON WISHBONE FUNCTIONALITY
			val encoder = encoderFactory(slave.encoder.inputs)
			encoder.mustNotBeNull("encoderFactory", "Encoder factory must not return null")

			slave.encoder.isValid := encoder.isValid
			slave.encoder.output := encoder.output
		}

		arbiter
	}

	def apply(busMap: MasterSlaveMap[Wishbone]): CrossbarArbiter = {
		busMap.mustNotBeNull("busMap", "Wishbone master-slave mappings must be specified")

		val arbiter = new CrossbarArbiter(busMap.masters.length, busMap.slaves.length)
		arbiter.io.slaves.zipWithIndex.foreach { case (slave, slaveIndex) =>
			slave.masters.zip(busMap.masters).zip(busMap.io.masters).foreach { case ((ioMaster, wbMaster), bmMaster) =>
				ioMaster.request := wbMaster.CYC && bmMaster.isValid && bmMaster.index === slaveIndex
			}
		}

		arbiter
	}
}
