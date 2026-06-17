package com.example.myapplication.profile

import com.google.gson.annotations.SerializedName

// ── User ────────────────────────────────────────────────────
data class User(
    @SerializedName("id")          val id: Int,
    @SerializedName("username")    val username: String,
    @SerializedName("email")       val email: String,
    @SerializedName("avatar_url")  val avatarUrl: String?,
    @SerializedName("bio")         val bio: String?,
    @SerializedName("is_online")   val isOnline: Boolean = false,
    @SerializedName("created_at")  val createdAt: String?
)

// ── Friend status ────────────────────────────────────────────
enum class FriendStatus {
    NONE,
    PENDING_SENT,
    PENDING_RECEIVED,
    ACCEPTED,
    BLOCKED,
    SELF;

    companion object {
        fun from(value: String): FriendStatus = when (value.lowercase()) {
            "none"             -> NONE
            "pending_sent"     -> PENDING_SENT
            "pending_received" -> PENDING_RECEIVED
            "accepted"         -> ACCEPTED
            "blocked"          -> BLOCKED
            "self"             -> SELF
            else               -> NONE
        }
    }
}

data class FriendStatusData(
    @SerializedName("status")        val status: String,
    @SerializedName("friendship_id") val friendshipId: Int?
)

// ── Generic API response wrapper ─────────────────────────────
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data")    val data: T?
)

// ── Request bodies ────────────────────────────────────────────
data class SendFriendRequestBody(
    @SerializedName("sender_id")   val senderId: Int,
    @SerializedName("receiver_id") val receiverId: Int
)


data class UpdateProfileBody(
    @SerializedName("user_id")    val userId: Int,
    @SerializedName("username")   val username: String?,
    @SerializedName("bio")        val bio: String?,
    @SerializedName("avatar_url") val avatarUrl: String?
)

// ── UpdatedUser (returned by update_profile.php) ─────────────
data class UpdatedUser(
    @SerializedName("id")         val id: Int,
    @SerializedName("username")   val username: String,
    @SerializedName("bio")        val bio: String?,
    @SerializedName("avatar_url") val avatarUrl: String?
)