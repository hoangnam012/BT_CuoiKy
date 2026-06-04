package com.example.myapplication.ui.chatlist

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
data class ActiveUser(val id: String, val name: String, val avatarUrl: String, val isOnline: Boolean)

@Immutable
data class ChatItemData(
    val id: String, val senderName: String, val avatarUrl: String,
    val lastMessage: String, val time: String, val isOnline: Boolean,
    val isGroup: Boolean = false, // Cờ nhận biết Nhóm Chat
    val unreadCount: Int = 0, val isRead: Boolean = true, val isPinned: Boolean = false
)

@Immutable
data class SearchUser(val id: String, val name: String, var isPinned: Boolean = false)

data class TogglePinRequest(val id: String, val isPinned: Boolean, val userId: String)
data class CreateGroupRequest(val groupName: String, val groupType: String, val memberIds: List<String>)

// --- RETROFIT API ---
interface ChatApiService {
    @GET("chatapp/get_chats.php")
    suspend fun getChats(@retrofit2.http.Query("userId") userId: String): List<ChatItemData> // Truyền userId vào

    @GET("chatapp/get_active_users.php")
    suspend fun getActiveUsers(): List<ActiveUser>

    @POST("chatapp/toggle_pin.php")
    suspend fun togglePin(@Body request: TogglePinRequest)

    @GET("chatapp/search_users.php")
    suspend fun searchUsers(@retrofit2.http.Query("q") query: String): List<SearchUser>

    @POST("chatapp/create_group.php")
    suspend fun createGroup(@Body request: CreateGroupRequest)
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2/"
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

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val currentUserId = "u1"
    // [MỚI] Biến chứa kết quả tìm kiếm
    private val _searchResults = MutableStateFlow<List<SearchUser>>(emptyList())
    val searchResults: StateFlow<List<SearchUser>> = _searchResults

    init {
        fetchAllData()
    }

    fun fetchAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Truyền currentUserId vào
                _chatList.value = RetrofitClient.apiService.getChats(currentUserId)
                _activeUsers.value = RetrofitClient.apiService.getActiveUsers()
            } catch (e: Exception) {
                println("Lỗi gọi API: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun togglePin(chatId: String, currentPinStatus: Boolean) {
        viewModelScope.launch {
            try {
                _chatList.value = _chatList.value.map {
                    if (it.id == chatId) it.copy(isPinned = !currentPinStatus) else it
                }.sortedByDescending { it.isPinned }

                // Gửi kèm currentUserId lên server
                RetrofitClient.apiService.togglePin(TogglePinRequest(chatId, !currentPinStatus, currentUserId))
            } catch (e: Exception) {
                fetchAllData()
            }
        }
    }
    // [MỚI] Hàm gọi API tìm kiếm
    fun search(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList() // Xóa kết quả nếu xóa ô tìm kiếm
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
    fun createGroup(name: String, type: String, members: List<SearchUser>) {
        viewModelScope.launch {
            try {
                // 1. Lấy danh sách ID của các thành viên được chọn
                val memberIds = members.map { it.id }

                // 2. Bắn API lên server XAMPP
                val request = CreateGroupRequest(name, type, memberIds)
                RetrofitClient.apiService.createGroup(request)

                // 3. THẦN CHÚ CẬP NHẬT NGAY LẬP TỨC: Gọi lại hàm fetchAllData() để lấy danh sách mới nhất từ DB về
                fetchAllData()
            } catch (e: Exception) {
                println("Lỗi tạo nhóm: ${e.message}")
            }
        }
    }
    // [MỚI] Hàm dọn dẹp kết quả tìm kiếm (khi thoát trang)
    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }
}