package com.example.myapplication.network

object ApiConfig {
    // Khi chạy trên máy ảo Android Studio: dùng 10.0.2.2 thay cho localhost
    // Khi chạy trên điện thoại thật qua USB: dùng IP máy tính của bạn (vd: 192.168.1.5)
    // Khi chạy trên Genymotion: dùng 10.0.3.2
    const val BASE_URL = "http://10.0.2.2/vku_chat/"

    // Timeout (giây)
    const val TIMEOUT = 30L
}