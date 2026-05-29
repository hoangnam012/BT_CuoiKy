package com.example.myapplication.profile

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Lấy profile user
    @GET("get_user_profile.php")
    suspend fun getUserProfile(
        @Query("user_id") userId: Int
    ): Response<ApiResponse<User>>

    // Kiểm tra trạng thái bạn bè
    @GET("check_friend_status.php")
    suspend fun checkFriendStatus(
        @Query("user_id") userId: Int,
        @Query("target_id") targetId: Int
    ): Response<ApiResponse<FriendStatusData>>

    // Gửi lời mời kết bạn
    @POST("send_friend_request.php")
    suspend fun sendFriendRequest(
        @Body body: SendFriendRequestBody
    ): Response<ApiResponse<Any>>

    // Update profile
    @PUT("update_profile.php")
    suspend fun updateProfile(
        @Body body: UpdateProfileBody
    ): Response<ApiResponse<UpdatedUser>>
}