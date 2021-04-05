package com.janbina.habits.data.database

import java.security.SecureRandom

object IdGenerator {

    private const val ID_LENGTH = 20
    private const val ID_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

    private val rand = SecureRandom()

    fun generate(): String {
        val builder = StringBuilder()
        val maxRandom = ID_ALPHABET.length
        repeat(ID_LENGTH) {
            builder.append(ID_ALPHABET[rand.nextInt(maxRandom)])
        }
        return builder.toString()
    }
}
