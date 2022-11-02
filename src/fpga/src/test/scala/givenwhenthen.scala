package uk.co.lophtware.msfreference.tests.givenwhenthen

abstract trait Given[A <: When[_]] {
	def when: A = ???
}

abstract trait When[A <: Then] {
	def then: A = ???
}

abstract trait Then {
}

abstract trait And[A] {
	def and: A = ???
}

abstract trait GivenAnd[TGiven, TWhen <: When[_]] extends Given[TWhen] with And[TGiven] {
}

abstract trait WhenAnd[TWhen, TThen <: Then] extends When[TThen] with And[TWhen] {
}
