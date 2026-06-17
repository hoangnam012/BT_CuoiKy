package com.example.myapplication.chatlist

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// --- BẢNG MÀU ---
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val PinkAccent = Purple80               // Các nút bấm, số tin nhắn, icon ghim sẽ chuyển sang Tím Sáng
val SurfaceDark = Color(0xFF2B2930)     // Màu nền thanh tìm kiếm, menu dưới (Tone Xám Tím chuẩn M3)
val BackgroundDark = Color(0xFF141218)  // Nền tổng thể của app (Đen ám tím sâu)

// Giữ nguyên các màu báo trạng thái
val OnlineGreen = Color(0xFF4CAF50)
val OfflineOrange = Color(0xFFFF9800)

// --- DATA MODELS ---
@Immutable
data class ActiveUser(val id: String, val name: String, val avatarUrl: String? = "", val isOnline: Boolean)

@Immutable
data class ChatItemData(
    val id: String, val senderName: String, val avatarUrl: String,
    val lastMessage: String, val time: String, val isOnline: Boolean,
    val isGroup: Boolean = false, // Cờ nhận biết Nhóm Chat
    val unreadCount: Int = 0, val isRead: Boolean = true, val isPinned: Boolean = false
)

@Immutable
data class SearchUser(val id: String, val name: String, var isPinned: Boolean = false,val avatarUrl: String? = "")

data class TogglePinRequest(val id: String, val isPinned: Boolean, val userId: String)
data class CreateGroupRequest(val groupName: String, val groupType: String, val memberIds: List<String>, val creatorId: String)

// --- RETROFIT API ---
interface ChatApiService {
    @GET("BT_CuoiKyBackend/chatlist/get_chats.php")
    suspend fun getChats(@Query("userId") userId: String): List<ChatItemData>

    @GET("BT_CuoiKyBackend/chatlist/get_active_users.php")
    suspend fun getActiveUsers(): List<ActiveUser>

    @POST("BT_CuoiKyBackend/chatlist/toggle_pin.php")
    suspend fun togglePin(@Body request: TogglePinRequest)

    @GET("BT_CuoiKyBackend/chatlist/search_users.php")
    suspend fun searchUsers(@Query("q") query: String): List<SearchUser>

    @POST("BT_CuoiKyBackend/chatlist/create_group.php")
    suspend fun createGroup(@Body request: CreateGroupRequest)

    @GET("BT_CuoiKyBackend/chatlist/get_all_users.php")
    suspend fun getAllUsers(): List<SearchUser>
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8081/"
    val apiService: ChatApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatApiService::class.java)
    }
}

// --- VIEWMODEL ---
class ChatViewModel : ViewModel() {
    private val _chatList = MutableStateFlow<List<ChatItemData>>(emptyList())
    val chatList: StateFlow<List<ChatItemData>> = _chatList

    private val _activeUsers = MutableStateFlow<List<ActiveUser>>(emptyList())
    val activeUsers: StateFlow<List<ActiveUser>> = _activeUsers

    private val _allUsers = MutableStateFlow<List<SearchUser>>(emptyList())
    val allUsers: StateFlow<List<SearchUser>> = _allUsers

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchResults = MutableStateFlow<List<SearchUser>>(emptyList())
    val searchResults: StateFlow<List<SearchUser>> = _searchResults

    fun fetchAllData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _chatList.value = RetrofitClient.apiService.getChats(userId)
                _activeUsers.value = RetrofitClient.apiService.getActiveUsers()
                _allUsers.value = RetrofitClient.apiService
                    .getAllUsers()
                    .filter { it.id != userId }
            } catch (e: Exception) {
                println("Lỗi gọi API: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun togglePin(userId: String, chatId: String, currentPinStatus: Boolean) {
        viewModelScope.launch {
            try {
                _chatList.value = _chatList.value.map {
                    if (it.id == chatId) it.copy(isPinned = !currentPinStatus) else it
                }.sortedByDescending { it.isPinned }

                RetrofitClient.apiService.togglePin(
                    TogglePinRequest(chatId, !currentPinStatus, userId)
                )
            } catch (e: Exception) {
                fetchAllData(userId)
            }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                _searchResults.value = RetrofitClient.apiService.searchUsers(query)
            } catch (e: Exception) {
                println("Lỗi tìm kiếm: ${e.message}")
                _searchResults.value = emptyList()
            }
        }
    }

    fun createGroup(
        userId: String,
        name: String,
        type: String,
        members: List<SearchUser>,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val memberIds = (members.map { it.id } + userId).distinct()

                val request = CreateGroupRequest(
                    groupName = name,
                    groupType = type,
                    memberIds = memberIds,
                    creatorId = userId
                )

                RetrofitClient.apiService.createGroup(request)

                fetchAllData(userId)

                onSuccess()
            } catch (e: Exception) {
                println("Lỗi tạo nhóm: ${e.message}")
            }
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }
}