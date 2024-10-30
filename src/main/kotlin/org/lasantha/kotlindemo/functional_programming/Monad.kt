package org.lasantha.kotlindemo.functional_programming

interface Monad<out A> {

    fun <B> map(f: (A) -> B): Monad<B>

    fun <B> flatMap(f: (A) -> Monad<B>): Monad<B>
}

/**
 * A simple class to demonstrate the monad laws.
 */
sealed interface Optional<out A> : Monad<A> {
    companion object {
        fun <B> of(value: B?): Optional<B> {
            return when (value) {
                null -> None
                else -> Some(value)
            }
        }
    }

    data class Some<A>(val value: A) : Optional<A> {

        override fun <B> map(f: (A) -> B) = of(f(value))

        override fun <B> flatMap(f: (A) -> Monad<B>) = f(value)
    }

    data object None : Optional<Nothing> {

        override fun <B> map(f: (Nothing) -> B) = None

        override fun <B> flatMap(f: (Nothing) -> Monad<B>) = None
    }
}
