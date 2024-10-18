package org.lasantha.kotlindemo.closeable

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class AutoCloseableDoorTest {

    private var lock = 0

    @BeforeEach
    fun setUp() {
        lock = 0
    }

    @Test
    fun `Test CloseableDoor - no close`() {
        val door = AutoCloseableDoor(connectBlock = { ++lock }, closeBlock = { --lock })
        assertEquals(0, lock)
        door.connect()
        assertEquals(1, lock)
    }

    @Test
    fun `Test CloseableDoor - manual close`() {
        val door = AutoCloseableDoor(connectBlock = { ++lock }, closeBlock = { --lock })
        assertEquals(0, lock)
        door.connect()
        assertEquals(1, lock)
        door.close()
        assertEquals(0, lock)
    }

    @Test
    fun `Test CloseableDoor - auto close`() {
        AutoCloseableDoor(connectBlock = { ++lock }, closeBlock = { --lock }).use {
            assertEquals(0, lock)
            it.connect()
            assertEquals(1, lock)
        }

        // Auto close should have released the lock
        assertEquals(0, lock)
    }

    @Test
    fun `Test CloseableDoor - exception at close`() {
        val door = AutoCloseableDoor(
            connectBlock = { ++lock },
            closeBlock = {
                throw IllegalStateException("Failed to close")
                --lock
            }
        )
        assertEquals(0, lock)

        assertFailsWith<IllegalStateException> {
            door.use {
                it.connect()
                assertEquals(1, lock)
            }
        }
        // expecting IllegalStateException at close, so counter is not decremented
        assertEquals(1, lock)
    }

    @Test
    fun `Test CloseableDoor - exception at connect`() {
        val door = AutoCloseableDoor(
            connectBlock = {
                throw IllegalStateException("Failed to connect")
                ++lock
            },
            closeBlock = { --lock }
        )
        assertEquals(0, lock)

        assertFailsWith<IllegalStateException> {
            door.use {
                it.connect()
            }
        }
        // expecting IllegalStateException at connect, so counter is not incremented. But auto close will decrement it.
        assertEquals(-1, lock)
    }

    @Test
    fun `Test CloseableDoor - exceptions at connect and close`() {
        val door = AutoCloseableDoor(
            connectBlock = {
                throw IllegalStateException("Failed to connect")
                ++lock
            },
            closeBlock = {
                throw IllegalStateException("Failed to close")
                --lock
            }
        )
        assertEquals(0, lock)

        assertFailsWith<IllegalStateException> {
            door.use {
                it.connect()
            }
        }
        // expecting IllegalStateException at connect and close, so counter is not modified.
        assertEquals(0, lock)
    }
}
