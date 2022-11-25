package uk.co.lophtware.msfreference.tests.formal

import org.scalatest._
import spinal.core._
import spinal.core.formal._

import uk.co.lophtware.msfreference.tests.EnvFile

abstract trait FormalVerificationFixture[TDut <: Component] extends TestSuiteMixin with FormalAssertions { this: TestSuite =>
	private var fixtureDut: TDut = _

	abstract override def run(testName: Option[String], args: Args): Status = {
		var status: Status = null
		val runTests = () => super.run(testName, args)
		createSimulation().doVerify(new Component {
			val dut = FormalDut(dutFactory())
			fixtureDut = dut

			assumeInitial(ClockDomain.current.isResetActive)
			status = runTests()
		})
		status
	}

	private def createSimulation(): SpinalFormalConfig = FormalVerificationFixture.createSimulation()

	protected def dutFactory(): TDut = ???

	protected def verification(test: TDut => Any) = test(fixtureDut)
}

object FormalVerificationFixture {
	private val envVars = sys.env.withDefault(unknown => EnvFile.default(unknown))

	private val config = SpinalConfig(
		defaultClockDomainFrequency = FixedFrequency(100 MHz),
		defaultConfigForClockDomains = ClockDomainConfig(
			clockEdge = RISING,
			resetKind = ASYNC,
			resetActiveLevel = LOW,
			softResetActiveLevel = LOW,
			clockEnableActiveLevel = HIGH)).includeFormal

	def createSimulation() = new SpinalFormalConfig()
		.withSymbiYosys
		.withBMC(depth=100)
		.workspacePath(envVars("SPINALSIM_WORKSPACE"))
		.withConfig(config)
}
