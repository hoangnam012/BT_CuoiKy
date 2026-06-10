package com.example.myapplication.ui.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun TopHeader(title: String, showEdit: Boolean, onEditClick: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
        if (showEdit) IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White) }
    }
}

@Composable
fun ActiveUsersRow(users: List<ActiveUser>) {
    LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(users) { user ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(60.dp)) {
                Box {
                    Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.Gray), contentAlignment = Alignment.Center) { Text(text = user.name.take(1), color = Color.White, fontWeight = FontWeight.Bold) }
                    if (user.isOnline) {
                        Box(modifier = Modifier.size(14.dp).align(Alignment.TopEnd).offset((-2).dp, 2.dp).clip(CircleShape).background(OnlineGreen).border(2.dp, BackgroundDark, CircleShape))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = user.name, color = Color.LightGray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun ChatItemRow(chatItem: ChatItemData, onPinToggle: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box {
            if (chatItem.avatarUrl.isNotEmpty()) {
                AsyncImage(
                    model = chatItem.avatarUrl, contentDescription = "Avatar", contentScale = ContentScale.Crop,
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.DarkGray)
                )
            } else {
                Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.DarkGray), contentAlignment = Alignment.Center) {
                    Text(text = chatItem.senderName.take(1), color = Color.White, fontSize = 20.sp)
                }
            }

            // LOGIC CHẤM TRẠNG THÁI (Ẩn nếu là nhóm, Đổi màu nếu là cá nhân)
            if (!chatItem.isGroup) {
                val statusColor = if (chatItem.isOnline) OnlineGreen else OfflineOrange
                Box(modifier = Modifier.size(14.dp).align(Alignment.TopEnd).offset((-2).dp, 2.dp).clip(CircleShape).background(statusColor).border(2.dp, BackgroundDark, CircleShape))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = chatItem.senderName.uppercase(), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = chatItem.lastMessage, color = if (chatItem.unreadCount > 0) Color.White else Color.Gray, fontWeight = if (chatItem.unreadCount > 0) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (chatItem.unreadCount > 0) {
                    Box(modifier = Modifier.background(PinkAccent, RoundedCornerShape(6.dp)).padding(horizontal = 6.dp, vertical = 2.dp), contentAlignment = Alignment.Center) { Text(text = chatItem.unreadCount.toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                } else if (chatItem.isRead) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = PinkAccent, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = chatItem.time, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Icon(Icons.Default.PushPin, contentDescription = null, tint = if (chatItem.isPinned) PinkAccent else Color.DarkGray, modifier = Modifier.size(18.dp).clickable { onPinToggle() })
        }
    }
}

@Composable
fun MainBottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(containerColor = SurfaceDark, tonalElevation = 8.dp) {
        NavigationBarItem(icon = { Icon(Icons.Default.Chat, contentDescription = null) }, label = { Text("Chat") }, selected = selectedTab == 0, onClick = { onTabSelected(0) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PinkAccent, unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent))
        NavigationBarItem(icon = { Icon(Icons.Default.Notifications, contentDescription = null) }, label = { Text("Thông báo") }, selected = selectedTab == 1, onClick = { onTabSelected(1) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PinkAccent, unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent))
        NavigationBarItem(icon = { Icon(Icons.Default.Contacts, contentDescription = null) }, label = { Text("Danh bạ") }, selected = selectedTab == 2, onClick = { onTabSelected(2) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PinkAccent, unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent))
        NavigationBarItem(icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) }, label = { Text("Reel") }, selected = selectedTab == 3, onClick = { onTabSelected(3) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PinkAccent, unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent))
        NavigationBarItem(icon = { Icon(Icons.Default.Settings, contentDescription = null) }, label = { Text("Cài đặt") }, selected = selectedTab == 4, onClick = { onTabSelected(4) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PinkAccent, unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent))
    }
}