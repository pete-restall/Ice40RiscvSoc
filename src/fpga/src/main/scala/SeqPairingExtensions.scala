package uk.co.lophtware.msfreference

object SeqPairingExtensions {
	implicit class SeqPairer[A, B](items: B)(implicit cast: B => Seq[A]) {
		if (items == null) {
			throw new IllegalArgumentException("Sequence of items must be specified; arg=items, value=null")
		}

		def asPairedSeq: Iterable[Seq[A]] = {
			if ((items.length & 1) != 0) {
				throw new IllegalArgumentException(s"Sequence must have an even number of items; arg=items, length=${items.length}")
			}

			pairsFrom(items)
		}

		private def pairsFrom(items: Seq[A]): Iterable[Seq[A]] = if (items.isEmpty) Seq.empty else firstPairFrom(items) ++ pairsFrom(items.tail.tail)

		private def firstPairFrom(items: Seq[A]): Iterable[Seq[A]] = List(List(items.head, items.tail.head))
	}
}
