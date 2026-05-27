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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordScreen(navController: NavController, resetToken: String) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    Box(modifier = Modifier.fillMaxSize().background(DiscordVeryDarkGray)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = DiscordLightGray)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFF2B2D31)), contentAlignment = Alignment.Center) {
                Text(text = "\uD83D\uDD12", fontSize = 40.sp) // Biểu tượng ổ khóa
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text(text = "Tạo mật khẩu mới", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Mật khẩu mới của bạn phải khác với các mật khẩu đã sử dụng trước đây.", fontSize = 14.sp, color = DiscordLightGray.copy(alpha = 0.7f), textAlign = TextAlign.Center, lineHeight = 22.sp)
            Spacer(modifier = Modifier.height(40.dp))

            // Field Mật khẩu mới
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "MẬT KHẨU MỚI", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DiscordLightGray.copy(alpha = 0.6f), letterSpacing = 0.8.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it; errorMessage = "" },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DiscordBlurple, unfocusedBorderColor = Color(0xFF3D3F45),
                        focusedContainerColor = Color(0xFF2B2D31), unfocusedContainerColor = Color(0xFF2B2D31),
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = DiscordBlurple
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null, tint = DiscordLightGray.copy(alpha = 0.5f))
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Field Xác nhận mật khẩu
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "XÁC NHẬN MẬT KHẨU", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DiscordLightGray.copy(alpha = 0.6f), letterSpacing = 0.8.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errorMessage = "" },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = errorMessage.isNotEmpty(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (errorMessage.isNotEmpty()) DiscordRed else DiscordBlurple,
                        unfocusedBorderColor = if (errorMessage.isNotEmpty()) DiscordRed.copy(alpha = 0.5f) else Color(0xFF3D3F45),
                        focusedContainerColor = Color(0xFF2B2D31), unfocusedContainerColor = Color(0xFF2B2D31),
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = DiscordBlurple
                    ),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null, tint = DiscordLightGray.copy(alpha = 0.5f))
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    singleLine = true
                )
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = errorMessage, fontSize = 12.sp, color = DiscordRed)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (newPassword.isBlank() || confirmPassword.isBlank()) {
                        errorMessage = "Vui lòng nhập đầy đủ thông tin"
                    } else if (newPassword.length < 6) {
                        errorMessage = "Mật khẩu phải có ít nhất 6 ký tự"
                    } else if (newPassword != confirmPassword) {
                        errorMessage = "Mật khẩu xác nhận không khớp"
                    } else {
                        isLoading = true
                        coroutineScope.launch {
                            val result = authRepository.resetPassword(resetToken, newPassword, confirmPassword)
                            isLoading = false
                            result.onSuccess {
                                Toast.makeText(context, "Đổi mật khẩu thành công! Hãy đăng nhập lại.", Toast.LENGTH_LONG).show()
                                // Xóa toàn bộ stack và văng người dùng ra lại màn hình đăng nhập
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }.onFailure {
                                Toast.makeText(context, it.message ?: "Lỗi", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text(text = "Xác nhận đổi mật khẩu", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}