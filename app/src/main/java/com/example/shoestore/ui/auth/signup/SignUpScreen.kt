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
    val rightToLeftOffset = rememberInfiniteTransition()
    val rightToLeftX = rightToLeftOffset.animateFloat(
        initialValue = 1000f,
        targetValue = -1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val leftToRightOffset = rememberInfiniteTransition()
    val leftToRightX = leftToRightOffset.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "SHOE STORE   VIP STORE",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 100.sp),
            color = Color.Black,
            modifier = Modifier
                .alpha(0.4f)
                .offset(x = rightToLeftX.value.dp, y = (-230).dp)
                .align(Alignment.Center)
        )
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
    var selectedDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var isEmailSent by remember { mutableStateOf(false) }
    var isEmailVerified by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = null,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

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
            DatePicker(state = datePickerState, showModeToggle = false)
        }
    }

    // Hàm kiểm tra trạng thái xác thực email
    fun checkEmailVerification() {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        user.reload().addOnCompleteListener { reloadTask ->
                            if (reloadTask.isSuccessful) {
                                isEmailVerified = user.isEmailVerified
                                if (isEmailVerified) {
                                    Toast.makeText(
                                        context,
                                        "Email đã được xác thực! Bạn có thể đăng ký.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Email chưa được xác thực. Vui lòng kiểm tra email và bấm vào liên kết.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Không thể kiểm tra trạng thái: ${reloadTask.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Không tìm thấy thông tin người dùng",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Đăng nhập thất bại: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    ShoeStoreTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedBackgroundSignUp()
            Surface(modifier = Modifier.fillMaxSize(), color = Color.White.copy(alpha = 0.9f)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("First Name", style = MaterialTheme.typography.bodyMedium) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("Last Name", style = MaterialTheme.typography.bodyMedium) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
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
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                    )
                                }
                            }
                        )
                        OutlinedTextField(
                            value = selectedDate,
                            onValueChange = { /* Readonly */ },
                            label = { Text("Date of Birth", style = MaterialTheme.typography.bodyMedium) },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Select date")
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

                                // Tạo tài khoản để gửi email xác thực
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val user = task.result?.user
                                            if (user != null) {
                                                user.sendEmailVerification()
                                                    .addOnCompleteListener { verifyTask ->
                                                        if (verifyTask.isSuccessful) {
                                                            Toast.makeText(
                                                                context,
                                                                "Email xác thực đã được gửi đến $email. Vui lòng bấm vào liên kết để xác nhận.",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            isEmailSent = true
                                                            // Đăng xuất để yêu cầu người dùng đăng nhập lại
                                                            auth.signOut()
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                "Không thể gửi email xác thực: ${verifyTask.exception?.message}",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Không tìm thấy thông tin người dùng sau khi tạo tài khoản",
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
                                                    "Email không tồn tại hoặc không hợp lệ: ${task.exception?.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            enabled = !isEmailSent || !isEmailVerified
                        ) {
                            Text("GỬI EMAIL XÁC THỰC", style = MaterialTheme.typography.labelLarge)
                        }

                        if (isEmailSent && !isEmailVerified) {
                            Button(
                                onClick = { checkEmailVerification() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                            ) {
                                Text("KIỂM TRA LẠI", style = MaterialTheme.typography.labelLarge)
                            }
                        }

                        Button(
                            onClick = {
                                if (firstName.isBlank() || lastName.isBlank() || email.isBlank() ||
                                    password.isBlank() || selectedDate.isBlank()
                                ) {
                                    Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (!isEmailVerified) {
                                    Toast.makeText(context, "Vui lòng xác thực email trước khi đăng ký", Toast.LENGTH_LONG).show()
                                    return@Button
                                }

                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val user = task.result?.user
                                            if (user != null && user.isEmailVerified) {
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
                                                    "created_at" to createdAt,
                                                    "role" to "user",
                                                    "profile_image" to ""
                                                )

                                                db.collection("users")
                                                    .document(userId)
                                                    .set(userMap)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(
                                                            context,
                                                            "Đăng ký thành công! Chào mừng $firstName $lastName",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                        firstName = ""
                                                        lastName = ""
                                                        email = ""
                                                        password = ""
                                                        selectedDate = ""
                                                        isEmailSent = false
                                                        isEmailVerified = false
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
                                                    "Email chưa được xác thực hoặc không tìm thấy người dùng",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Đăng nhập thất bại: ${task.exception?.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            enabled = isEmailVerified
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