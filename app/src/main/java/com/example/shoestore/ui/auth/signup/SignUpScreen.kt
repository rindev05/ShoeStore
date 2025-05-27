package com.example.shoestore.ui.auth.signup

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
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
import com.example.shoestore.R
import com.example.shoestore.ui.theme.ShoeStoreTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AnimatedBackgroundSignUp() {
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
        // First text (right to left)
        Text(
            text = "SHOE STORE   VIP STORE",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 100.sp),
            color = Color.Black,
            modifier = Modifier
                .alpha(0.4f)
                .offset(x = rightToLeftX.value.dp, y = (-230).dp)
                .align(Alignment.Center)
        )

        // Second text (left to right)
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
fun SignUpScreen(navController: NavController) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Date Picker state
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }

    // Tạo DatePicker state với ngày tối đa là hiện tại
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = null,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    // Hiển thị DatePicker Dialog khi showDatePicker = true
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dateMillis ->
                        val instant = Instant.ofEpochMilli(dateMillis)
                        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        selectedDate = localDate.format(formatter)
                    }
                    showDatePicker = false
                }) {
                    Text("OK", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", style = MaterialTheme.typography.labelLarge)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }

    ShoeStoreTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Animation
            AnimatedBackgroundSignUp()

            // Main Content
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White.copy(alpha = 0.9f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Logo container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.shoe_logo),
                            contentDescription = "Shoe Store Logo",
                            modifier = Modifier
                                .size(80.dp)
                                .aspectRatio(1f)
                        )
                    }

                    Text(
                        text = "BECOME A SHOESTORE MEMBER",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "Create your ShoeStore Member profile and get first access to the very best of ShoeStore products, inspiration and community.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Form Fields
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // First Name
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("First Name", style = MaterialTheme.typography.bodyMedium) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Last Name
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("Last Name", style = MaterialTheme.typography.bodyMedium) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Email
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email address", style = MaterialTheme.typography.bodyMedium) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        // Password
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
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
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

                        // Date of Birth
                        OutlinedTextField(
                            value = selectedDate,
                            onValueChange = { /* Readonly */ },
                            label = { Text("Date of Birth", style = MaterialTheme.typography.bodyMedium) },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Select date"
                                    )
                                }
                            }
                        )

                        Text(
                            text = "By creating an account, you agree to ShoeStore's Privacy Policy and Terms of Use.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Button(
                            onClick = {
                                if (firstName.isBlank() || lastName.isBlank() || email.isBlank() ||
                                    password.isBlank() || selectedDate.isBlank()
                                ) {
                                    Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.(com|net|org)$"
                                if (!email.matches(Regex(emailPattern))) {
                                    Toast.makeText(context, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val user = task.result?.user
                                            if (user != null) {
                                                val userId = user.uid
                                                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                                                    .withZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                                                val createdAt = Instant.now().atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                                                    .format(formatter)

                                                val userMap = hashMapOf(
                                                    "date_birth" to selectedDate,
                                                    "email" to email,
                                                    "first_name" to firstName,
                                                    "last_name" to lastName,
                                                    "created_at" to createdAt, // Thêm thời gian tạo tài khoản
                                                    "role" to "user", // Vai trò mặc định
                                                    "profile_image" to "" // Ảnh đại diện mặc định (rỗng)
                                                )

                                                FirebaseFirestore.getInstance()
                                                    .collection("users")
                                                    .document(userId)
                                                    .set(userMap)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(
                                                            context,
                                                            "Đăng ký thành công! Chào mừng $firstName $lastName",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                        // Xóa dữ liệu form
                                                        firstName = ""
                                                        lastName = ""
                                                        email = ""
                                                        password = ""
                                                        selectedDate = ""
                                                        // Trì hoãn điều hướng
                                                        Handler(Looper.getMainLooper()).postDelayed({
                                                            navController.navigate("home") {
                                                                popUpTo("SignUpScreen") { inclusive = true }
                                                            }
                                                        }, 1000)
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Toast.makeText(
                                                            context,
                                                            "Lưu dữ liệu thất bại: ${e.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Không tìm thấy thông tin người dùng sau khi đăng ký",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            if (task.exception?.message?.contains("email address is already in use") == true) {
                                                Toast.makeText(
                                                    context,
                                                    "Email đã được sử dụng, vui lòng dùng email khác",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Đăng ký thất bại: ${task.exception?.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
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
                            Text("JOIN US", style = MaterialTheme.typography.labelLarge)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Already a Member? ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            TextButton(
                                onClick = { navController.navigate("LoginScreen") },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "Sign In",
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