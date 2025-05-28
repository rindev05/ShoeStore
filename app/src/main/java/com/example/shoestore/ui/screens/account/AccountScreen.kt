package com.example.shoestore.ui.account

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.shoestore.R
import com.example.shoestore.data.service.CloudinaryService
import com.example.shoestore.ui.theme.ShoeStoreTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavController) {
    // States cho các trường thông tin
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var profileImage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    // States cho hiển thị mật khẩu
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    // Context và Firebase
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    val scope = rememberCoroutineScope()

    // Lấy dữ liệu ban đầu từ Firestore
    LaunchedEffect(userId) {
        if (userId != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        firstName = document.getString("first_name") ?: ""
                        lastName = document.getString("last_name") ?: ""
                        email = document.getString("email") ?: ""
                        phoneNumber = document.getString("phone_number") ?: ""
                        profileImage = document.getString("profile_image") ?: ""
                        password = "******"
                        confirmPassword = "******"
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                    Toast.makeText(context, "Lỗi tải dữ liệu: ${it.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            isLoading = false
            navController.navigate("LoginScreen") {
                popUpTo("AccountScreen") { inclusive = true }
            }
        }
    }

    // Launcher để chọn ảnh từ thiết bị
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        if (uri != null) {
            scope.launch {
                try {
                    val imageUrl = CloudinaryService.getInstance(context)
                        .uploadImage(uri, "profile_images")
                    profileImage = imageUrl
                    Toast.makeText(context, "Tải ảnh thành công", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Tải ảnh thất bại: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Scroll state để hỗ trợ trượt dọc
    val scrollState = rememberScrollState()

    ShoeStoreTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Nút Back bên trái
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(start = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            // Văn bản "Tài khoản của tôi" ở giữa
                            Text(
                                text = "Tài khoản của tôi",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            // Spacer để cân bằng
                            Spacer(modifier = Modifier.size(48.dp))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1C2526)
                    )
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = Color(0xFF1C2526),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Button(
                        onClick = {
                            if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || phoneNumber.isBlank()) {
                                Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.(com|net|org)$"
                            if (!email.matches(Regex(emailPattern))) {
                                Toast.makeText(context, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (password != "******" || confirmPassword != "******") {
                                if (password != confirmPassword) {
                                    Toast.makeText(context, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (password.length < 6) {
                                    Toast.makeText(context, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                            }

                            isSaving = true

                            // Cập nhật thông tin lên Firestore
                            val userMap = hashMapOf(
                                "first_name" to firstName,
                                "last_name" to lastName,
                                "email" to email,
                                "phone_number" to phoneNumber,
                                "profile_image" to profileImage,
                                "updated_at" to Instant.now().atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                            )

                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(userId ?: "")
                                .update(userMap as Map<String, Any>)
                                .addOnSuccessListener {
                                    // Cập nhật email trong Firebase Auth
                                    currentUser?.updateEmail(email)?.addOnSuccessListener {
                                        // Cập nhật mật khẩu nếu có
                                        if (password != "******") {
                                            currentUser.updatePassword(password).addOnSuccessListener {
                                                Toast.makeText(context, "Cập nhật thông tin và mật khẩu thành công", Toast.LENGTH_LONG).show()
                                                isSaving = false
                                                navController.popBackStack()
                                            }.addOnFailureListener { e ->
                                                isSaving = false
                                                Toast.makeText(context, "Cập nhật mật khẩu thất bại: ${e.message}", Toast.LENGTH_LONG).show()
                                            }
                                        } else {
                                            Toast.makeText(context, "Cập nhật thông tin thành công", Toast.LENGTH_LONG).show()
                                            isSaving = false
                                            navController.popBackStack()
                                        }
                                    }?.addOnFailureListener { e ->
                                        isSaving = false
                                        Toast.makeText(context, "Cập nhật email thất bại: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    isSaving = false
                                    Toast.makeText(context, "Cập nhật thông tin thất bại: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3),
                            contentColor = Color.White
                        ),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("Lưu thay đổi", fontSize = 16.sp)
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFF1C2526))
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.Start
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        color = Color.White
                    )
                } else {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Ảnh đại diện
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.size(100.dp)
                        ) {
                            Image(
                                painter = if (selectedImageUri != null && profileImage.isNotEmpty()) {
                                    rememberAsyncImagePainter(
                                        model = profileImage,
                                        placeholder = painterResource(id = R.drawable.sa12_5),
                                        error = painterResource(id = R.drawable.sa12_5)
                                    )
                                } else if (profileImage.isNotEmpty()) {
                                    rememberAsyncImagePainter(
                                        model = profileImage,
                                        placeholder = painterResource(id = R.drawable.sa12_5),
                                        error = painterResource(id = R.drawable.sa12_5)
                                    )
                                } else {
                                    painterResource(id = R.drawable.sa12_5)
                                },
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                            )
                            IconButton(
                                onClick = {
                                    pickImageLauncher.launch("image/*")
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2196F3))
                                    .align(Alignment.BottomEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Picture",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Họ
                    Text(
                        text = "Họ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            containerColor = Color(0xFF2E3B3C),
                            focusedTextColor = Color.White
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tên
                    Text(
                        text = "Tên",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            containerColor = Color(0xFF2E3B3C),
                            focusedTextColor = Color.White
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email
                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            containerColor = Color(0xFF2E3B3C),
                            focusedTextColor = Color.White
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Số điện thoại
                    Text(
                        text = "Số điện thoại",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            containerColor = Color(0xFF2E3B3C),
                            focusedTextColor = Color.White
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mật khẩu mới
                    Text(
                        text = "Mật khẩu mới (để trống nếu không muốn thay đổi)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            containerColor = Color(0xFF2E3B3C),
                            focusedTextColor = Color.White
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        singleLine = true,
                        placeholder = { Text("Nhập mật khẩu mới", color = Color.Gray) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                                    tint = Color.White
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Xác nhận lại mật khẩu
                    Text(
                        text = "Xác nhận lại mật khẩu",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            containerColor = Color(0xFF2E3B3C),
                            focusedTextColor = Color.White
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        singleLine = true,
                        placeholder = { Text("Xác nhận lại mật khẩu", color = Color.Gray) },
                        trailingIcon = {
                            IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isConfirmPasswordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                                    tint = Color.White
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}