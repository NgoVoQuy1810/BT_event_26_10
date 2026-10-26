package com.example.bt_event

object FormValidator {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\$")
    private val PHONE_REGEX = Regex("^(03|05|07|08|09)\\d{8}\$")

    fun isValidName(name: String) = name.trim().length >= 3

    fun isValidEmail(email: String) =
        email.isNotBlank() && EMAIL_REGEX.matches(email.trim())

    fun isValidPhone(phone: String) =
        PHONE_REGEX.matches(phone.trim())

    fun isValidPassword(pass: String) = pass.length >= 6

    fun isConfirmPasswordMatch(pass: String, confirmPass: String) =
        confirmPass.isNotEmpty() && pass == confirmPass
}