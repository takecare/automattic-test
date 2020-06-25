package com.automattic.freshlypressed.presentation

data class Effect<T>(
    private var isConsumed: Boolean = false,
    private val payload: T
) {
    constructor(payload: T) : this(false, payload)

    fun get() =
        if (isConsumed) {
            null
        } else {
            isConsumed = true
            payload
        }

    fun consume(block: (T) -> Unit) {
        get()?.let(block)
    }
}
