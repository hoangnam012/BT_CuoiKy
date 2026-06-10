package com.example.myapplication.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// --- Data Classes (Request) ---

data class RegisterRequest(
    val username: String,
    val email: String,
    val phone: String = "",
    val password: String
)

data class LoginRequest(
    val email: String,       // Có thể là email hoặc SĐT
    val password: String
)

data class OtpVerifyRequest(
    val email: String,
    val otp_code: String,
    val type: String         // "register" hoặc "forgot_password"
)

data class ForgotPasswordRequest(
    val email: String        // email hoặc SĐT
)

data class ResetPasswordRequest(
    val reset_token: String,
    val new_password: String,
    val confirm_password: String
)

data class ResendOtpRequest(
    val email: String,
    val type: String         // "register" hoặc "forgot_password"
)

// --- Data Classes (Response) ---

data class BaseResponse(
    val success: Boolean,
    val message: String
)

data class UserInfo(
    val id: Int,
    val username: String,
    val email: String,
    val avatar_url: String?
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val expires_at: String?,
    val user: UserInfo?
)

data class RegisterData(
    val user_id: Int?,
    val email: String?
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: RegisterData? // <-- Đã bọc đúng tầng "data" do hàm success() của PHP sinh ra
)

data class OtpVerifyResponse(
    val success: Boolean,
    val message: String,
    val token: String?,       // Có khi type = "register"
    val reset_token: String?, // Có khi type = "forgot_password"
    val user: UserInfo?
)

// --- Retrofit Interface ---

interface ApiService {

    @POST("api/auth/register.php")
    suspend fun register(@Body body: RegisterRequest): Response<RegisterResponse>

    @POST("api/auth/login.php")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("api/auth/verify_otp.php")
    suspend fun verifyOtp(@Body body: OtpVerifyRequest): Response<OtpVerifyResponse>

    @POST("api/auth/forgot_password.php")
    suspend fun forgotPassword(@Body body: ForgotPasswordRequest): Response<BaseResponse>

    @POST("api/auth/reset_password.php")
    suspend fun resetPassword(@Body body: ResetPasswordRequest): Response<BaseResponse>

    @POST("api/auth/resend_otp.php")
    suspend fun resendOtp(@Body body: ResendOtpRequest): Response<BaseResponse>

    @POST("api/auth/logout.php")
    suspend fun logout(@Header("Authorization") token: String): Response<BaseResponse>
}