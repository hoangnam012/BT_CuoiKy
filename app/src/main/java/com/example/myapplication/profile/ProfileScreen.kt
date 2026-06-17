// ============================================================
// ui/profile/ProfileScreen.kt
// ============================================================
package com.example.myapplication.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.profile.FriendStatus
import com.example.myapplication.profile.User

// ── Entry Point ───────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    targetUserId: Int,
    loggedInUserId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToChat: (Int, String, String) -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState      by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState  by viewModel.actionState.collectAsStateWithLifecycle()
    var showEditDialog by remember { mutableStateOf(false) }
    var snackMessage   by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(targetUserId) {
        viewModel.loadProfile(targetUserId, loggedInUserId)
    }

    LaunchedEffect(actionState) {
        when (val s = actionState) {
            is ProfileAction.Success -> {
                snackbarHostState.showSnackbar(s.message)
                viewModel.resetActionState()
            }
            is ProfileAction.Error -> {
                snackbarHostState.showSnackbar(s.message)
                viewModel.resetActionState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ProfileTopBar(
                isOwnProfile = (uiState as? ProfileUiState.Success)?.isOwnProfile ?: false,
                onBack = onNavigateBack
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> ProfileLoadingPlaceholder()
                is ProfileUiState.Error   -> ProfileErrorView(state.message) {
                    viewModel.loadProfile(targetUserId, loggedInUserId)
                }
                is ProfileUiState.Success -> {
                    ProfileContent(
                        user         = state.user,
                        friendStatus = state.friendStatus,
                        isOwnProfile = state.isOwnProfile,
                        actionLoading = actionState is ProfileAction.Loading,
                        onSendFriendRequest = { viewModel.sendFriendRequest(state.user.id) },
                        onAcceptFriendRequest = { viewModel.acceptFriendRequest() },
                        onMessage    = {
                            onNavigateToChat(
                                state.user.id,
                                state.user.username,
                                state.user.avatarUrl ?: ""
                            )
                        },
                        onEditProfile = { showEditDialog = true },
                        onLogout     = onLogout
                    )

                    if (showEditDialog) {
                        EditProfileDialog(
                            user        = state.user,
                            onDismiss   = { showEditDialog = false },
                            onSave      = { username, bio, avatarUrl ->
                                viewModel.updateProfile(username, bio, avatarUrl)
                                showEditDialog = false
                            }
                        )
                    }
                }
            }
        }
    }
}

// ── Top App Bar ───────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar(isOwnProfile: Boolean, onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                if (isOwnProfile) "Trang cá nhân" else "Hồ sơ",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 17.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor    = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

// ── Main Content ──────────────────────────────────────────────
@Composable
private fun ProfileContent(
    user: User,
    friendStatus: FriendStatus,
    isOwnProfile: Boolean,
    actionLoading: Boolean,
    onSendFriendRequest: () -> Unit,
    onAcceptFriendRequest: () -> Unit,
    onMessage: () -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header ảnh bìa + avatar
        ProfileHeaderSection(user = user)

        // Hàng nút hành động (Messenger/Zalo style)
        ActionButtonsRow(
            isOwnProfile = isOwnProfile,
            friendStatus = friendStatus,
            actionLoading = actionLoading,
            onSendFriendRequest = onSendFriendRequest,
            onAcceptFriendRequest = onAcceptFriendRequest,
            onMessage = onMessage,
            onEditProfile = onEditProfile,
            onLogout = onLogout
        )

        Spacer(Modifier.height(8.dp))

        // Thông tin chi tiết
        ProfileDetailsCard(user = user)

        Spacer(Modifier.height(32.dp))
    }
}

// ── Header Section ────────────────────────────────────────────
@Composable
private fun ProfileHeaderSection(user: User) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        // Ảnh bìa gradient dùng primary -> secondary (DiscordBlurple -> DiscordGreen)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(primary, secondary)
                    )
                )
        )

        // Trang trí mờ
        Box(
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-20).dp)
                .alpha(0.1f)
                .background(Color.White, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-30).dp, y = 30.dp)
                .alpha(0.08f)
                .background(Color.White, CircleShape)
        )

        // Avatar + tên + trạng thái
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color.White, CircleShape)
                    .background(Color.White, CircleShape)
                    .shadow(8.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AvatarImage(
                    avatarUrl = user.avatarUrl,
                    username  = user.username,
                    size      = 100.dp
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text       = user.username,
                color      = Color.White,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text     = user.email,
                color    = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp
            )

            if (user.isOnline) {
                Spacer(Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f) // vàng nhẹ
                ) {
                    Text(
                        "● Đang hoạt động",
                        color    = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

// ── Avatar dùng chung ─────────────────────────────────────────
@Composable
fun AvatarImage(
    avatarUrl: String?,
    username: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val initial = username.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!avatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Avatar của $username",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Fallback gradient từ primary -> secondary
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = initial,
                    color      = Color.White,
                    fontSize   = (size.value * 0.35f).sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ── Hàng nút hành động (Zalo/Messenger style) ───────────────
@Composable
private fun ActionButtonsRow(
    isOwnProfile: Boolean,
    friendStatus: FriendStatus,
    actionLoading: Boolean,
    onSendFriendRequest: () -> Unit,
    onAcceptFriendRequest: () -> Unit,
    onMessage: () -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    if (isOwnProfile) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionChip(
                text = "Chỉnh sửa",
                icon = Icons.Outlined.Edit,
                containerColor = colorScheme.primary,
                onClick = onEditProfile,
                modifier = Modifier.weight(1f)
            )
            ActionChip(
                text = "Đăng xuất",
                icon = Icons.AutoMirrored.Outlined.ExitToApp,
                containerColor = Color.Transparent,
                borderColor = colorScheme.error,
                contentColor = colorScheme.error,
                onClick = onLogout,
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (friendStatus) {
                FriendStatus.NONE, FriendStatus.PENDING_SENT, FriendStatus.BLOCKED -> {
                    val (btnText, btnIcon, btnColor, enabled) = when (friendStatus) {
                        FriendStatus.NONE -> Quadruple(
                            "Thêm bạn", Icons.Outlined.PersonAdd,
                            colorScheme.primary, true
                        )
                        FriendStatus.PENDING_SENT -> Quadruple(
                            "Đã gửi", Icons.Outlined.HourglassEmpty,
                            colorScheme.tertiary, false
                        )
                        FriendStatus.BLOCKED -> Quadruple(
                            "Đã chặn", Icons.Outlined.Block,
                            colorScheme.outline, false
                        )
                        else -> Quadruple("", Icons.Outlined.Person, Color.Transparent, false)
                    }
                    ActionChip(
                        text = btnText,
                        icon = btnIcon,
                        containerColor = btnColor,
                        enabled = enabled && !actionLoading,
                        showLoading = actionLoading,
                        onClick = onSendFriendRequest,
                        modifier = Modifier.weight(1f)
                    )
                    ActionChip(
                        text = "Nhắn tin",
                        icon = Icons.Outlined.MailOutline,
                        containerColor = colorScheme.primary,
                        onClick = onMessage,
                        modifier = Modifier.weight(1f)
                    )
                }
                FriendStatus.PENDING_RECEIVED -> {
                    ActionChip(
                        text = "Chấp nhận",
                        icon = Icons.Outlined.Check,
                        containerColor = colorScheme.primary, // hoặc tertiary
                        enabled = !actionLoading,
                        showLoading = actionLoading,
                        onClick = onAcceptFriendRequest,
                        modifier = Modifier.weight(1f)
                    )
                    ActionChip(
                        text = "Nhắn tin",
                        icon = Icons.Outlined.MailOutline,
                        containerColor = colorScheme.primary,
                        onClick = onMessage,
                        modifier = Modifier.weight(1f)
                    )
                }
                FriendStatus.ACCEPTED -> {
                    ActionChip(
                        text = "Nhắn tin",
                        icon = Icons.Outlined.MailOutline,
                        containerColor = colorScheme.primary,
                        onClick = onMessage,
                        modifier = Modifier.weight(1f)
                    )
                }
                FriendStatus.SELF -> { /* Không hiển thị */ }
            }
        }
    }
}

// ── Chip hành động nhỏ gọn ──────────────────────────────────
@Composable
private fun ActionChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    borderColor: Color? = null,
    contentColor: Color = Color.White,
    enabled: Boolean = true,
    showLoading: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val actualContainer = if (borderColor != null) Color.Transparent else containerColor
    OutlinedButton(
        onClick = onClick,
        enabled = enabled && !showLoading,
        modifier = modifier.height(IntrinsicSize.Min),
        shape = RoundedCornerShape(14.dp),
        border = if (borderColor != null) BorderStroke(1.5.dp, borderColor) else null,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = actualContainer,
            contentColor = if (borderColor != null) contentColor else Color.White,
            disabledContainerColor = actualContainer.copy(alpha = 0.4f),
            disabledContentColor = if (borderColor != null) contentColor.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.4f)
        )
    ) {
        if (showLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
    }
}

// ── Card thông tin cá nhân (list style) ─────────────────────
@Composable
private fun ProfileDetailsCard(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Text(
                "Thông tin cá nhân",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
            )

            ProfileInfoRow(
                icon = Icons.Outlined.Person,
                label = "Tên người dùng",
                value = user.username
            )
            ProfileInfoRow(
                icon = Icons.Outlined.Email,
                label = "Email",
                value = user.email
            )
            if (!user.bio.isNullOrBlank()) {
                ProfileInfoRow(
                    icon = Icons.Outlined.Info,
                    label = "Giới thiệu",
                    value = user.bio
                )
            }
            if (!user.createdAt.isNullOrBlank()) {
                ProfileInfoRow(
                    icon = Icons.Outlined.CalendarMonth,
                    label = "Tham gia",
                    value = user.createdAt.take(10)
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp)
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

// ── Edit Profile Dialog ──────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (username: String, bio: String, avatarUrl: String?) -> Unit
) {
    var username   by remember { mutableStateOf(user.username) }
    var bio        by remember { mutableStateOf(user.bio ?: "") }
    var avatarUrl  by remember { mutableStateOf(user.avatarUrl ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text("Chỉnh sửa hồ sơ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarImage(
                        avatarUrl = avatarUrl.ifBlank { null },
                        username  = username,
                        size      = 56.dp
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Avatar URL", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }

                OutlinedTextField(
                    value         = avatarUrl,
                    onValueChange = { avatarUrl = it },
                    label         = { Text("URL ảnh đại diện") },
                    placeholder   = { Text("https://...") },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value         = username,
                    onValueChange = { username = it },
                    label         = { Text("Tên người dùng *") },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp),
                    isError       = username.isBlank()
                )

                OutlinedTextField(
                    value         = bio,
                    onValueChange = { bio = it },
                    label         = { Text("Giới thiệu bản thân") },
                    modifier      = Modifier.fillMaxWidth().height(100.dp),
                    maxLines      = 4,
                    shape         = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick  = {
                    if (username.isNotBlank()) {
                        onSave(username.trim(), bio.trim(), avatarUrl.ifBlank { null })
                    }
                },
                shape  = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Lưu thay đổi", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Huỷ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

// ── Loading & Error Views ────────────────────────────────────
@Composable
private fun ProfileLoadingPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Text("Đang tải hồ sơ...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }
    }
}

@Composable
private fun ProfileErrorView(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(56.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Đã xảy ra lỗi",
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
                color      = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                message,
                fontSize  = 14.sp,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                shape   = RoundedCornerShape(12.dp),
                colors  = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Thử lại", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ── Quadruple helper ─────────────────────────────────────────
private data class Quadruple<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
private operator fun <A, B, C, D> Quadruple<A, B, C, D>.component1() = a
private operator fun <A, B, C, D> Quadruple<A, B, C, D>.component2() = b
private operator fun <A, B, C, D> Quadruple<A, B, C, D>.component3() = c
private operator fun <A, B, C, D> Quadruple<A, B, C, D>.component4() = d