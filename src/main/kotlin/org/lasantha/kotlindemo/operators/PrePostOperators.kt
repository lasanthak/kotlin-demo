package org.lasantha.kotlindemo.operators


fun main(args: Array<String>) {
    val ppo = PrePostOperators(100)
    println("$ppo")
    println("Pre inc > ${ppo.preInc()}, $ppo")
    println("$ppo")
    println("Post inc > ${ppo.postInc()}, $ppo")
    println("$ppo")
}

class PrePostOperators(private var a: Long) {
    fun preInc(): Long {
        return ++a
    }

    fun postInc(): Long {
        return a++
    }

    override fun toString(): String = "a = $a"
}
