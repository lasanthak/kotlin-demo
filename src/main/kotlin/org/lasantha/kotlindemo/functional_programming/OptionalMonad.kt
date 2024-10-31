package org.lasantha.kotlindemo.functional_programming

/**
 * A sample Optional class to demonstrate the monad laws.
 * If the given value is null, it will return None, otherwise it will return Some.
 *   Some.map() -> Some or None
 *   Some.flatMap() -> Some or None
 *   None.map() -> None
 *   None.flatMap() -> None
 */
sealed interface Optional<out A> : Monad<A> {

    override fun <B> map(f: (A) -> B): Optional<B>

    override fun <B> flatMap(f: (A) -> Monad<B>): Optional<B>

    companion object {
        fun <B> of(value: B?): Optional<B> {
            return when (value) {
                null -> None
                else -> Some(value)
            }
        }
    }

    data class Some<out A>(val value: A) : Optional<A> {

        override fun <B> map(f: (A) -> B) = of(f(value))

        override fun <B> flatMap(f: (A) -> Monad<B>) = when (val result = f(value)) {
            is Some -> result
            is None -> None
            else -> throw IllegalStateException("Optional can only contain Some or None")
        }
    }

    /**
     * None object will short-circuit the map and flatMap operations in a pipeline.
     */
    data object None : Optional<Nothing> {

        override fun <B> map(f: (Nothing) -> B) = None

        override fun <B> flatMap(f: (Nothing) -> Monad<B>) = None
    }
}
