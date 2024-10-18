package org.lasantha.kotlindemo.closeable

import java.io.Closeable
import java.util.UUID

open class AutoCloseableDoor(private val connectBlock: () -> Unit, private val closeBlock: () -> Unit): Closeable {
    val name = UUID.randomUUID().toString()

    fun connect() {
        println("[$name] Connecting ...")
        connectBlock()
        println("[$name] Successfully connected")
    }

    override fun close() {
        println("[$name] Closing ...")
        closeBlock()
        println("[$name] Successfully closed")
    }
}
