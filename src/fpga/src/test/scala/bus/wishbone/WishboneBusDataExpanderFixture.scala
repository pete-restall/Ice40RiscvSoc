package uk.co.lophtware.msfreference.tests.bus.wishbone

import scala.util.Random

import spinal.core._
import spinal.lib.bus.wishbone.WishboneConfig

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusDataExpander

class WishboneBusDataExpanderFixture(slaveConfig: WishboneConfig, numberOfSlaves: Int, dutCreatedViaApplyFactory: Boolean) extends Component {
	val io = new WishboneBusDataExpander.IoBundle(slaveConfig, numberOfSlaves)
	private val dut = createAndWireDut()

	private def createAndWireDut() = if (dutCreatedViaApplyFactory) createWiredDutViaApplyFactory() else createAndWireDutManually()

	private def createWiredDutViaApplyFactory() = {
		val dut = WishboneBusDataExpander(io.slaves.head, io.slaves.tail.toSeq:_*)
		io.master <> dut.io.master
		dut
	}

	private def createAndWireDutManually() = {
		val dut = new WishboneBusDataExpander(slaveConfig, numberOfSlaves)
		io <> dut.io
		dut
	}

	def anyAddress() = Random.nextLong(1 << slaveConfig.addressWidth)

	def anyMasterData() = slaveDataSliceLsbs.foldLeft(BigInt(0))((acc, i) => acc | (BigInt(anySlaveData()) << i))

	private val slaveDataSliceLsbs = (0 until numberOfSlaves * slaveConfig.dataWidth by slaveConfig.dataWidth)

	def anySlaveData() = Random.nextLong(1 << slaveConfig.dataWidth)

	def anyMasterDataSelect() = slaveDataSelectSliceLsbs.foldLeft(BigInt(0))((acc, i) => acc | (BigInt(anySlaveDataSelect()) << i))

	private val slaveDataSelectSliceLsbs = (0 until numberOfSlaves * slaveConfig.selWidth by slaveConfig.selWidth)

	def anySlaveDataSelect() = Random.nextLong(1 << slaveConfig.selWidth)
}
