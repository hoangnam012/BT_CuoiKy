package com.example.myapplication.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.DiscordBlurple
import com.example.myapplication.ui.theme.DiscordLightGray

/**
 * Reusable labeled text field for auth screens.
 * Used by LoginScreen and RegisterScreen.
 */
@Composable
fun AuthTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    placeholder: String = ""
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = DiscordLightGray.copy(alpha = 0.6f),
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            isError = isError,
            placeholder = if (placeholder.isNotEmpty()) {
                {
                    Text(
                        text = placeholder,
                        color = DiscordLightGray.copy(alpha = 0.3f),
                        fontSize = 15.sp
                    )
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) Color(0xFFED4245) else DiscordBlurple,
                unfocusedBorderColor = if (isError) Color(0xFFED4245).copy(alpha = 0.5f) else Color.Transparent,
                focusedContainerColor = Color(0xFF1E1F22),
                unfocusedContainerColor = Color(0xFF1E1F22),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = DiscordBlurple
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            singleLine = true
        )
    }
}