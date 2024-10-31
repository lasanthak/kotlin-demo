package org.lasantha.kotlindemo.functional_programming

/**
 * A sample Producer class to demonstrate the monad laws.
 * The caller has to implement the produce function to return a Producible object.
 * The Result represents a successful value by encapsulating any value of type R and
 * a Failure represents a failure by encapsulating any explanation of type F.
 *   Result.map() -> Result
 *   Result.flatMap() -> Result or Failure
 *   Failure.map() -> Failure
 *   Failure.flatMap() -> Failure
 */
sealed interface Producible<out R, out F> : Monad<R> {

    val success: Boolean

    override fun <B> map(f: (R) -> B): Producible<B, F>

    data class Result<out A>(val value: A) : Producible<A, Nothing> {

        override val success = true

        override fun <B> map(f: (A) -> B) = Result(f(value))

        override fun <B> flatMap(f: (A) -> Monad<B>) = f(value)
    }

    /**
     * Failure object will short-circuit the map and flatMap operations in a pipeline.
     */
    data class Failure<out F>(val explanation: F) : Producible<Nothing, F> {

        override val success = false

        override fun <B> map(f: (Nothing) -> B) = this

        override fun <B> flatMap(f: (Nothing) -> Monad<B>) = this
    }
}

interface Producer<out R, out F> {
    fun produce(): Producible<R, F>
}
