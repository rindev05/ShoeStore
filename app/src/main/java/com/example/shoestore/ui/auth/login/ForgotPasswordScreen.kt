package com.example.shoestore.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shoestore.ui.theme.ShoeStoreTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    ShoeStoreTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Forgot Password",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Enter your email address to receive a password reset link.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Email TextField
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email address", style = MaterialTheme.typography.bodyMedium) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Reset Password Button
                Button(
                    onClick = {
                        if (email.isBlank()) {
                            Toast.makeText(context, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.(com|net|org)$"
                        if (!email.matches(Regex(emailPattern))) {
                            Toast.makeText(context, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true

                        // Gửi email đặt lại mật khẩu trực tiếp qua Firebase Authentication
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                // Định dạng thời gian hiện tại
                                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                                    .withZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                                val timestamp = Instant.now().atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                                    .format(formatter)

                                // Chuẩn bị dữ liệu log
                                val logData = hashMapOf(
                                    "email" to email,
                                    "timestamp" to timestamp,
                                    "status" to if (task.isSuccessful) "success" else "failed"
                                )

                                // Lưu log vào Firestore
                                FirebaseFirestore.getInstance()
                                    .collection("password_reset_logs")
                                    .add(logData)
                                    .addOnSuccessListener {
                                        println("Đã lưu log yêu cầu đặt lại mật khẩu: $logData")
                                    }
                                    .addOnFailureListener { e ->
                                        println("Lưu log thất bại: ${e.message}")
                                    }

                                // Xử lý kết quả gửi email
                                isLoading = false
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Email đặt lại mật khẩu đã được gửi đến $email. Vui lòng kiểm tra hộp thư của bạn.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    navController.navigate("LoginScreen") {
                                        popUpTo("ForgotPasswordScreen") { inclusive = true }
                                    }
                                } else {
                                    val errorMessage = task.exception?.message ?: "Đã có lỗi xảy ra"
                                    Toast.makeText(
                                        context,
                                        "Gửi email thất bại: $errorMessage",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Send Reset Link", style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nút quay lại đăng nhập
                TextButton(
                    onClick = { navController.navigate("LoginScreen") },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Back to Sign In",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Black
                    )
                }
            }
        }
    }
}