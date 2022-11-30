package uk.co.lophtware.msfreference.tests.formal

import org.scalatest._
import spinal.core._
import spinal.core.formal._

import uk.co.lophtware.msfreference.tests.EnvFile

abstract trait FormalVerificationFixture[TDut <: Component] extends TestSuiteMixin with FormalAssertions { this: TestSuite =>
	private var fixtureDut: TDut = _
	private var runningStage: String = "NONE"

	abstract override def run(testName: Option[String], args: Args): Status = {
		run("BMC", createBoundedModelSimulation(), testName, args)
		.thenRun(run("INDUCTIVE", createInductiveSimulation(), testName, args))
	}

	private def run(stage: String, simulation: SpinalFormalConfig, testName: Option[String], args: Args): Status = {
		runningStage = stage

		var status: Status = null
		val runTests = () => super.run(testName, args)
		simulation.workspaceName(s"${this.getClass.getName}_${stage}").doVerify(new Component {
			val dut = FormalDut(dutFactory())
			fixtureDut = dut

			assumeInitial(ClockDomain.current.isResetActive)
			status = runTests()
		})
		status
	}

	private def createBoundedModelSimulation(): SpinalFormalConfig = FormalVerificationFixture.createBoundedModelSimulation()

	private def createInductiveSimulation(): SpinalFormalConfig = FormalVerificationFixture.createInductiveSimulation()

	override def suiteName: String = runningStage

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

	def createBoundedModelSimulation() = new SpinalFormalConfig()
		.withSymbiYosys
		.withBMC(depth=100)
		.workspacePath(envVars("SPINALSIM_WORKSPACE"))
		.withConfig(config)

	def createInductiveSimulation() = new SpinalFormalConfig()
		.withSymbiYosys
		.withProve(depth=200)
		.workspacePath(envVars("SPINALSIM_WORKSPACE"))
		.withConfig(config)
}
