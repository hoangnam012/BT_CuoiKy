package com.example.myapplication.auth

import com.example.myapplication.network.*

class AuthRepository {

    private val api = RetrofitClient.apiService

    /** Đăng ký tài khoản mới */
    suspend fun register(username: String, email: String, phone: String, password: String): Result<RegisterResponse> {
        return try {
            // Sửa lỗi thiếu tham số phone bằng cách truyền phone vào đây
            val response = api.register(RegisterRequest(username, email, phone, password))

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                // Nếu backend PHP trả về thành công (success = true)
                if (body.success) {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body.message))
                }
            } else {
                // Đọc nội dung lỗi JSON khi server từ chối xử lý (Mã lỗi 400 Bad Request)
                val errorBody = response.errorBody()?.string()
                if (!errorBody.isNullOrEmpty()) {
                    val jsonError = com.google.gson.JsonParser().parse(errorBody).asJsonObject
                    val errorMessage = jsonError.get("message")?.asString ?: "Lỗi không xác định"
                    Result.failure(Exception(errorMessage))
                } else {
                    Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối đến máy chủ. Kiểm tra XAMPP đang chạy."))
        }
    }

    /** Đăng nhập */
    suspend fun login(emailOrPhone: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(emailOrPhone, password))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) Result.success(body)
                else Result.failure(Exception(body.message))
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối đến máy chủ."))
        }
    }

    /** Xác minh OTP */
    suspend fun verifyOtp(email: String, otp: String, type: String): Result<OtpVerifyResponse> {
        return try {
            val response = api.verifyOtp(OtpVerifyRequest(email, otp, type))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) Result.success(body)
                else Result.failure(Exception(body.message))
            } else {
                // Đọc lỗi chi tiết từ PHP thay vì chỉ báo Lỗi 400
                val errorBody = response.errorBody()?.string()
                if (!errorBody.isNullOrEmpty()) {
                    val jsonError = com.google.gson.JsonParser().parse(errorBody).asJsonObject
                    val errorMessage = jsonError.get("message")?.asString ?: "Lỗi không xác định"
                    Result.failure(Exception(errorMessage))
                } else {
                    Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối đến máy chủ."))
        }
    }

    /** Quên mật khẩu — gửi OTP */
    suspend fun forgotPassword(emailOrPhone: String): Result<BaseResponse> {
        return try {
            val response = api.forgotPassword(ForgotPasswordRequest(emailOrPhone))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối đến máy chủ."))
        }
    }

    /** Đặt lại mật khẩu mới */
    suspend fun resetPassword(resetToken: String, newPassword: String, confirmPassword: String): Result<BaseResponse> {
        return try {
            val response = api.resetPassword(ResetPasswordRequest(resetToken, newPassword, confirmPassword))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) Result.success(body)
                else Result.failure(Exception(body.message))
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối đến máy chủ."))
        }
    }

    /** Gửi lại OTP */
    suspend fun resendOtp(email: String, type: String): Result<BaseResponse> {
        return try {
            val response = api.resendOtp(ResendOtpRequest(email, type))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối đến máy chủ."))
        }
    }

    /** Đăng xuất */
    suspend fun logout(token: String): Result<BaseResponse> {
        return try {
            val response = api.logout("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Lỗi máy chủ: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối đến máy chủ."))
        }
    }
}