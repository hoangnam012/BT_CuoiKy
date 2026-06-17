package com.example.myapplication

import android.net.Uri
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.auth.authNavGraph
import com.example.myapplication.chatdetail.ChatDetailScreen
import com.example.myapplication.chatlist.ChatListScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.profile.ProfileScreen

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
                        startDestination = "auth_graph"
                    ) {
                        authNavGraph(navController = navController)

                        composable("profile") {
                            ProfileScreen(
                                targetUserId = 1,
                                loggedInUserId = 1,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onNavigateToChat = { userId, userName, avatarUrl ->
                                    val encodeName = Uri.encode(userName)
                                    val encodeAvatar = Uri.encode(avatarUrl)
                                    navController.navigate("chat_detail/1/$userId/$encodeName/false?avatarUrl=$encodeAvatar")
                                },
                                onLogout = {
                                    navController.navigate("auth_graph") {
                                        popUpTo("profile") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(
                            route = "chat_list/{loggedInUserId}",
                            arguments = listOf(
                                navArgument("loggedInUserId") { type = NavType.StringType},
                            )
                        ) {backStackEntry->

                            val loggedInUserId = backStackEntry.arguments
                                ?.getString("loggedInUserId") ?: "1"


                            ChatListScreen(
                                loggedInUserId = loggedInUserId,
                                onOpenChatDetail = { targetUserId, targetName, avatarUrl, isGroupChat ->
                                    val encodeName = Uri.encode(targetName)
                                    val encodeAvatar = Uri.encode(avatarUrl)
                                    navController.navigate("chat_detail/$loggedInUserId/$targetUserId/$encodeName/$isGroupChat?avatarUrl=$encodeAvatar")
                                },
                                onOpenMyProfile = {
                                    navController.navigate("profile/$loggedInUserId/$loggedInUserId")
                                }

                            )
                        }
                        composable(
                            route = "chat_detail/{loggedInUserId}/{targetUserId}/{targetName}/{isGroupChat}?avatarUrl={avatarUrl}",
                            arguments = listOf(
                                navArgument("loggedInUserId") { type = NavType.StringType },
                                navArgument("targetUserId") { type = NavType.StringType },
                                navArgument("targetName") { type = NavType.StringType },
                                navArgument("isGroupChat") { type = NavType.BoolType},
                                navArgument("avatarUrl") {
                                    type = NavType.StringType
                                    defaultValue = ""
                                }
                            )
                        ) { backStackEntry ->

                            val loggedInUserId = backStackEntry.arguments
                                ?.getString("loggedInUserId") ?: "1"

                            val targetUserId = backStackEntry.arguments
                                ?.getString("targetUserId") ?: "2"

                            val targetName = backStackEntry.arguments
                                ?.getString("targetName") ?: "User"

                            val isGroupChat = backStackEntry.arguments?.getBoolean("isGroupChat") ?: false

                            val targetAvatarUrl = backStackEntry.arguments?.getString("avatarUrl") ?: ""

                            ChatDetailScreen(
                                loggedInUserId = loggedInUserId,
                                targetUserId = targetUserId,
                                targetName = targetName,
                                targetAvatarUrl = targetAvatarUrl,
                                isGroupChat = isGroupChat,
                                onBack = {
                                    navController.popBackStack()
                                },
                                onOpenProfile = { userId ->
                                    navController.navigate("profile/$userId/$loggedInUserId")
                                }
                            )
                        }
                        composable(
                            route = "profile/{targetUserId}/{loggedInUserId}",
                            arguments = listOf(
                                navArgument("targetUserId") { type = NavType.IntType },
                                navArgument("loggedInUserId") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->

                            val targetUserId = backStackEntry.arguments
                                ?.getInt("targetUserId") ?: 0

                            val loggedInUserId = backStackEntry.arguments
                                ?.getInt("loggedInUserId") ?: 0

                            ProfileScreen(
                                targetUserId = targetUserId,
                                loggedInUserId = loggedInUserId,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onNavigateToChat = { userId, userName, avatarUrl ->
                                    val encodeName = Uri.encode(userName)
                                    val encodeAvatar = Uri.encode(avatarUrl)
                                    // Sửa số 1 thành $loggedInUserId ở đây nè
                                    navController.navigate("chat_detail/$loggedInUserId/$userId/$encodeName/false?avatarUrl=$encodeAvatar")
                                },
                                onLogout = {
                                    navController.navigate("auth_graph") {
                                        popUpTo(0)
                                    }
                                }
                            )
                        }

                    }
                }

            }
        }
    }
}