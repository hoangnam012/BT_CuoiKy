package com.example.myapplication.auth

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OtpVerificationScreen(
    onVerifyClick: (otp: String) -> Unit = {},
    onResendCode: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val otpLength = 6
    var otpValue by remember { mutableStateOf("") }
    var countdown by remember { mutableIntStateOf(29) }
    var canResend by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    // Countdown timer
    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown--
        }
        canResend = true
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DiscordVeryDarkGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Back button + Security icon top right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = DiscordLightGray
                    )
                }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DiscordBlurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "\uD83D\uDEE1\uFE0F", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main content card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3035))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Email icon
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF2B2D31)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✉\uFE0F", fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Xác nhận danh tính",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Chúng tôi đã gửi mã xác minh gồm 6 chữ số đến email của bạn. Vui lòng nhập mã để tiếp tục.",
                        fontSize = 14.sp,
                        color = DiscordLightGray.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // OTP Input: hidden real field + visual boxes
                    Box(contentAlignment = Alignment.Center) {
                        // Hidden text field
                        BasicTextField(
                            value = otpValue,
                            onValueChange = { newVal ->
                                if (newVal.length <= otpLength && newVal.all { it.isDigit() }) {
                                    otpValue = newVal
                                    if (newVal.length == otpLength) {
                                        onVerifyClick(newVal)
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(1.dp)
                                .focusRequester(focusRequester),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = ImeAction.Done
                            ),
                            cursorBrush = SolidColor(Color.Transparent),
                            textStyle = TextStyle(color = Color.Transparent)
                        )

                        // Visual OTP boxes
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.clickable { focusRequester.requestFocus() }
                        ) {
                            for (i in 0 until otpLength) {
                                val char = otpValue.getOrNull(i)
                                val isCurrent = i == otpValue.length
                                val isFilled = char != null

                                Box(
                                    modifier = Modifier
                                        .size(width = 46.dp, height = 52.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF1E1F22))
                                        .border(
                                            width = if (isCurrent) 2.dp else 1.dp,
                                            color = when {
                                                isCurrent -> DiscordBlurple
                                                isFilled -> DiscordBlurple.copy(alpha = 0.5f)
                                                else -> Color(0xFF3D3F45)
                                            },
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isFilled) char.toString() else "—",
                                        fontSize = if (isFilled) 22.sp else 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isFilled) Color.White
                                        else DiscordLightGray.copy(alpha = 0.3f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Verify button
                    Button(
                        onClick = { onVerifyClick(otpValue) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = otpValue.length == otpLength,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DiscordBlurple.copy(alpha = 0.3f),
                            disabledContainerColor = DiscordBlurple.copy(alpha = 0.15f)
                        )
                    ) {
                        Text(
                            text = "Xác nhận mã",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (otpValue.length == otpLength) DiscordBlurple
                            else DiscordLightGray.copy(alpha = 0.4f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Resend section
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Không nhận được mã?  ",
                                fontSize = 14.sp,
                                color = DiscordLightGray.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Gửi lại mã",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (canResend) DiscordBlurple
                                else DiscordLightGray.copy(alpha = 0.4f),
                                modifier = Modifier.clickable(enabled = canResend) {
                                    if (canResend) {
                                        onResendCode()
                                        canResend = false
                                        countdown = 29
                                        scope.launch {
                                            while (countdown > 0) {
                                                delay(1000L)
                                                countdown--
                                            }
                                            canResend = true
                                        }
                                    }
                                }
                            )
                        }
                        if (!canResend) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Gửi lại sau ${countdown}s",
                                fontSize = 12.sp,
                                color = DiscordLightGray.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Security tip card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3035))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Bảo mật",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DiscordYellow
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Kiểm tra thư mục Thư rác hoặc Quảng cáo nếu không thấy mã trong Hộp thư đến chính.",
                            fontSize = 13.sp,
                            color = DiscordLightGray.copy(alpha = 0.6f),
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                listOf("Chính sách", "Hỗ trợ", "Trạng thái").forEach { item ->
                    Text(
                        text = item,
                        fontSize = 12.sp,
                        color = DiscordLightGray.copy(alpha = 0.4f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "CUOI KI ANDROID",
                fontSize = 11.sp,
                color = DiscordLightGray.copy(alpha = 0.25f)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OtpVerificationScreenPreview() {
    MyApplicationTheme {
        OtpVerificationScreen()
    }
}