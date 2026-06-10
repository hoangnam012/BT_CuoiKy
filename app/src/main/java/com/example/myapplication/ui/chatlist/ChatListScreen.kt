package com.example.myapplication.ui.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChatListScreen(viewModel: ChatViewModel = viewModel()) {
    var selectedTab by remember { mutableStateOf(0) }
    var currentRoute by remember { mutableStateOf("MAIN") }

    val chatList by viewModel.chatList.collectAsState()
    val activeUsers by viewModel.activeUsers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val recentSearches = remember {
        mutableStateListOf(
            SearchUser("1", "Trần Đức Mạnh"), SearchUser("2", "Lê Ngọc Hoàng Anh"),
            SearchUser("3", "Nguyễn Linh"), SearchUser("4", "Phạm Hải", isPinned = true),
            SearchUser("5", "Lê Trang"), SearchUser("6", "Vũ Tuấn")
        )
    }

    val selectedGroupMembers = remember { mutableStateListOf<SearchUser>() }
    var groupName by remember { mutableStateOf("") }
    var groupType by remember { mutableStateOf("Nhóm") }

    Scaffold(
        containerColor = BackgroundDark,
        bottomBar = { if (currentRoute == "MAIN") MainBottomNavigationBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it }) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (currentRoute) {
                "MAIN" -> {
                    if (selectedTab == 0) {
                        ChatsTabScreen(
                            chatList = chatList,
                            activeUsers = activeUsers,
                            isLoading = isLoading,
                            onSearchClick = { currentRoute = "SEARCH_MAIN" },
                            onEditClick = { currentRoute = "NEW_MESSAGE" },
                            onPinToggle = { id, isPinned -> viewModel.togglePin(id, isPinned) }
                        )
                    } else { Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) }
                }
                "SEARCH_MAIN" -> SearchMainScreen(recentSearches, { currentRoute = "MAIN" }, { currentRoute = "SEARCH_EDIT" }, { currentRoute = "SEARCH_ACTIVE" })
                "SEARCH_EDIT" -> SearchEditScreen(recentSearches, { currentRoute = "SEARCH_MAIN" })
                "SEARCH_ACTIVE" -> {
                    val searchResults by viewModel.searchResults.collectAsState()
                    SearchActiveScreen(
                        searchResults = searchResults,
                        onSearchQueryChange = { query -> viewModel.search(query) },
                        onBack = {
                            viewModel.clearSearchResults() // Dọn dẹp dữ liệu khi thoát
                            currentRoute = "SEARCH_MAIN"
                        }
                    )
                }
                "NEW_MESSAGE" -> NewMessageScreen(
                    contacts = chatList, // <--- Truyền thẳng danh sách Chat thật vào thay vì recentSearches
                    onBack = { currentRoute = "MAIN" },
                    onGroupClick = { selectedGroupMembers.clear(); groupName = ""; currentRoute = "NEW_GROUP" },
                    onPinClick = { currentRoute = "PIN_CHAT" }
                )
                "NEW_GROUP" -> NewGroupScreen(recentSearches, selectedGroupMembers, { currentRoute = "NEW_MESSAGE" }, { currentRoute = "GROUP_INFO" })
                "GROUP_INFO" -> GroupInfoScreen(
                    selectedMembers = selectedGroupMembers,
                    groupName = groupName,
                    onGroupNameChange = { groupName = it },
                    groupType = groupType,
                    onGroupTypeChange = { groupType = it },
                    onBack = { currentRoute = "NEW_GROUP" },
                    onCreate = {
                        // 1. Gọi hàm lưu vào CSDL
                        viewModel.createGroup(groupName, groupType, selectedGroupMembers)

                        // 2. Quay về màn hình chính, lúc này danh sách đã tự động chớp mắt cập nhật nhóm mới!
                        currentRoute = "MAIN"
                    }
                )
                "PIN_CHAT" -> PinChatScreen(
                    contacts = chatList, // Đưa danh sách Chat từ DB vào
                    onBack = { currentRoute = "NEW_MESSAGE" },
                    onDone = { currentRoute = "NEW_MESSAGE" },
                    onPinToggle = { id, isPinned -> viewModel.togglePin(id, isPinned) } // Truyền hàm gọi CSDL vào
                )
            }
        }
    }
}

@Composable
fun ChatsTabScreen(
    chatList: List<ChatItemData>,
    activeUsers: List<ActiveUser>,
    isLoading: Boolean,
    onSearchClick: () -> Unit,
    onEditClick: () -> Unit,
    onPinToggle: (String, Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopHeader(title = "CHATS", showEdit = true, onEditClick = onEditClick)
        // Gọi list Active Users động từ Database
        ActiveUsersRow(users = activeUsers)

        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                .background(SurfaceDark, RoundedCornerShape(12.dp))
                .clickable { onSearchClick() }
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, tint = Color.Gray, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tìm kiếm...", color = Color.Gray)
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PinkAccent)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(chatList) { chat ->
                    ChatItemRow(chatItem = chat, onPinToggle = { onPinToggle(chat.id, chat.isPinned) })
                }
            }
        }
    }
}