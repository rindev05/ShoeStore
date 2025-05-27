package com.example.shoestore.ui.splash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.shoestore.R
import android.content.Intent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.shoestore.ui.theme.ShoeStoreTheme
import com.example.shoestore.ui.welcome.WelcomeScreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    ShoeStoreTheme {
        // Box với logo ở giữa
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.shoe_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(180.dp), // Điều chỉnh kích thước logo
                colorFilter = ColorFilter.tint(Color.White)
            )
        }

        // Delay và điều hướng sau 2 giây
        LaunchedEffect(key1 = true) {
            delay(2000L) // Đợi 2 giây - có thể điều chỉnh thời gian
            navController.navigate("WelcomeScreen") {
                // Xóa SplashScreen khỏi back stack để người dùng không quay lại
                popUpTo("SplashScreen") { inclusive = true }
            }
        }
    }
}
