package com.example.myapplication.chatdetail

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*


data class BackgroundState(
    val uri: Uri?,
    val clearUri: () -> Unit,
    val launchPicker: () -> Unit
)

@Composable
fun rememberBackgroundState(
    context: Context,
    myId: Int,
    targetId: Int,
    isGroup: Boolean
    ): BackgroundState {

    var backgroundUri by remember {
        mutableStateOf(BackgroundPreferences.getBackgroundLocal(context, myId, targetId, isGroup))
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val copiedUri = BackgroundPreferences.copyImageToInternalStorage(context, uri)

                if (copiedUri != null) {
                    backgroundUri = copiedUri

                    BackgroundPreferences.saveBackGroundLocal(context, myId, targetId, isGroup, copiedUri)
                }
            }
        }
    )

    return BackgroundState(
        uri = backgroundUri,
        clearUri = { backgroundUri = null
            BackgroundPreferences.clearBackgroundLocal(context, myId, targetId, isGroup)},
        launchPicker = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    )
}