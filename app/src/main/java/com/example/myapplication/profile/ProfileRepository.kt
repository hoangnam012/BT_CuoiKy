package com.example.myapplication.profile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ProfileRepository {
    // Sau này sẽ thay bằng truy vấn từ Room/Retrofit
    fun getUser(): Flow<User> = flowOf(
        User(
            id = "1",
            name = "Lâm Phong",
            phone = "+84 123 456 789",
            email = "phongl@example.com",
            avatarUrl = null
        )
    )
}