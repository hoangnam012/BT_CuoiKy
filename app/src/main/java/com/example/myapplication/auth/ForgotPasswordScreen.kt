package com.example.myapplication.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    Box(modifier = Modifier.fillMaxSize().background(DiscordVeryDarkGray)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = DiscordLightGray)
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
            Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFF2B2D31)), contentAlignment = Alignment.Center) {
                Text(text = "↻", fontSize = 48.sp, color = DiscordBlurple)
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text(text = "Khôi phục tài khoản", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Nhập email của bạn để nhận mã xác minh bảo mật.", fontSize = 14.sp, color = DiscordLightGray.copy(alpha = 0.7f), textAlign = TextAlign.Center, lineHeight = 22.sp)
            Spacer(modifier = Modifier.height(40.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "EMAIL HOẶC SỐ ĐIỆN THOẠI", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DiscordLightGray.copy(alpha = 0.6f), letterSpacing = 0.8.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text(text = "ten@example.com", color = DiscordLightGray.copy(alpha = 0.3f), fontSize = 15.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = null, tint = DiscordLightGray.copy(alpha = 0.4f), modifier = Modifier.size(20.dp)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DiscordBlurple, unfocusedBorderColor = Color(0xFF3D3F45),
                        focusedContainerColor = Color(0xFF2B2D31), unfocusedContainerColor = Color(0xFF2B2D31),
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = DiscordBlurple
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (email.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập Email", Toast.LENGTH_SHORT).show()
                    } else {
                        isLoading = true
                        coroutineScope.launch {
                            val result = authRepository.forgotPassword(email.trim())
                            isLoading = false
                            result.onSuccess {
                                Toast.makeText(context, "Mã xác minh đã được gửi!", Toast.LENGTH_SHORT).show()
                                // Chuyển sang màn OTP và đính kèm thông tin email + loại hành động quên mật khẩu
                                navController.navigate("otp_verify/${email.trim()}/forgot_password")
                            }.onFailure {
                                Toast.makeText(context, it.message ?: "Lỗi", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple.copy(alpha = 0.3f)),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = DiscordBlurple, modifier = Modifier.size(24.dp))
                else Text(text = "Gửi mã xác minh", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = DiscordBlurple)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { navController.popBackStack() }.padding(8.dp)) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = DiscordLightGray.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Quay lại Đăng nhập", fontSize = 14.sp, color = DiscordLightGray.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
            }
        }
    }
}