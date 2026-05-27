package com.example.myapplication.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DiscordBlurple,
    secondary = DiscordGreen,
    tertiary = DiscordYellow,
    background = DiscordDarkGray,
    surface = DiscordVeryDarkGray,
    onPrimary = Color.White,
    onBackground = DiscordLightGray,
    onSurface = DiscordLightGray
)

private val LightColorScheme = lightColorScheme(
    primary = DiscordBlurple,
    secondary = Color(0xFF404EED),
    tertiary = DiscordYellow,
    background = Color.White,
    surface = Color(0xFFF2F3F5),
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

// ĐÃ XÓA dòng @RequiresApi ở đây để app chạy được trên mọi máy
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Vẫn để false để giữ màu Discord
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Code bên trong đã tự check an toàn rồi nên không cần @RequiresApi bên ngoài nữa
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}