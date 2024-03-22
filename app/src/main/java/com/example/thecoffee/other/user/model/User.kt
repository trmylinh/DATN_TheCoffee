package com.example.thecoffee.other.user.model

import android.net.Uri
import java.io.Serializable

data class User (
    val uid: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val avt: String? = null,
)