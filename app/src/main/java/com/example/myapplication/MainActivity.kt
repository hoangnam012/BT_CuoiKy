package com.example.myapplication

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.auth.authNavGraph
import com.example.myapplication.chatdetail.ChatDetailScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            MyApplicationTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        // Điểm bắt đầu bây giờ là gọi vào route cha của Auth
                        startDestination = "auth_graph"
                    ) {
                        // 1. Toàn bộ Navigation của phần Auth nằm ở đây
                        authNavGraph(navController = navController)

                        // 2. Các bạn khác trong nhóm làm phần nào thì thêm vào đây
                        // (Ví dụ: chatNavGraph(navController), profileNavGraph(navController)...)
                        composable("chat_detail") {
                            ChatDetailScreen()
                        }

                    }
                }

            }
        }
    }
}