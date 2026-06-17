package com.example.myapplication.remote

import android.media.Image
import com.example.myapplication.model.Message
import com.example.myapplication.model.SimpleResponse
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Headers

interface ChatApi {

    @Headers("Cache-Control: no-cache")
    @GET("BT_CuoiKyBackend/get_messages.php")
    suspend fun getMessages(
        @Query("sender_id") senderId: Int,
        @Query("receiver_id") receiverId: String,
        @Query("is_group") isGroup: Int,
        @Query("t") timestamp: Long = System.currentTimeMillis()
    ): List<Message>

    @FormUrlEncoded
    @POST("BT_CuoiKyBackend/send_message.php")
    suspend fun sendMessages(
        @Field("sender_id") senderId: Int,
        @Field("receiver_id") receiverId: String,
        @Field("content") content: String,
        @Field("is_group") isGroup: Int
    ): SimpleResponse

    @GET("BT_CuoiKyBackend/get_group_messages.php")
    suspend fun getGroupMessages(
        @Query("group_id") group_id: Int
    ): List<Message>

    @Multipart
    @POST("BT_CuoiKyBackend/send_image.php")
    suspend fun sendImage(
        @Part("sender_id") senderId: RequestBody,
        @Part("receiver_id") receiverId: RequestBody,
        @Part("is_group") isGroup: RequestBody,
        @Part image: MultipartBody.Part
    ): SimpleResponse

    @Multipart
    @POST("BT_CuoiKyBackend/upload_file.php")
    suspend fun upLoadFile(
        @Part("sender_id") senderId: RequestBody,
        @Part("receiver_id") receiverId: RequestBody,
        @Part("is_group") isGroup: RequestBody,
        @Part file: MultipartBody.Part
    ): SimpleResponse

    @FormUrlEncoded
    @POST("BT_CuoiKyBackend/delete_message.php")
    suspend fun deleteMessages(
        @Field("message_id") messageId: Int
    ): SimpleResponse

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8081/"

        val gson = GsonBuilder().setLenient().create()
        fun create(): ChatApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ChatApi::class.java)
        }
    }
}