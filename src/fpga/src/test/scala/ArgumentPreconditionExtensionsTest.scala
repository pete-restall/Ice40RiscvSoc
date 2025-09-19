package net.restall.ice40riscvsoc.tests

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.tests.StringGeneratorExtensions._

class ArgumentPreconditionExtensionsTest extends AnyFlatSpec {
	"mustNotBeNull(argName)" must "not accept a null argName" in {
		val thrown = the [IllegalArgumentException] thrownBy(anyNonNull().mustNotBeNull(null))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=null")
	}

	private def anyNonNull() = new AnyRef()

	it must "not accept an empty argName" in {
		val thrown = the [IllegalArgumentException] thrownBy(anyNonNull().mustNotBeNull(""))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=")
	}

	it must "not accept a whitespace argName" in {
		val whitespaceArgName = StringGenerator.anyWhitespace()
		val thrown = the [IllegalArgumentException] thrownBy(anyNonNull().mustNotBeNull(whitespaceArgName))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=" + whitespaceArgName)
	}

	it must "not throw an exception when the value is not null" in {
		noException must be thrownBy(anyNonNull().mustNotBeNull(anyArgName()))
	}

	private def anyArgName() = StringGenerator.anyNonNullNonBlank()

	it must "throw an IllegalArgumentException with the given (trimmed) argName and a default message when the value is null" in {
		val trimmedArgName = anyArgName().trim
		val thrown = the [IllegalArgumentException] thrownBy(null.asInstanceOf[AnyRef].mustNotBeNull(trimmedArgName.wrappedInWhitespace))
		thrown.getMessage must be(s"Argument must be specified; arg=${trimmedArgName}, value=null")
	}

	"mustNotBeNull(argName, message)" must "not accept a null argName" in {
		val thrown = the [IllegalArgumentException] thrownBy(anyNonNull().mustNotBeNull(null, anyMessage()))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=null")
	}

	private def anyMessage() = StringGenerator.anyNonNullNonBlank()

	it must "not accept an empty argName" in {
		val thrown = the [IllegalArgumentException] thrownBy(anyNonNull().mustNotBeNull("", anyMessage()))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=")
	}

	it must "not accept a whitespace argName" in {
		val whitespaceArgName = StringGenerator.anyWhitespace()
		val thrown = the [IllegalArgumentException] thrownBy(anyNonNull().mustNotBeNull(whitespaceArgName, anyMessage()))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=" + whitespaceArgName)
	}

	it must "not accept a null message" in {
		val thrown = the [IllegalArgumentException] thrownBy(anyNonNull().mustNotBeNull(anyArgName(), null))
		thrown.getMessage must be("Exception message must be specified; arg=message, value=null")
	}

	it must "not accept an empty message" in {
		val thrown = the [IllegalArgumentException] thrownBy(anyNonNull().mustNotBeNull(anyArgName(), ""))
		thrown.getMessage must be("Exception message must be specified; arg=message, value=")
	}

	it must "not accept a whitespace message" in {
		val whitespaceMessage = StringGenerator.anyWhitespace()
		val thrown = the [IllegalArgumentException] thrownBy(anyNonNull().mustNotBeNull(anyArgName(), whitespaceMessage))
		thrown.getMessage must be("Exception message must be specified; arg=message, value=" + whitespaceMessage)
	}

	it must "not throw an exception when the value is not null" in {
		noException must be thrownBy(anyNonNull().mustNotBeNull(anyArgName(), anyMessage()))
	}

	it must "throw an IllegalArgumentException with the given (trimmed) argName and (trimmed) message when the value is null" in {
		val trimmedArgName = anyArgName().trim
		val trimmedMessage = anyMessage().trim
		val thrown = the [IllegalArgumentException] thrownBy(null.asInstanceOf[AnyRef].mustNotBeNull(trimmedArgName.wrappedInWhitespace, trimmedMessage.wrappedInWhitespace))
		thrown.getMessage must be(s"${trimmedMessage}; arg=${trimmedArgName}, value=null")
	}

	"mustNotContainNull(argName)" must "not accept a null argName" in {
		val thrown = the [IllegalArgumentException] thrownBy(anySeqWithoutNulls().mustNotContainNull(null))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=null")
	}

	private def anySeqWithoutNulls() = Seq(new AnyRef())

	it must "not accept an empty argName" in {
		val thrown = the [IllegalArgumentException] thrownBy(anySeqWithoutNulls().mustNotContainNull(""))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=")
	}

	it must "not accept a whitespace argName" in {
		val whitespaceArgName = StringGenerator.anyWhitespace()
		val thrown = the [IllegalArgumentException] thrownBy(anySeqWithoutNulls().mustNotContainNull(whitespaceArgName))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=" + whitespaceArgName)
	}

	it must "not throw an exception when the sequence is empty" in {
		noException must be thrownBy(Seq().mustNotContainNull(anyArgName()))
	}

	it must "not throw an exception when the sequence contains one non-null value" in {
		noException must be thrownBy(Seq(new AnyRef).mustNotContainNull(anyArgName()))
	}

	it must "not throw an exception when the sequence contains all non-null values" in {
		val noNulls = Seq.fill(Random.between(2, 100)) { new AnyRef }
		noException must be thrownBy(noNulls.mustNotContainNull(anyArgName()))
	}

	it must "throw an IllegalArgumentException with the given (trimmed) argName and a default message when the value is null" in {
		val trimmedArgName = anyArgName().trim
		val thrown = the [IllegalArgumentException] thrownBy(null.asInstanceOf[Seq[AnyRef]].mustNotContainNull(trimmedArgName.wrappedInWhitespace))
		thrown.getMessage must be(s"Sequence argument must not contain nulls; arg=${trimmedArgName}, value=null")
	}

	it must "throw an IllegalArgumentException with the given (trimmed) argName, a default message and the first offending index when the sequence contains null" in {
		val containsNulls = Random.shuffle(sequenceOfNoNulls() ++ sequenceOfNulls())
		val indexOfFirstNull = containsNulls.indexOf(null)
		val trimmedArgName = anyArgName().trim
		val thrown = the [IllegalArgumentException] thrownBy(containsNulls.mustNotContainNull(trimmedArgName.wrappedInWhitespace))
		thrown.getMessage must be(s"Sequence argument must not contain nulls; arg=${trimmedArgName}, value=null, index=${indexOfFirstNull}")
	}

	private def sequenceOfNoNulls() = Seq.fill(Random.between(1, 100)) { new AnyRef }

	private def sequenceOfNulls() = Seq.fill(Random.between(1, 100)) { null }

	"mustNotContainNull(argName, message)" must "not accept a null argName" in {
		val thrown = the [IllegalArgumentException] thrownBy(anySeqWithoutNulls().mustNotContainNull(null, anyMessage()))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=null")
	}

	it must "not accept an empty argName" in {
		val thrown = the [IllegalArgumentException] thrownBy(anySeqWithoutNulls().mustNotContainNull("", anyMessage()))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=")
	}

	it must "not accept a whitespace argName" in {
		val whitespaceArgName = StringGenerator.anyWhitespace()
		val thrown = the [IllegalArgumentException] thrownBy(anySeqWithoutNulls().mustNotContainNull(whitespaceArgName, anyMessage()))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=" + whitespaceArgName)
	}

	it must "not accept a null message" in {
		val thrown = the [IllegalArgumentException] thrownBy(anySeqWithoutNulls().mustNotContainNull(anyArgName(), null))
		thrown.getMessage must be("Exception message must be specified; arg=message, value=null")
	}

	it must "not accept an empty message" in {
		val thrown = the [IllegalArgumentException] thrownBy(anySeqWithoutNulls().mustNotContainNull(anyArgName(), ""))
		thrown.getMessage must be("Exception message must be specified; arg=message, value=")
	}

	it must "not accept a whitespace message" in {
		val whitespaceMessage = StringGenerator.anyWhitespace()
		val thrown = the [IllegalArgumentException] thrownBy(anySeqWithoutNulls().mustNotContainNull(anyArgName(), whitespaceMessage))
		thrown.getMessage must be("Exception message must be specified; arg=message, value=" + whitespaceMessage)
	}

	it must "not throw an exception when the sequence is empty" in {
		noException must be thrownBy(Seq().mustNotContainNull(anyArgName(), anyMessage()))
	}

	it must "not throw an exception when the sequence contains one non-null value" in {
		noException must be thrownBy(Seq(new AnyRef).mustNotContainNull(anyArgName(), anyMessage()))
	}

	it must "not throw an exception when the sequence contains all non-null values" in {
		val noNulls = Seq.fill(Random.between(2, 100)) { new AnyRef }
		noException must be thrownBy(noNulls.mustNotContainNull(anyArgName(), anyMessage()))
	}

	it must "throw an IllegalArgumentException with the given (trimmed) argName and (trimmed) message when the value is null" in {
		val trimmedArgName = anyArgName().trim
		val trimmedMessage = anyMessage().trim
		val thrown = the [IllegalArgumentException] thrownBy(null.asInstanceOf[Seq[AnyRef]].mustNotContainNull(trimmedArgName.wrappedInWhitespace, trimmedMessage.wrappedInWhitespace))
		thrown.getMessage must be(s"${trimmedMessage}; arg=${trimmedArgName}, value=null")
	}

	it must "throw an IllegalArgumentException with the given (trimmed) argName, the (trimmed) message and the first offending index when the sequence contains null" in {
		val containsNulls = Random.shuffle(sequenceOfNoNulls() ++ sequenceOfNulls())
		val indexOfFirstNull = containsNulls.indexOf(null)
		val trimmedArgName = anyArgName().trim
		val trimmedMessage = anyMessage().trim
		val thrown = the [IllegalArgumentException] thrownBy(containsNulls.mustNotContainNull(trimmedArgName.wrappedInWhitespace, trimmedMessage.wrappedInWhitespace))
		thrown.getMessage must be(s"${trimmedMessage}; arg=${trimmedArgName}, value=null, index=${indexOfFirstNull}")
	}

	"mustBeSpecified(argName)" must "not accept a null argName" in {
		val thrown = the [IllegalArgumentException] thrownBy(anySpecifiedString().mustBeSpecified(null))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=null")
	}

	private def anySpecifiedString() = StringGenerator.anyNonNullNonBlank()

	it must "not accept an empty argName" in {
		val thrown = the [IllegalArgumentException] thrownBy(anySpecifiedString().mustBeSpecified(""))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=")
	}

	it must "not accept a whitespace argName" in {
		val whitespaceArgName = StringGenerator.anyWhitespace()
		val thrown = the [IllegalArgumentException] thrownBy(anySpecifiedString().mustBeSpecified(whitespaceArgName))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=" + whitespaceArgName)
	}

	it must "not throw an exception when the value is not null, empty or whitespace" in {
		noException must be thrownBy(anySpecifiedString().mustBeSpecified(anyArgName()))
	}

	it must "throw an IllegalArgumentException with the given (trimmed) argName and a default message when the value is null" in {
		val trimmedArgName = anyArgName().trim
		val thrown = the [IllegalArgumentException] thrownBy(null.asInstanceOf[String].mustBeSpecified(trimmedArgName.wrappedInWhitespace))
		thrown.getMessage must be(s"Argument must be specified; arg=${trimmedArgName}, value=null")
	}

	it must "throw an IllegalArgumentException with the given (trimmed) argName and a default message when the value is an empty string" in {
		val trimmedArgName = anyArgName().trim
		val thrown = the [IllegalArgumentException] thrownBy("".mustBeSpecified(trimmedArgName.wrappedInWhitespace))
		thrown.getMessage must be(s"Argument must be specified; arg=${trimmedArgName}, value=")
	}

	it must "throw an IllegalArgumentException with the given (trimmed) argName and a default message when the value is a whitespace" in {
		val trimmedArgName = anyArgName().trim
		val whitespace = StringGenerator.anyWhitespace()
		val thrown = the [IllegalArgumentException] thrownBy(whitespace.mustBeSpecified(trimmedArgName.wrappedInWhitespace))
		thrown.getMessage must be(s"Argument must be specified; arg=${trimmedArgName}, value=${whitespace}")
	}

	"mustBeSpecified(argName, message)" must "not accept a null argName" in {
		val thrown = the [IllegalArgumentException] thrownBy(anySpecifiedString().mustBeSpecified(null, anyMessage()))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=null")
	}

	it must "not accept an empty argName" in {
		val thrown = the [IllegalArgumentException] thrownBy(anySpecifiedString().mustBeSpecified("", anyMessage()))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=")
	}

	it must "not accept a whitespace argName" in {
		val whitespaceArgName = StringGenerator.anyWhitespace()
		val thrown = the [IllegalArgumentException] thrownBy(anySpecifiedString().mustBeSpecified(whitespaceArgName, anyMessage()))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=" + whitespaceArgName)
	}

	it must "not accept a null message" in {
		val thrown = the [IllegalArgumentException] thrownBy(anySpecifiedString().mustBeSpecified(anyArgName(), null))
		thrown.getMessage must be("Exception message must be specified; arg=message, value=null")
	}

	it must "not accept an empty message" in {
		val thrown = the [IllegalArgumentException] thrownBy(anySpecifiedString().mustBeSpecified(anyArgName(), ""))
		thrown.getMessage must be("Exception message must be specified; arg=message, value=")
	}

	it must "not accept a whitespace message" in {
		val whitespaceMessage = StringGenerator.anyWhitespace()
		val thrown = the [IllegalArgumentException] thrownBy(anySpecifiedString().mustBeSpecified(anyArgName(), whitespaceMessage))
		thrown.getMessage must be("Exception message must be specified; arg=message, value=" + whitespaceMessage)
	}

	it must "not throw an exception when the value is not null, empty or whitespace" in {
		noException must be thrownBy(anySpecifiedString().mustNotBeNull(anyArgName(), anyMessage()))
	}

	it must "throw an IllegalArgumentException with the given (trimmed) argName and (trimmed) message when the value is null" in {
		val trimmedArgName = anyArgName().trim
		val trimmedMessage = anyMessage().trim
		val thrown = the [IllegalArgumentException] thrownBy(null.asInstanceOf[String].mustBeSpecified(trimmedArgName.wrappedInWhitespace, trimmedMessage.wrappedInWhitespace))
		thrown.getMessage must be(s"${trimmedMessage}; arg=${trimmedArgName}, value=null")
	}

	it must "throw an IllegalArgumentException with the given (trimmed) argName and (trimmed) message when the value is an empty string" in {
		val trimmedArgName = anyArgName().trim
		val trimmedMessage = anyMessage().trim
		val thrown = the [IllegalArgumentException] thrownBy("".mustBeSpecified(trimmedArgName.wrappedInWhitespace, trimmedMessage.wrappedInWhitespace))
		thrown.getMessage must be(s"${trimmedMessage}; arg=${trimmedArgName}, value=")
	}

	it must "throw an IllegalArgumentException with the given (trimmed) argName and (trimmed) message when the value is whitespace" in {
		val trimmedArgName = anyArgName().trim
		val trimmedMessage = anyMessage().trim
		val whitespace = StringGenerator.anyWhitespace()
		val thrown = the [IllegalArgumentException] thrownBy(whitespace.mustBeSpecified(trimmedArgName.wrappedInWhitespace, trimmedMessage.wrappedInWhitespace))
		thrown.getMessage must be(s"${trimmedMessage}; arg=${trimmedArgName}, value=${whitespace}")
	}

	"isOutOfRange(argName)" must "not accept a null argName when called on a non-null object" in {
		val thrown = the [IllegalArgumentException] thrownBy(anyNonNull().isOutOfRange(null))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=null")
	}

	it must "not accept a null argName when called on a null object" in {
		val thrown = the [IllegalArgumentException] thrownBy(null.asInstanceOf[Any].isOutOfRange(null))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=null")
	}

	it must "not accept an empty argName when called on a non-null object" in {
		val thrown = the [IllegalArgumentException] thrownBy(anyNonNull().isOutOfRange(""))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=")
	}

	it must "not accept an empty argName when called on a null object" in {
		val thrown = the [IllegalArgumentException] thrownBy(null.asInstanceOf[Any].isOutOfRange(""))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=")
	}

	it must "not accept a whitespace argName when called on a non-null object" in {
		val whitespaceArgName = StringGenerator.anyWhitespace()
		val thrown = the [IllegalArgumentException] thrownBy(anyNonNull().isOutOfRange(whitespaceArgName))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=" + whitespaceArgName)
	}

	it must "not accept a whitespace argName when called on a null object" in {
		val whitespaceArgName = StringGenerator.anyWhitespace()
		val thrown = the [IllegalArgumentException] thrownBy(null.asInstanceOf[Any].isOutOfRange(whitespaceArgName))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=" + whitespaceArgName)
	}

	it must "return an IllegalArgumentException with the given (trimmed) argName and a default message when the value is null" in {
		val trimmedArgName = anyArgName().trim
		val exception = null.asInstanceOf[Any].isOutOfRange(trimmedArgName.wrappedInWhitespace)
		exception.getMessage must be(s"Argument is out of range; arg=${trimmedArgName}, value=null")
	}

	it must "return an IllegalArgumentException with the given (trimmed) argName and a default message with the argument value" in {
		val trimmedArgName = anyArgName().trim
		val argValue = anyNonNull()
		val exception = argValue.isOutOfRange(trimmedArgName.wrappedInWhitespace)
		exception.getMessage must be(s"Argument is out of range; arg=${trimmedArgName}, value=${argValue}")
	}

	"isOutOfRange(argName, message)" must "not accept a null argName when called on a non-null object" in {
		val thrown = the [IllegalArgumentException] thrownBy(anyNonNull().isOutOfRange(null, anyMessage()))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=null")
	}

	it must "not accept a null argName when called on a null object" in {
		val thrown = the [IllegalArgumentException] thrownBy(null.asInstanceOf[Any].isOutOfRange(null, anyMessage()))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=null")
	}

	it must "not accept an empty argName when called on a non-null object" in {
		val thrown = the [IllegalArgumentException] thrownBy(anyNonNull().isOutOfRange("", anyMessage()))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=")
	}

	it must "not accept an empty argName when called on a null object" in {
		val thrown = the [IllegalArgumentException] thrownBy(null.asInstanceOf[Any].isOutOfRange("", anyMessage()))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=")
	}

	it must "not accept a whitespace argName when called on a non-null object" in {
		val whitespaceArgName = StringGenerator.anyWhitespace()
		val thrown = the [IllegalArgumentException] thrownBy(anyNonNull().isOutOfRange(whitespaceArgName, anyMessage()))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=" + whitespaceArgName)
	}

	it must "not accept a whitespace argName when called on a null object" in {
		val whitespaceArgName = StringGenerator.anyWhitespace()
		val thrown = the [IllegalArgumentException] thrownBy(null.asInstanceOf[Any].isOutOfRange(whitespaceArgName, anyMessage()))
		thrown.getMessage must be("Argument name must be specified; arg=argName, value=" + whitespaceArgName)
	}

	it must "return an IllegalArgumentException with the given (trimmed) argName and (trimmed) message when the value is null" in {
		val trimmedArgName = anyArgName().trim
		val trimmedMessage = anyMessage().trim
		val exception = null.asInstanceOf[Any].isOutOfRange(trimmedArgName.wrappedInWhitespace, trimmedMessage.wrappedInWhitespace)
		exception.getMessage must be(s"${trimmedMessage}; arg=${trimmedArgName}, value=null")
	}

	it must "return an IllegalArgumentException with the given (trimmed) argName and (trimmed) message with the argument value" in {
		val trimmedArgName = anyArgName().trim
		val trimmedMessage = anyMessage().trim
		val argValue = anyNonNull()
		val exception = argValue.isOutOfRange(trimmedArgName.wrappedInWhitespace, trimmedMessage.wrappedInWhitespace)
		exception.getMessage must be(s"${trimmedMessage}; arg=${trimmedArgName}, value=${argValue}")
	}
}
