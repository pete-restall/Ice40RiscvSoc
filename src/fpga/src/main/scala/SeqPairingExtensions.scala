package uk.co.lophtware.msfreference

object SeqPairingExtensions {
	implicit class SeqPairer[T](items: Seq[T]) {
		if (items == null) {
			throw new IllegalArgumentException("Sequence of items must be specified; arg=items, value=null")
		}

		def asPairedSeq: Iterable[Seq[T]] = {
			if ((items.length & 1) != 0) {
				throw new IllegalArgumentException(s"Sequence must have an even number of items; arg=items, length=${items.length}")
			}

			items.zipWithIndex.groupBy { case(_, i) => i >> 1 }.values.map(_.map { case(item, _) => item })
		}
	}
}
