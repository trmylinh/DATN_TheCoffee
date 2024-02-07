package com.example.thecoffee.data.models

import java.io.Serializable

data class User (
    val uid: String,
    val name: String?,
    val email: String?,
    val phone: String?,
    var isAuthenticated: Boolean = false,
    var isNew: Boolean? = false,
    var isCreated: Boolean = false
) : Serializable {
    constructor() : this("", "", "", ""){}
}