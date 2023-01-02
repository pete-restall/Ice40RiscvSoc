package uk.co.lophtware.msfreference.tests.bus.wishbone

import scala.util.Random

import spinal.core._
import spinal.lib.bus.wishbone.WishboneConfig

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusSelMappingAdapter

class WishboneBusSelMappingAdapterFixture(masterConfig: WishboneConfig, slaveSelWidth: BitCount, slaveSelAddend: Int, dutCreatedViaApplyFactory: Boolean) extends Component {
	val io = new WishboneBusSelMappingAdapter.IoBundle(masterConfig, slaveSelWidth)
	private val dut = createAndWireDut()

	private def createAndWireDut() = if (dutCreatedViaApplyFactory) createWiredDutViaApplyFactory() else createAndWireDutManually()

	private def createWiredDutViaApplyFactory() = WishboneBusSelMappingAdapter(io.master, io.slave, slaveSelMapper)

	private def slaveSelMapper(sel: Bits) = (sel.resize(io.slave.SEL.getWidth).asUInt + slaveSelAddend).asBits

	private def createAndWireDutManually() = {
		val dut = new WishboneBusSelMappingAdapter(masterConfig, slaveSelWidth, slaveSelMapper)
		io <> dut.io
		dut
	}

	val slaveSelMask = (1 << io.slave.SEL.getWidth) - 1

	def anyMasterSelValue() = Random.nextInt(1 << io.master.SEL.getWidth)

	def anyAddress() = Random.nextInt(1 << io.master.ADR.getWidth)

	def anyData() = Random.nextInt(1 << io.master.DAT_MOSI.getWidth)
}
