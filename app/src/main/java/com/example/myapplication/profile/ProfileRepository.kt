package com.example.myapplication.profile

class ProfileRepository(
    private val api: ApiService = RetrofitClient.instance
) {

    suspend fun getUserProfile(userId: Int): Result<User> {
        return try {
            val response = api.getUserProfile(userId)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(
                    Exception(response.body()?.message ?: "Không thể tải profile")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkFriendStatus(
        userId: Int,
        targetId: Int
    ): Result<FriendStatusData> {

        return try {
            val response = api.checkFriendStatus(userId, targetId)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(
                    Exception(response.body()?.message ?: "Lỗi kiểm tra bạn bè")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendFriendRequest(
        senderId: Int,
        receiverId: Int
    ): Result<String> {

        return try {

            val body = SendFriendRequestBody(senderId, receiverId)

            val response = api.sendFriendRequest(body)

            if (response.isSuccessful && response.body()?.success == true) {

                Result.success(
                    response.body()?.message ?: "Đã gửi lời mời"
                )

            } else {

                Result.failure(
                    Exception(response.body()?.message ?: "Gửi thất bại")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(
        userId: Int,
        username: String,
        bio: String,
        avatarUrl: String?
    ): Result<UpdatedUser> {

        return try {

            val body = UpdateProfileBody(
                userId,
                username,
                bio,
                avatarUrl
            )

            val response = api.updateProfile(body)

            if (response.isSuccessful && response.body()?.success == true) {

                Result.success(response.body()!!.data!!)

            } else {

                Result.failure(
                    Exception(response.body()?.message ?: "Cập nhật thất bại")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun acceptFriendRequest(
        senderId: Int,
        receiverId: Int
    ): Result<String> {
        return try {
            val body = SendFriendRequestBody(senderId, receiverId)
            val response = api.acceptFriendRequest(body)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.message ?: "Đã chấp nhận")
            } else {
                Result.failure(Exception(response.body()?.message ?: "Lỗi chấp nhận"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}