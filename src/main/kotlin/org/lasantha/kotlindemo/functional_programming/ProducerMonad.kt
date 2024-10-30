package org.lasantha.kotlindemo.functional_programming

/**
 * A sample Producer class to demonstrate the monad laws.
 */
sealed interface Producible<out R, out F> : Monad<R> {
    data class Result<A>(val value: A) : Producible<A, Nothing> {

        override fun <B> map(f: (A) -> B) = Result(f(value))

        override fun <B> flatMap(f: (A) -> Monad<B>) = f(value)
    }

    data class Failure<F>(val explanation: F) : Producible<Nothing, F> {

        override fun <B> map(f: (Nothing) -> B) = this

        override fun <B> flatMap(f: (Nothing) -> Monad<B>) = this
    }
}

interface Producer<out R, out F> {
    fun produce(): Producible<R, F>
}
