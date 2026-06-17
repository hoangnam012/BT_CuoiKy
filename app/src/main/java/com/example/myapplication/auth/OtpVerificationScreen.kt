package com.example.myapplication.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OtpVerificationScreen(navController: NavController, email: String, otpType: String) {
    val otpLength = 6
    var otpValue by remember { mutableStateOf("") }
    var countdown by remember { mutableIntStateOf(29) }
    var canResend by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown--
        }
        canResend = true
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Box(modifier = Modifier.fillMaxSize().background(DiscordVeryDarkGray)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = DiscordLightGray)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3035))) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(72.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFF2B2D31)), contentAlignment = Alignment.Center) {
                        Text(text = "✉\uFE0F", fontSize = 32.sp)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Xác nhận danh tính", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Mã OTP đã được gửi đến email:\n$email", fontSize = 14.sp, color = DiscordLightGray.copy(alpha = 0.7f), textAlign = TextAlign.Center, lineHeight = 22.sp)
                    Spacer(modifier = Modifier.height(32.dp))

                    Box(contentAlignment = Alignment.Center) {
                        BasicTextField(
                            value = otpValue,
                            onValueChange = { newVal ->
                                if (newVal.length <= otpLength && newVal.all { it.isDigit() }) {
                                    otpValue = newVal
                                }
                            },
                            modifier = Modifier.size(1.dp).focusRequester(focusRequester),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                            cursorBrush = SolidColor(Color.Transparent),
                            textStyle = TextStyle(color = Color.Transparent)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.clickable { focusRequester.requestFocus() }) {
                            for (i in 0 until otpLength) {
                                val char = otpValue.getOrNull(i)
                                val isCurrent = i == otpValue.length
                                val isFilled = char != null
                                Box(
                                    modifier = Modifier.size(width = 46.dp, height = 52.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF1E1F22)).border(
                                        width = if (isCurrent) 2.dp else 1.dp,
                                        color = when {
                                            isCurrent -> DiscordBlurple
                                            isFilled -> DiscordBlurple.copy(alpha = 0.5f)
                                            else -> Color(0xFF3D3F45)
                                        }, shape = RoundedCornerShape(10.dp)
                                    ), contentAlignment = Alignment.Center
                                ) {
                                    Text(text = if (isFilled) char.toString() else "—", fontSize = if (isFilled) 22.sp else 16.sp, fontWeight = FontWeight.Bold, color = if (isFilled) Color.White else DiscordLightGray.copy(alpha = 0.3f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            isLoading = true
                            coroutineScope.launch {
                                val result = authRepository.verifyOtp(email, otpValue, otpType)
                                isLoading = false
                                result.onSuccess { response ->
                                    if (otpType == "register") {
                                        Toast.makeText(context, "Xác minh thành công! Hãy đăng nhập.", Toast.LENGTH_LONG).show()
                                        navController.navigate("login") { popUpTo("login") { inclusive = true } }
                                    } else {
                                        Toast.makeText(context, "OTP đúng! Tiến hành đổi mật khẩu.", Toast.LENGTH_LONG).show()
                                        val resetToken = response.reset_token
                                        navController.navigate("reset_password/$resetToken")
                                    }
                                }.onFailure {
                                    Toast.makeText(context, it.message ?: "Mã OTP sai", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = otpValue.length == otpLength && !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple.copy(alpha = 0.3f))
                    ) {
                        if (isLoading) CircularProgressIndicator(color = DiscordBlurple, modifier = Modifier.size(24.dp))
                        else Text(text = "Xác nhận mã", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = DiscordBlurple)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}