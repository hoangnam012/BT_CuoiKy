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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
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
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val authRepository = remember { AuthRepository() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(DiscordVeryDarkGray)) {
        Box(
            modifier = Modifier.fillMaxWidth().height(250.dp).background(
                Brush.verticalGradient(colors = listOf(DiscordGreen.copy(alpha = 0.06f), Color.Transparent))
            )
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Lumina", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DiscordLightGray)
            }

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3035))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Tạo tài khoản", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Tham gia cùng hàng triệu người dùng!", fontSize = 14.sp, color = DiscordLightGray.copy(alpha = 0.7f), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(28.dp))

                    AuthTextField(
                        label = "TÊN NGƯỜI DÙNG",
                        value = username,
                        onValueChange = { username = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AuthTextField(
                        label = "EMAIL HOẶC SỐ ĐIỆN THOẠI",
                        value = email,
                        onValueChange = { email = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "MẬT KHẨU", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DiscordLightGray.copy(alpha = 0.6f), letterSpacing = 0.8.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; passwordError = "" },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DiscordBlurple, unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color(0xFF1E1F22), unfocusedContainerColor = Color(0xFF1E1F22),
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = DiscordBlurple
                            ),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null, tint = DiscordLightGray.copy(alpha = 0.5f))
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "XÁC NHẬN MẬT KHẨU", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DiscordLightGray.copy(alpha = 0.6f), letterSpacing = 0.8.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it; passwordError = "" },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            isError = passwordError.isNotEmpty(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (passwordError.isNotEmpty()) DiscordRed else DiscordBlurple,
                                unfocusedBorderColor = if (passwordError.isNotEmpty()) DiscordRed.copy(alpha = 0.5f) else Color.Transparent,
                                focusedContainerColor = Color(0xFF1E1F22), unfocusedContainerColor = Color(0xFF1E1F22),
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
                        if (passwordError.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = passwordError, fontSize = 12.sp, color = DiscordRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Bằng cách đăng ký, bạn đồng ý với Điều khoản dịch vụ và Chính sách bảo mật.", fontSize = 12.sp, color = DiscordLightGray.copy(alpha = 0.5f), textAlign = TextAlign.Center, lineHeight = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                            } else if (password != confirmPassword) {
                                passwordError = "Mật khẩu xác nhận không khớp"
                            } else {
                                isLoading = true
                                coroutineScope.launch {
                                    val result = authRepository.register(username.trim(), email.trim(), "", password)
                                    isLoading = false
                                    result.onSuccess { response ->
                                        Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                        // Đăng ký thành công -> Chuyển sang màn OTP và truyền kèm email qua argument của Điều hướng
                                        navController.navigate("otp_verify/${email.trim()}/register")
                                    }.onFailure { exception ->
                                        Toast.makeText(context, "Lỗi: ${exception.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple),
                        enabled = !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text(text = "Đăng ký", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Đã có tài khoản?  ", fontSize = 14.sp, color = DiscordLightGray.copy(alpha = 0.7f))
                        Text(text = "Đăng nhập", fontSize = 14.sp, color = DiscordBlurple, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { navController.popBackStack() })
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}