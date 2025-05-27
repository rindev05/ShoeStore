package com.example.shoestore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.asPaddingValues
import com.example.shoestore.ui.navigation.AppNavigation
import com.example.shoestore.ui.theme.ShoeStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Đảm bảo nội dung không bị che bởi thanh trạng thái và thanh điều hướng
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Đặt chế độ biểu tượng tối trên thanh trạng thái (dark icons)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true // Biểu tượng tối trên nền sáng

        setContent {
            ShoeStoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background // Sử dụng Material 3 color scheme
                ) {
                    // Áp dụng padding cho tất cả thanh hệ thống
                    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(systemBarsPadding)
                    ) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}