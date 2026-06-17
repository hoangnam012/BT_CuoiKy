package com.example.myapplication.chatdetail

import android.content.Context
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.model.Message
import com.example.myapplication.remote.ChatApi
import com.example.myapplication.ui.theme.DiscordGreen
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.myapplication.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    loggedInUserId: String,
    targetUserId: String,
    targetName: String,
    targetAvatarUrl: String = "",
    isGroupChat: Boolean,
    onBack: () -> Unit = {},
    onOpenProfile: (String) -> Unit = {}
) {
    var messages by remember { mutableStateOf(listOf<Message>()) }
    val chatApi = remember { ChatApi.create() }
    val scope = rememberCoroutineScope()

    val myId =loggedInUserId.toIntOrNull() ?: 1
    val targetId = targetUserId
    val isGroupInt = if (isGroupChat) 1 else 0

    var showDeleteDialog by remember { mutableStateOf(false) }
    var messageToDelete by remember { mutableStateOf<Message?>(null) }

    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val context = LocalContext.current

    val bgState = rememberBackgroundState(
        context = context,
        myId = myId,
        targetId = targetId,
        isGroup = isGroupChat
    )
    var showMenu by remember { mutableStateOf(false) }


    fun loadData() {
        scope.launch {
            try {
                val response = chatApi.getMessages(
                    myId,
                    targetId,
                    isGroupInt
                )

                messages = response.map {
                    it.copy(isMine = it.senderId == myId)
                }.toList()

            } catch (e: Exception) {
                android.util.Log.e("LỖI_CHAT", "Chi tiết lỗi: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(targetId) {
        while (true) {
            loadData() // Tự động gọi hàm lấy tin nhắn
            delay(3000L) // Nghỉ 3 giây rồi lặp lại
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Quay lại",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                title = {
                    if (isSearching) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Tìm kiếm...", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = CircleShape
                        )
                    }
                    else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    if (!isGroupChat) {
                                        onOpenProfile(targetUserId)
                                    }
                                }
                                .padding(start = 4.dp, end = 8.dp)
                        ) {
                            AsyncImage(
                                model = if (targetAvatarUrl.isNotEmpty()) targetAvatarUrl
                                else "https://ui-avatars.com/api/?name=${targetName.replace(" ", "+")}&background=random",
                                contentDescription = null,
                                contentScale = ContentScale.Crop, // Thêm ContentScale.Crop để ảnh tròn không bị méo
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = targetName,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = if (isGroupChat) "Nhóm" else "Đang hoạt động",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DiscordGreen
                                )
                            }
                        }
                    }
                },
                actions = {
                    if (isSearching) {
                        IconButton(onClick = {
                            isSearching = false
                            searchQuery = ""
                        }) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Close,
                                contentDescription = "Đóng tìm kiếm"
                            )
                        }
                    } else {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Search,
                                contentDescription = "Tìm kiếm"
                            )
                        }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Settings,
                                    contentDescription = "Cài đặt",
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Đổi ảnh nền") },
                                    onClick = {
                                        showMenu = false
                                        bgState.launchPicker()
                                    }
                                )
                                if (bgState.uri != null) {
                                    DropdownMenuItem(
                                        text = { Text("Xóa ảnh nền", color = Color.Red) },
                                        onClick = {
                                            showMenu = false
                                            bgState.clearUri()
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            if(!isSearching) {
                ChatInputBar(
                    onSendMessage = { text ->
                        scope.launch {
                            try {
                                chatApi.sendMessages(myId, targetId, text, isGroup = isGroupInt)
                                delay(300)
                                loadData()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    onImageSelected = { uri ->
                        scope.launch {
                            val sdfTemp =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val timerTemp = sdfTemp.format(Date())
                            val tempMessage = Message(
                                id = System.currentTimeMillis().toInt(),
                                senderId = myId,
                                receiverId = targetId,
                                content = uri.toString(),
                                type = "image_uploading",
                                isDeleted = 0,
                                created_at = timerTemp,
                                is_group = isGroupInt,
                                isMine = true
                            )
                            messages = listOf(tempMessage) + messages

                            try {
                                val file = getFileFromUri(context, uri)

                                if (file != null) {
                                    val requestFile =
                                        file.asRequestBody("image/*".toMediaTypeOrNull())

                                    var finalFileName = file.name
                                    if (!finalFileName.contains(".")) {
                                        finalFileName += ".jpg"
                                    }
                                    val body =
                                        MultipartBody.Part.createFormData(
                                            "image",
                                            finalFileName,
                                            requestFile
                                        )

                                    val senderIdPart =
                                        myId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                                    val receiverIdPart =
                                        targetId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                                    val isGroupPart =
                                        isGroupInt.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                                    chatApi.sendImage(
                                        senderIdPart,
                                        receiverIdPart,
                                        isGroupPart,
                                        body
                                    )

                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                loadData()
                            }
                        }
                    },
                    onFileSelected = { uri ->
                        scope.launch {
                            try {
                                val fileToSend = getFileFromUri(context, uri)

                                if (fileToSend != null) {
                                    val sdfTemp =
                                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                    val tempMessage = Message(
                                        id = System.currentTimeMillis().toInt(),
                                        senderId = myId,
                                        receiverId = targetId,
                                        content = uri.toString(),
                                        type = "file_uploading",
                                        isDeleted = 0,
                                        created_at = sdfTemp.format(Date()),
                                        isMine = true,
                                        is_group = isGroupInt,
                                        fileName = fileToSend.name
                                    )

                                    messages = listOf(tempMessage) + messages

                                    val requestFile =
                                        fileToSend.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                                    var filePart = MultipartBody.Part.createFormData(
                                        "file",
                                        fileToSend.name,
                                        requestFile
                                    )
                                    val senderIdPart =
                                        myId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                                    val receiverIdPart =
                                        targetId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                                    val isGroupPart =
                                        isGroupInt.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                                    chatApi.upLoadFile(
                                        senderIdPart,
                                        receiverIdPart,
                                        isGroupPart,
                                        filePart
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                loadData()
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).imePadding()) {
            if (bgState.uri != null) {
                AsyncImage(
                    model = bgState.uri,
                    contentDescription = "Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            val filteredMessage = if(searchQuery.isBlank()) {
                messages
            } else {
                messages.filter { msg ->
                            msg.content.contains(searchQuery, ignoreCase = true)
                }
            }

            MessageList(
                messages = filteredMessage,
                padding = PaddingValues(0.dp),
                isSearching = isSearching,
                isGroupChat = isGroupChat,
                targetAvatarUrl = targetAvatarUrl,
                onLongClick = { msg ->
                    messageToDelete = msg
                    showDeleteDialog = true
                }
            )
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Thu hồi tin nhắn") },
                    text = { Text("Bạn có chắc muốn thu hồi tin nhắn này không? Hành động này không thể hoàn tác.") },
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                messageToDelete?.let { msg ->
                                    chatApi.deleteMessages(msg.id)
                                    loadData()
                                }
                                showDeleteDialog = false
                            }
                        }) {
                            Text("Thu hồi", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Hủy")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MessageList(
    messages: List<Message>,
    padding: PaddingValues,
    isSearching: Boolean = false,
    isGroupChat: Boolean,
    targetAvatarUrl: String,
    onLongClick: (Message) -> Unit
) {
    val sortedMessages = messages.sortedBy { it.created_at }

    // Đảo lại: tin mới nhất nằm ở index 0
    val chatMessages = sortedMessages.asReversed()

    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty() && !isSearching) {
            // reverseLayout = true thì index 0 nằm dưới cùng
            listState.scrollToItem(0)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(16.dp),
        reverseLayout = true
    ) {
        itemsIndexed(chatMessages) { index, msg ->

            val nextMsg = chatMessages.getOrNull(index + 1)

            val showTimeHeader =
                nextMsg == null || isOver30Minutes(msg.created_at, nextMsg.created_at)

            Column(modifier = Modifier.fillMaxWidth()) {
                if (showTimeHeader && msg.created_at.isNotBlank()) {
                    Text(
                        text = parseFomatt(msg.created_at),
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
                ChatBubble(
                    message = msg,
                    isGroupChat = isGroupChat,
                    targetAvatarUrl = targetAvatarUrl,
                    onLongClick = { onLongClick(msg) }
                )
            }
        }
    }
}

