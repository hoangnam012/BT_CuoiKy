package com.example.myapplication.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation

/**
 * Extension function này chứa toàn bộ định tuyến của team Auth.
 * Các bạn trong team làm phần khác sẽ không cần đụng vào file này.
 */
fun NavGraphBuilder.authNavGraph(navController: NavController) {
    // Gom nhóm toàn bộ luồng Auth vào một route cha có tên là "auth_graph"
    // Khi vào "auth_graph", màn hình mặc định hiện ra sẽ là "login"
    navigation(startDestination = "login", route = "auth_graph") {

        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        composable("forgot_password") {
            ForgotPasswordScreen(navController = navController)
        }

        composable(
            route = "otp_verify/{email}/{type}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val type = backStackEntry.arguments?.getString("type") ?: "register"

            OtpVerificationScreen(
                navController = navController,
                email = email,
                otpType = type
            )
        }

        // Màn hình Reset Password để tránh crash
        composable(
            route = "reset_password/{resetToken}",
            arguments = listOf(navArgument("resetToken") { type = NavType.StringType })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("resetToken") ?: ""
            ResetPasswordScreen(navController = navController, resetToken = token)
        }
    }
}