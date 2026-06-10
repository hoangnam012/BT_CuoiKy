package com.example.myapplication.chatdetail

import android.content.Context
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen() {
    var messages by remember { mutableStateOf(listOf<Message>()) }
    val chatApi = remember { ChatApi.create() }
    val scope = rememberCoroutineScope()

    val myId =1;
    val currentGroupId =1
    val isGroupChat = true

    var showDeleteDialog by remember { mutableStateOf(false) }
    var messageToDelete by remember { mutableStateOf<Message?>(null) }

    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val context = LocalContext.current

    val bgState = rememberBackgroundState(
        context = context,
        myId = myId,
        targetId = currentGroupId,
        isGroup = isGroupChat
    )
    var showMenu by remember { mutableStateOf(false) }


    fun loadData() {
        scope.launch {
            try {
                val response = if (isGroupChat) {
                    chatApi.getGroupMessages(currentGroupId)
                } else {
                    chatApi.getMessages(myId, 2)
                }
                messages = response.reversed().map { it.copy(isMine = it.senderId == 1) }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    LaunchedEffect(Unit) { loadData() }

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = if (isGroupChat) "https://ui-avatars.com/api/?name=Nhom+Do+An&background=random"
                                else "https://ui-avatars.com/api/?name=Thang+Pham&background=random",
                                contentDescription = null,
                                modifier = Modifier.size(36.dp).clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isGroupChat) "Nhóm Đồ Án VKU" else "Thắng Phạm",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = if (isGroupChat) "3 thành viên" else "Đang hoạt động",
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
                                chatApi.sendMessages(1, 1, text, isGroup = 1)
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
                                senderId = 1,
                                receiverId = 1,
                                content = uri.toString(),
                                type = "image_uploading",
                                isDeleted = 0,
                                created_at = timerTemp,
                                is_group = 1,
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
                                        "1".toRequestBody("text/plain".toMediaTypeOrNull())
                                    val receiverIdPart =
                                        "1".toRequestBody("text/plain".toMediaTypeOrNull())
                                    val isGroupPart =
                                        "1".toRequestBody("text/plain".toMediaTypeOrNull())

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
                                        senderId = 1,
                                        receiverId = 1,
                                        content = uri.toString(),
                                        type = "file_uploading",
                                        isDeleted = 0,
                                        created_at = sdfTemp.format(Date()),
                                        isMine = true,
                                        is_group = 1,
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
                                        "1".toRequestBody("text/plain".toMediaTypeOrNull())
                                    val receiverIdPart =
                                        "1".toRequestBody("text/plain".toMediaTypeOrNull())
                                    val isGroupPart =
                                        "1".toRequestBody("text/plain".toMediaTypeOrNull())

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
fun MessageList(messages: List<Message>, padding: PaddingValues, isSearching: Boolean = false, onLongClick: (Message) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        reverseLayout = !isSearching
    ) {
        itemsIndexed(messages) { index, msg ->
            var oldMsg = messages.getOrNull(index +1)
            var showTimeHeader = oldMsg == null || isOver30Minutes(msg.created_at, oldMsg.created_at)
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
                    isGroupChat = true,
                    onLongClick = { onLongClick(msg) }
                )
            }
        }
    }
}
