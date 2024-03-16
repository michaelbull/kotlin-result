package com.github.michaelbull.result

/**
 * Returns an [Iterator] over the possibly contained [value][Result.value].
 *
 * The iterator yields one [value][Result.value] if this result [is ok][Result.isOk], otherwise
 * throws [NoSuchElementException].
 *
 * - Rust: [Result.iter](https://doc.rust-lang.org/std/result/enum.Result.html#method.iter)
 */
public fun <V, E> Result<V, E>.iterator(): Iterator<V> {
    return ResultIterator(this)
}

/**
 * Returns a [MutableIterator] over the possibly contained [value][Result.value].
 *
 * The iterator yields one [value][Result.value] if this result [is ok][Result.isOk], otherwise
 * throws [NoSuchElementException].
 *
 * - Rust: [Result.iter_mut](https://doc.rust-lang.org/std/result/enum.Result.html#method.iter_mut)
 */
public fun <V, E> Result<V, E>.mutableIterator(): MutableIterator<V> {
    return ResultIterator(this)
}

private class ResultIterator<out V, out E>(private val result: Result<V, E>) : MutableIterator<V> {

    /**
     * A flag indicating whether this [Iterator] has [yielded] the [value][Result.value] of the
     * [result].
     */
    private var yielded = false

    /**
     * @return `true` if this [Iterator] has [yielded] the [value][Result.value] of the [result],
     * `false` otherwise.
     */
    override fun hasNext(): Boolean {
        return !yielded && result.isOk
    }

    /**
     * Returns the [value][Result.value] of the [result] if not [yielded] and the result
     * [is ok][Result.isOk].
     *
     * @throws NoSuchElementException if already [yielded] or the result
     * [is an error][Result.isErr].
     */
    override fun next(): V {
        if (hasNext()) {
            remove()
            return result.value
        } else {
            throw NoSuchElementException()
        }
    }

    /**
     * Flags this [Iterator] as having [yielded] the [value][Result.value] of the [result].
     */
    override fun remove() {
        yielded = true
    }
}
