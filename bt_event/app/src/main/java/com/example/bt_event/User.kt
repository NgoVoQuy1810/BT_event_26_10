package com.example.bt_event

import java.io.Serializable

data class User(
    val fullName: String,
    val email: String,
    val phone: String,
    val gender: String
) : Serializable