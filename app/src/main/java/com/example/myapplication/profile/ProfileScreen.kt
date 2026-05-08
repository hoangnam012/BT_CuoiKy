package com.example.myapplication.profile

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@SuppressLint("ViewModelConstructorInComposable") // Cho phép tạo ViewModel trực tiếp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val user by viewModel.userState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = { /* Xử lý back */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                user?.let {
                    Text(
                        text = it.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = it.phone,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } ?: run {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(24.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.Info,
                    title = "Thông tin tài khoản",
                    onClick = {
                        Toast.makeText(context, "Thông tin tài khoản", Toast.LENGTH_SHORT).show()
                        viewModel.onMenuItemClick("account_info")
                    }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Lock,
                    title = "Quyền riêng tư",
                    onClick = {
                        Toast.makeText(context, "Quyền riêng tư", Toast.LENGTH_SHORT).show()
                        viewModel.onMenuItemClick("privacy")
                    }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Notifications,
                    title = "Thông báo",
                    onClick = {
                        Toast.makeText(context, "Thông báo", Toast.LENGTH_SHORT).show()
                        viewModel.onMenuItemClick("notifications")
                    }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Cài đặt chung",
                    onClick = {
                        Toast.makeText(context, "Cài đặt chung", Toast.LENGTH_SHORT).show()
                        viewModel.onMenuItemClick("settings")
                    }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Language,
                    title = "Ngôn ngữ",
                    onClick = {
                        Toast.makeText(context, "Ngôn ngữ", Toast.LENGTH_SHORT).show()
                        viewModel.onMenuItemClick("language")
                    }
                )
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Đăng xuất",
                    onClick = {
                        Toast.makeText(context, "Đăng xuất", Toast.LENGTH_SHORT).show()
                        viewModel.onMenuItemClick("logout")
                    },
                    isDestructive = true
                )
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

// Preview không dùng MyApplicationTheme (tránh lỗi API 31)
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    // Dùng MaterialTheme mặc định thay vì MyApplicationTheme để preview không báo lỗi API 31
    MaterialTheme {
        ProfileScreen(viewModel = ProfileViewModel())
    }
}