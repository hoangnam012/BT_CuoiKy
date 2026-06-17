package com.example.myapplication.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun SearchMainScreen(recentSearches: List<SearchUser>, onBack: () -> Unit, onEditClick: () -> Unit, onSearchBarClick: () -> Unit) {
    val topSearches = remember(recentSearches) { recentSearches.take(10) }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White) }
            Box(modifier = Modifier.weight(1f).background(SurfaceDark, RoundedCornerShape(12.dp)).clickable { onSearchBarClick() }.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, tint = Color.Gray, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("Tìm kiếm...", color = Color.Gray)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Tìm kiếm gần đây", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "Chỉnh sửa", color = PinkAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onEditClick() })
        }
        LazyVerticalGrid(columns = GridCells.Fixed(5), contentPadding = PaddingValues(horizontal = 8.dp), modifier = Modifier.fillMaxWidth()) {
            items(topSearches) { user ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 16.dp)) {
                    Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.Gray), contentAlignment = Alignment.Center) { Text(text = user.name.take(1), color = Color.White, fontWeight = FontWeight.Bold) }
                    Spacer(modifier = Modifier.height(4.dp)); Text(text = user.name, color = Color.LightGray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun SearchEditScreen(recentSearches: MutableList<SearchUser>, onBack: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false }, containerColor = SurfaceDark, title = { Text("Xóa lịch sử", color = Color.White) }, text = { Text("Bạn có muốn xóa tất cả lịch sử tìm kiếm hay không?", color = Color.LightGray) }, confirmButton = { TextButton(onClick = { recentSearches.clear(); showDialog = false }) { Text("Xóa", color = PinkAccent) } }, dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Hủy", color = Color.White) } })
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White) }; Text("Chỉnh sửa lịch sử tìm kiếm", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Tìm kiếm gần đây", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            if (recentSearches.isNotEmpty()) { Text(text = "Xóa tất cả", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.clickable { showDialog = true }) }
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(recentSearches) { user ->
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.DarkGray), contentAlignment = Alignment.Center) { Text(text = user.name.take(1), color = Color.White) }; Spacer(modifier = Modifier.width(16.dp)); Text(text = user.name, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f)); IconButton(onClick = { recentSearches.remove(user) }) { Icon(Icons.Default.Close, contentDescription = "Xóa", tint = Color.Gray) }
                }
            }
        }
    }
}

@Composable
fun SearchActiveScreen(
    searchResults: List<SearchUser>, // Nhận kết quả từ ViewModel
    onSearchQueryChange: (String) -> Unit, // Hàm bắn từ khóa lên ViewModel
    onBack: () -> Unit,
    onUserClick: (SearchUser) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { try { focusRequester.requestFocus() } catch (e: Exception) { } }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White) }
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onSearchQueryChange(it) // <--- Gọi hàm tìm kiếm mỗi khi gõ phím
                },
                modifier = Modifier.weight(1f).focusRequester(focusRequester),
                placeholder = { Text("Tìm kiếm...", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceDark, unfocusedContainerColor = SurfaceDark,
                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
        }

        // KIỂM TRA VÀ HIỂN THỊ KẾT QUẢ TỪ DATABASE
        if (searchQuery.isNotEmpty()) {
            if (searchResults.isEmpty()) {
                Text(text = "Không tìm thấy kết quả nào", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(16.dp))
            } else {
                Text(text = "Kết quả tìm kiếm", color = PinkAccent, fontSize = 14.sp, modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp))
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(searchResults) { user ->
                        Row(modifier = Modifier.fillMaxWidth().clickable {
                        onUserClick(user)
                        }.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.DarkGray), contentAlignment = Alignment.Center) {
                                Text(text = user.name.take(1), color = Color.White, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = user.name, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewMessageScreen(
    users: List<SearchUser>,
    onBack: () -> Unit,
    onGroupClick: () -> Unit,
    onPinClick: () -> Unit,
    onUserClick: (SearchUser) -> Unit
) {
    var query by remember { mutableStateOf("") }

    // Lọc danh sách theo từ khóa tìm kiếm (nếu có gõ)
    val filteredUsers = if (query.isBlank()) users else users.filter {
        it.name.contains(query, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White) }; Text("Tin nhắn mới", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center); Spacer(modifier = Modifier.width(48.dp))
        }
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Đến:", color = Color.Gray, fontSize = 16.sp); Spacer(modifier = Modifier.width(8.dp)); TextField(value = query, onValueChange = { query = it }, modifier = Modifier.weight(1f), colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = Color.White), placeholder = { Text("Tên người dùng...", color = Color.DarkGray) })
        }
        HorizontalDivider(color = SurfaceDark)
        Row(modifier = Modifier.fillMaxWidth().clickable { onGroupClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.GroupAdd, contentDescription = null, tint = Color.White); Spacer(modifier = Modifier.width(16.dp)); Text("Nhóm chat", color = Color.White, fontSize = 16.sp) }
        Row(modifier = Modifier.fillMaxWidth().clickable { onPinClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.PushPin, contentDescription = null, tint = Color.White); Spacer(modifier = Modifier.width(16.dp)); Text("Ghim", color = Color.White, fontSize = 16.sp) }
        Text("Gợi ý", color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))

        // Hiển thị danh sách Gợi ý từ Database có thể lướt
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredUsers) { user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onUserClick(user)
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val avatarUrl = user.avatarUrl
                    if (!avatarUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.DarkGray)
                        )
                    } else {
                        AsyncImage(
                            model = "https://ui-avatars.com/api/?name=${user.name.replace(" ", "+")}&background=random",
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.DarkGray)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = user.name,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun NewGroupScreen(contacts: List<SearchUser>, selectedMembers: MutableList<SearchUser>, onBack: () -> Unit, onNext: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }; val filteredContacts = if (searchQuery.isEmpty()) contacts else contacts.filter { it.name.contains(searchQuery, ignoreCase = true) }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White) }; Text("Nhóm mới", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            if (selectedMembers.size >= 2) { TextButton(onClick = onNext) { Text("Tiếp", color = PinkAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold) } } else { Spacer(modifier = Modifier.width(48.dp)) }
        }
        TextField(value = searchQuery, onValueChange = { searchQuery = it }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), placeholder = { Text("Tìm kiếm người dùng...", color = Color.Gray) }, leadingIcon = { Icon(Icons.Default.Search, tint = Color.Gray, contentDescription = null) }, shape = RoundedCornerShape(12.dp), colors = TextFieldDefaults.colors(focusedContainerColor = SurfaceDark, unfocusedContainerColor = SurfaceDark, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = Color.White), singleLine = true)
        Text(if (searchQuery.isEmpty()) "Gợi ý" else "Kết quả", color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredContacts) { user -> val isSelected = selectedMembers.contains(user)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { if (isSelected) selectedMembers.remove(user) else selectedMembers.add(user) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val avatarUrl = user.avatarUrl
                    if (!avatarUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.DarkGray)
                        )
                    } else {
                        AsyncImage(
                            model = "https://ui-avatars.com/api/?name=${user.name.replace(" ", "+")}&background=random",
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.DarkGray)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = user.name, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))

                    // Dấu check chọn nhóm
                    if (isSelected) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PinkAccent, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Outlined.Circle, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun GroupInfoScreen(selectedMembers: MutableList<SearchUser>, groupName: String, onGroupNameChange: (String) -> Unit, groupType: String, onGroupTypeChange: (String) -> Unit, onBack: () -> Unit, onCreate: () -> Unit) {
    val isCreateEnabled = groupName.isNotBlank()
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White) }; Text("Thông tin nhóm", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center); TextButton(onClick = onCreate, enabled = isCreateEnabled) { Text("Tạo", color = if (isCreateEnabled) PinkAccent else Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
        TextField(value = groupName, onValueChange = onGroupNameChange, modifier = Modifier.fillMaxWidth().padding(16.dp), placeholder = { Text("Đặt tên nhóm...", color = Color.Gray) }, colors = TextFieldDefaults.colors(focusedContainerColor = SurfaceDark, unfocusedContainerColor = SurfaceDark, focusedIndicatorColor = PinkAccent, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = Color.White), singleLine = true)
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onGroupTypeChange("Nhóm") }) { RadioButton(selected = groupType == "Nhóm", onClick = { onGroupTypeChange("Nhóm") }, colors = RadioButtonDefaults.colors(selectedColor = PinkAccent)); Text("Nhóm", color = Color.White) }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onGroupTypeChange("Cộng đồng") }) { RadioButton(selected = groupType == "Cộng đồng", onClick = { onGroupTypeChange("Cộng đồng") }, colors = RadioButtonDefaults.colors(selectedColor = PinkAccent)); Text("Cộng đồng", color = Color.White) }
        }
        Text("Thành viên được mời (${selectedMembers.size})", color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(4), contentPadding = PaddingValues(horizontal = 16.dp), modifier = Modifier.fillMaxWidth()) {
            items(selectedMembers) { user ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 16.dp)) {
                    Box {
                        val avatarUrl = user.avatarUrl
                        if (!avatarUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.DarkGray)
                            )
                        } else {
                            AsyncImage(
                                model = "https://ui-avatars.com/api/?name=${user.name.replace(" ", "+")}&background=random",
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.DarkGray)
                            )
                        }
                        // ─────────────────────────────────────

                        // Nút X nhỏ xóa thành viên
                        Box(modifier = Modifier.size(20.dp).align(Alignment.TopEnd).clip(CircleShape).background(Color.DarkGray).border(1.dp, BackgroundDark, CircleShape).clickable { selectedMembers.remove(user) }, contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = user.name, color = Color.LightGray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun PinChatScreen(
    contacts: List<ChatItemData>, // 1. Đổi sang dùng danh sách thật từ Database
    onBack: () -> Unit,
    onDone: () -> Unit,
    onPinToggle: (String, Boolean) -> Unit // 2. Nhận hàm gọi API từ ViewModel
) {
    val sortedContacts = contacts.sortedByDescending { it.isPinned }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White) }
            Text("Ghim", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            TextButton(onClick = onDone) { Text("Hoàn tất", color = PinkAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
        Text("Gợi ý", color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(sortedContacts) { user ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable {
                        // 3. GỌI THẲNG XUỐNG VIEWMODEL ĐỂ LƯU VÀO DATABASE KHI BẤM!
                        onPinToggle(user.id, user.isPinned)
                    }.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hiển thị Avatar thật
                    if (user.avatarUrl.isNotEmpty()) {
                        AsyncImage(
                            model = user.avatarUrl,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.DarkGray)
                        )
                    } else {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.Gray), contentAlignment = Alignment.Center) {
                            Text(text = user.senderName.take(1), color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = user.senderName, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.PushPin, contentDescription = null, tint = if (user.isPinned) PinkAccent else Color.DarkGray)
                }
            }
        }
    }
}