package com.example.myapplication.profile

data class User(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val avatarUrl: String? = null
)