package com.mikebull94.result

/**
 * - Rust: [Result.iter](https://doc.rust-lang.org/std/result/enum.Result.html#method.iter)
 */
fun <V, E> Result<V, E>.iterator(): Iterator<V> {
    return ResultIterator(this)
}

/**
 * Rust: [Result.iter_mut](https://doc.rust-lang.org/std/result/enum.Result.html#method.iter_mut)
 */
fun <V, E> Result<V, E>.mutableIterator(): MutableIterator<V> {
    return ResultIterator(this)
}

private class ResultIterator<out V, out E>(private val result: Result<V, E>) : MutableIterator<V> {
    private var yielded = false

    override fun hasNext(): Boolean {
        if (yielded) {
            return false
        }

        return when (result) {
            is Ok -> true
            is Error -> false
        }
    }

    override fun next(): V {
        if (!yielded && result is Ok) {
            yielded = true
            return result.value
        } else {
            throw NoSuchElementException()
        }
    }

    override fun remove() {
        yielded = true
    }
}
