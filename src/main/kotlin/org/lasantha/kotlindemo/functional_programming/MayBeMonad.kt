package org.lasantha.kotlindemo.functional_programming

sealed interface MayBe<out A> : Monad<A> {

    override fun <B> map(f: (A) -> B): MayBe<B>

    override fun <B> flatMap(f: (A) -> Monad<B>): MayBe<B>

    data class Just<out A>(val value: A) : MayBe<A> {

        override fun <B> map(f: (A) -> B) = Just(f(value))

        override fun <B> flatMap(f: (A) -> Monad<B>) = when (val result = f(value)) {
            is Just -> result
            is None -> None
            else -> throw IllegalStateException("MayBe can only contain Just or None")
        }
    }

    data object None : MayBe<Nothing> {

        override fun <B> map(f: (Nothing) -> B) = None

        override fun <B> flatMap(f: (Nothing) -> Monad<B>) = None

    }
}
