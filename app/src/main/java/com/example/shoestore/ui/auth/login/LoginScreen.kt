package com.example.shoestore.ui.auth.login

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoestore.MainActivity
import com.example.shoestore.R
import com.example.shoestore.ui.auth.signup.SignUpScreen
import com.example.shoestore.ui.theme.ShoeStoreTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AnimatedBackground() {
    val screenHeight = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() +
            WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

    // Animation for right to left
    val rightToLeftOffset = rememberInfiniteTransition()
    val rightToLeftX = rightToLeftOffset.animateFloat(
        initialValue = 1000f,
        targetValue = -1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Animation for left to right
    val leftToRightOffset = rememberInfiniteTransition()
    val leftToRightX = leftToRightOffset.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // First "SHOE STORE" text (right to left)
        Text(
            text = "SHOE STORE   VIP STORE",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 100.sp),
            color = Color.Black,
            modifier = Modifier
                .alpha(0.4f)
                .offset(x = rightToLeftX.value.dp, y = (-230).dp)
                .align(Alignment.Center)
        )

        // Second "SHOE STORE" text (left to right)
        Text(
            text = "SHOE STORE VIP PRO",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 100.sp),
            color = Color.Black,
            modifier = Modifier
                .alpha(0.4f)
                .offset(x = leftToRightX.value.dp, y = 230.dp)
                .align(Alignment.Center)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val coroutineScope = rememberCoroutineScope()

    ShoeStoreTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Animation
            AnimatedBackground()

            // Main Content
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White.copy(alpha = 0.9f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .padding(top = 16.dp, bottom = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Phần đầu (logo và tiêu đề)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Logo container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.shoe_logo),
                                contentDescription = "Shoe Store Logo",
                                modifier = Modifier
                                    .size(100.dp)
                                    .aspectRatio(1f)
                            )
                        }

                        Text(
                            text = "YOUR ACCOUNT FOR\nEVERYTHING SHOESTORE",
                            style = MaterialTheme.typography.displayMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 36.dp)
                        )
                    }

                    // Phần giữa và cuối (form và nút)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email address", style = MaterialTheme.typography.bodyMedium) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password", style = MaterialTheme.typography.bodyMedium) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(
                                    onClick = { passwordVisible = !passwordVisible }
                                ) {
                                    Icon(
                                        imageVector = if (passwordVisible) {
                                            Icons.Filled.Visibility
                                        } else {
                                            Icons.Filled.VisibilityOff
                                        },
                                        contentDescription = if (passwordVisible) {
                                            "Hide password"
                                        } else {
                                            "Show password"
                                        }
                                    )
                                }
                            }
                        )

                        TextButton(
                            onClick = { navController.navigate("ForgotPasswordScreen") },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = "Forgot password?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (email.isBlank() || password.isBlank()) {
                                    Toast.makeText(context, "Vui lòng điền đầy đủ", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                coroutineScope.launch {
                                    try {
                                        val authResult = auth.signInWithEmailAndPassword(email, password).await()
                                        val userId = authResult.user?.uid ?: ""

                                        // Kiểm tra vai trò admin trong Firestore
                                        val userDoc = db.collection("users").document(userId).get().await()
                                        val isAdmin = userDoc.getBoolean("isAdmin") ?: false

                                        if (isAdmin) {
                                            Toast.makeText(context, "Đăng nhập admin thành công!", Toast.LENGTH_LONG).show()
                                            navController.navigate("AdminDashboard") {
                                                popUpTo("LoginScreen") { inclusive = true }
                                            }
                                        } else {
                                            Toast.makeText(context, "Đăng nhập user thành công!", Toast.LENGTH_LONG).show()
                                            navController.navigate("home") {
                                                popUpTo("LoginScreen") { inclusive = true }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Đăng nhập thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            )
                        ) {
                            Text("SIGN IN", style = MaterialTheme.typography.labelLarge)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Not a Member? ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            TextButton(
                                onClick = {
                                    navController.navigate("SignUpScreen")
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "Join Us",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}