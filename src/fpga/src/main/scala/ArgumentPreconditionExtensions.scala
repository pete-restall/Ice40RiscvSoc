package net.restall.ice40riscvsoc

object ArgumentPreconditionExtensions {
	private val UNSPECIFIED_ARG_NAME = "Argument name must be specified"
	private val UNSPECIFIED_MESSAGE = "Exception message must be specified"

	implicit class NotNull(argValue: AnyRef) {
		def mustNotBeNull(argName: String): Unit = argValue.mustNotBeNull(argName, "Argument must be specified")

		def mustNotBeNull(argName: String, message: String): Unit = {
			argName.mustBeSpecified("argName", UNSPECIFIED_ARG_NAME)
			message.mustBeSpecified("message", UNSPECIFIED_MESSAGE)

			if (argValue == null) {
				throw new IllegalArgumentException(s"${message.trim}; arg=${argName.trim}, value=null")
			}
		}
	}

	implicit class NotContainNull[T <: AnyRef](argValue: Seq[T]) {
		def mustNotContainNull(argName: String): Unit = argValue.mustNotContainNull(argName, "Sequence argument must not contain nulls")

		def mustNotContainNull(argName: String, message: String): Unit = {
			argValue.mustNotBeNull(argName, message)

			val indexOfNull = argValue.indexOf(null)
			if (indexOfNull >= 0) {
				throw new IllegalArgumentException(s"${message.trim}; arg=${argName.trim}, value=null, index=${indexOfNull}")
			}
		}
	}

	implicit class Specified(argValue: String) {
		def mustBeSpecified(argName: String): Unit = argValue.mustBeSpecified(argName, "Argument must be specified")

		def mustBeSpecified(argName: String, message: String): Unit = {
			if (argName == null || argName.isBlank) {
				throw new IllegalArgumentException(s"${UNSPECIFIED_ARG_NAME}; arg=argName, value=${argName}")
			}

			if (message == null || message.isBlank) {
				throw new IllegalArgumentException(s"${UNSPECIFIED_MESSAGE}; arg=message, value=${message}")
			}

			if (argValue == null || argValue.isBlank) {
				throw new IllegalArgumentException(s"${message.trim}; arg=${argName.trim}, value=${argValue}")
			}
		}
	}

	implicit class OutOfRange(argValue: Any) {
		def isOutOfRange(argName: String): Exception = isOutOfRange(argName, "Argument is out of range")

		def isOutOfRange(argName: String, message: String): Exception = {
			argName.mustBeSpecified("argName", UNSPECIFIED_ARG_NAME)
			message.mustBeSpecified("message", UNSPECIFIED_MESSAGE)
			new IllegalArgumentException(s"${message.trim}; arg=${argName.trim}, value=${argValue}")
		}
	}
}
