package net.restall.ice40riscvsoc.tests.bus.wishbone

import scala.util.Random

import spinal.core._
import spinal.lib.bus.wishbone.WishboneConfig

import net.restall.ice40riscvsoc.bus.wishbone.WishboneBusSelMappingAdapter
import net.restall.ice40riscvsoc.tests.bus.wishbone.WishboneBusSelMappingAdapterFixture.DutCreationMethod

class WishboneBusSelMappingAdapterFixture(masterConfig: WishboneConfig, slaveSelWidth: BitCount, slaveSelAddend: Int, dutCreationMethod: DutCreationMethod.Enum) extends Component {
	val io = new WishboneBusSelMappingAdapter.IoBundle(masterConfig, slaveSelWidth)
	private val dut = createAndWireDut()

	private def createAndWireDut() = dutCreationMethod match {
		case DutCreationMethod.FactoryTakingMasterAndSlave => createWiredDutViaApplyFactoryTakingMasterAndSlave()
		case DutCreationMethod.FactoryTakingMaster => createWiredDutViaApplyFactoryTakingMaster()
		case DutCreationMethod.FactoryTakingSlave => createWiredDutViaApplyFactoryTakingSlave()
		case _ => createAndWireDutManually()
	}

	private def createWiredDutViaApplyFactoryTakingMasterAndSlave() = WishboneBusSelMappingAdapter(io.master, io.slave, slaveSelMapper)

	private def slaveSelMapper(sel: Bits) = (sel.resize(io.slave.SEL.getWidth).asUInt + slaveSelAddend).asBits

	private def createWiredDutViaApplyFactoryTakingMaster() = {
		val dut = WishboneBusSelMappingAdapter(io.master, slaveSelWidth, slaveSelMapper)
		io.slave <> dut.io.slave
		dut
	}

	private def createWiredDutViaApplyFactoryTakingSlave() = {
		val dut = WishboneBusSelMappingAdapter(masterConfig.selWidth bits, io.slave, slaveSelMapper)
		io.master <> dut.io.master
		dut
	}

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

object WishboneBusSelMappingAdapterFixture {
	object DutCreationMethod extends Enumeration {
		type Enum = Value
		val Constructor, FactoryTakingMasterAndSlave, FactoryTakingSlave, FactoryTakingMaster = Value
	}
}
