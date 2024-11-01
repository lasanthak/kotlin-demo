package org.lasantha.kotlindemo.functional_programming

/**
 * Implementation of the MayBe monad (from Haskell).
 *   Just.map() -> Just
 *   Just.flatMap() -> Just or None
 *   None.map() -> None
 *   None.flatMap() -> None
 */
sealed interface MayBe<out A> {
    fun <B> map(f: (A) -> B): MayBe<B>
    fun <B> flatMap(f: (A) -> MayBe<B>): MayBe<B>
    val isJust: Boolean

    fun getOrNull() = when (this) {
        is Just -> value
        is None -> null
    }

    data class Just<out A>(val value: A) : MayBe<A> {
        override fun <B> map(f: (A) -> B) = Just(f(value))
        override fun <B> flatMap(f: (A) -> MayBe<B>) = f(value)
        override val isJust = true
    }

    data object None : MayBe<Nothing> {
        override fun <B> map(f: (Nothing) -> B) = None
        override fun <B> flatMap(f: (Nothing) -> MayBe<B>) = None
        override val isJust = false
    }
}
