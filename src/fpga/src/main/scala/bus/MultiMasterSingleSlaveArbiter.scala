package uk.co.lophtware.msfreference.bus

import spinal.core._
import spinal.lib.master

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.multiplexing.Encoder

class MultiMasterSingleSlaveArbiter(numberOfMasters: Int) extends Component {
	val io = new MultiMasterSingleSlaveArbiter.IoBundle(numberOfMasters)

	val grantedMasterIndex = Reg(UInt(io.encoder.output.getWidth bits)) init(0)
	io.grantedMasterIndex := grantedMasterIndex
	if (numberOfMasters > 1) {
		switch(grantedMasterIndex) {
			(0 until numberOfMasters).foreach { index =>
				is(index) {
					when(io.encoder.isValid && !io.masters(index).request) {
						grantedMasterIndex := io.encoder.output
					}
				}
			}
		}

		io.masters.zipWithIndex.foreach { case (master, index) =>
			io.encoder.inputs(index) := master.request
			master.isError := master.request && !io.encoder.isValid
			master.isStalled := master.request && grantedMasterIndex =/= index
			master.isGranted := master.request && grantedMasterIndex === index
		}
	} else {
		grantedMasterIndex := 0
		io.masters.zipWithIndex.foreach { case (master, index) =>
			io.encoder.inputs(index) := True
			master.isError := False
			master.isStalled := False
			master.isGranted := True
		}
	}
}

object MultiMasterSingleSlaveArbiter {
	case class IoBundle(private val numberOfMasters: Int) extends Bundle {
		if (numberOfMasters < 1) {
			throw numberOfMasters.isOutOfRange("numberOfMasters", "Number of masters must be at least 1")
		}

		val encoder = new Encoder.IoBundle(numberOfMasters).flip()
		val grantedMasterIndex = out UInt(encoder.output.getWidth bits)
		val masters = Array.fill(numberOfMasters) { new MasterIoBundle() }
	}

	case class MasterIoBundle() extends Bundle {
		val request = in Bool()
		val isError = out Bool()
		val isStalled = out Bool()
		val isGranted = out Bool()
	}
}
