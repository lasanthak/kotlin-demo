package org.lasantha.kotlindemo.operators

class PrePostOperators(private var value: Long) {

    fun getValue(): Long {
        return value
    }

    fun preInc(): Long {
        return ++value
    }

    fun postInc(): Long {
        return value++
    }

    override fun toString(): String = "${this::class}(value=$value)"
}
