package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class Message(
    val id: Int,

    @SerializedName("sender_id")
    val senderId: Int,

    @SerializedName("receiver_id")
    val receiverId: String,

    val content: String,
    val type: String? = "text",
    val created_at: String,
    val avatar: String? = null,
    val isMine: Boolean = false,

    @SerializedName("is_deleted")
    val isDeleted: Int =0,

    @SerializedName("file_name")
    val fileName: String? = null,

    val is_group: Int = 0,
    val sender_name: String? = null,
    val sender_avatar: String? = null
)
data class SimpleResponse(
    val status: String,
    val message: String
)