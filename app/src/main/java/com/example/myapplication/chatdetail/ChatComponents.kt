package com.example.myapplication.chatdetail


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.model.Message
import com.example.myapplication.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(
    message: Message,
    isGroupChat: Boolean = false,
    targetAvatarUrl: String = "",
    onLongClick: (Int) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val isDeleted = message.isDeleted == 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isMine) {
            val avatarName = message.sender_name ?: "User"
            val avatarUrl = if (!message.sender_avatar.isNullOrEmpty()) {
                message.sender_avatar
            } else if (targetAvatarUrl.isNotEmpty()) {
                targetAvatarUrl
            } else {
                "https://ui-avatars.com/api/?name=${avatarName.replace(" ", "+")}&background=random"
            }
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(32.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(
            horizontalAlignment = if (message.isMine) Alignment.End else Alignment.Start
        ) {
            if (isGroupChat && !message.isMine && !message.sender_name.isNullOrEmpty()) {
                Text(
                    text = message.sender_name,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth(0.85f),
                contentAlignment = if (message.isMine) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Surface(
                    color = when {
                        isDeleted -> Color.LightGray.copy(alpha = 0.2f)
                        message.type == "image" && !isDeleted -> Color.Transparent
                        message.isMine -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(
                        topStart = 16.dp, topEnd = 16.dp,
                        bottomStart = if (message.isMine) 16.dp else 0.dp,
                        bottomEnd = if (message.isMine) 0.dp else 16.dp
                    ),
                    tonalElevation = 2.dp,
                    modifier = Modifier.combinedClickable(
                        onClick = { },
                        onLongClick = {
                            if(message.isMine && !isDeleted) {
                                onLongClick(message.id)
                            }
                        }
                    )
                ) {
                    if ((message.type == "image" || message.type == "image_uploading") && !isDeleted) {
                        val isUploading = message.type == "image_uploading"
                        Box(contentAlignment = Alignment.Center) {
                            AsyncImage(
                                model = if (isUploading) Uri.parse(message.content) else message.content,
                                contentDescription = "Image message",
                                modifier = Modifier
                                    .width(200.dp)
                                    .heightIn(max = 300.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                alpha = if (isUploading) 0.5f else 1f
                            )
                            if (isUploading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                    else if ((message.type == "file" || message.type == "file_uploading") && !isDeleted) {
                        val isUploading = message.type == "file_uploading"
                        val displayFileName = message.fileName ?: "Tệp đính kèm"

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    color = if (message.isMine) Color.White.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                                .alpha(if (isUploading) 0.5f else 1f)
                                .clickable {
                                    try {
                                        uriHandler.openUri(message.content)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_file),
                                contentDescription = null,
                                tint = if (message.isMine) Color.White else Color.Black,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.widthIn(max = 180.dp)) {
                                Text(
                                    text = displayFileName,
                                    color = if (message.isMine) Color.White else Color.Black,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                )
                                Text(
                                    text = "Tệp đính kèm",
                                    color = if (message.isMine) Color.White.copy(alpha = 0.7f) else Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (isUploading) {
                                Spacer(modifier = Modifier.width(12.dp))
                                CircularProgressIndicator(
                                    color = if (message.isMine) Color.White else MaterialTheme.colorScheme.primary,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    else {
                        Text(
                            text = if (isDeleted) "Tin nhắn đã bị thu hồi" else message.content,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            color = when {
                                isDeleted -> Color.Gray
                                message.isMine -> Color.White
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInputBar(
    onSendMessage: (String) -> Unit,
    onImageSelected: (Uri) -> Unit,
    onFileSelected: (Uri) -> Unit
) {
    var textState by remember { mutableStateOf("") }
    var selectedImageUri by remember {mutableStateOf<Uri?>(null)}
    var showEmojiPicker by remember { mutableStateOf(false) }

    //Image
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if(uri!= null) {
            selectedImageUri = uri
        }
    }

    //File
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        uri: Uri? ->
        if(uri!= null) {
            onFileSelected(uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AnimatedVisibility(visible = showEmojiPicker) {
            EmojiGrid(
                emojis = emojiList,
                onEmojiSelected = { selectedEmoji ->
                    textState += selectedEmoji
                }
            )
        }
        AnimatedVisibility(visible = selectedImageUri != null) {
            Box(modifier = Modifier.padding(start = 16.dp, top = 8.dp)) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Preview",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                IconButton(
                    onClick = { selectedImageUri = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .background(Color.Red, CircleShape)
                        .padding(4.dp)
                ) {
                    Text("X", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    pickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.size(45.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_image),
                    contentDescription = "Chọn ảnh",
                    modifier = Modifier.fillMaxSize().padding(8.dp),

                    tint = Color.Unspecified
                )
            }
            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = { showEmojiPicker = !showEmojiPicker },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_icon),
                    contentDescription = "Chọn Icon",
                    modifier = Modifier.fillMaxSize().padding(8.dp),

                    tint = Color.Unspecified
                )
            }
            IconButton(
                onClick = {
                    filePickerLauncher.launch("*/*")
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_file),
                    contentDescription = "Chọn File",
                    modifier = Modifier.fillMaxSize().padding(8.dp),

                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Nhắn tin...", color = Color.Gray) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(8.dp))


            IconButton(
                onClick = {
                    if (selectedImageUri != null) {
                        onImageSelected(selectedImageUri!!)
                        selectedImageUri = null
                    }
                    if (textState.isNotBlank()) {
                        onSendMessage(textState)
                        textState = ""
                    }
                    showEmojiPicker = false
                },
                modifier = Modifier.background(
                    color = if (textState.isNotBlank()) MaterialTheme.colorScheme.primary else Color.DarkGray,
                    shape = CircleShape
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}


