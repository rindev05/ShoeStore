package com.example.shoestore.ui.auth.login

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
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Email đặt lại mật khẩu đã được gửi đến $email. Vui lòng kiểm tra hộp thư của bạn.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    // Điều hướng về màn hình đăng nhập
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