package com.example.shoestore.ui.address

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoestore.ui.theme.ShoeStoreTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AddressDetail(
    val id: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val specificAddress: String = "",
    val street: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressDetailScreen(navController: NavController, addressId: String) {
    val db = FirebaseFirestore.getInstance()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val user = FirebaseAuth.getInstance().currentUser
    var addressDetail by remember {
        mutableStateOf(
            AddressDetail(
                id = addressId
            )
        )
    }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Lấy dữ liệu từ Firestore khi khởi tạo
    LaunchedEffect(addressId) {
        if (addressId.isNotEmpty() && user != null) {
            try {
                val doc = db.collection("users")
                    .document(user.uid)
                    .collection("addresses")
                    .document(addressId)
                    .get()
                    .await()
                if (doc.exists()) {
                    val data = doc.data
                    addressDetail = AddressDetail(
                        id = addressId,
                        fullName = data?.get("fullName")?.toString() ?: "",
                        phoneNumber = data?.get("phoneNumber")?.toString() ?: "",
                        specificAddress = data?.get("specificAddress")?.toString() ?: "",
                        street = data?.get("street")?.toString() ?: ""
                    )
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Không tìm thấy địa chỉ!")
                        navController.popBackStack()
                    }
                }
            } catch (e: Exception) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Lỗi khi tải địa chỉ: ${e.message}")
                    navController.popBackStack()
                }
            } finally {
                isLoading = false
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Vui lòng đăng nhập!")
                navController.popBackStack()
            }
            isLoading = false
        }
    }

    ShoeStoreTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1C2526)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color(0xFF1C2526))
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Chỉnh sửa địa chỉ",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column {
                        Text(
                            text = "Họ và tên",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        BasicTextField(
                            value = addressDetail.fullName,
                            onValueChange = { addressDetail = addressDetail.copy(fullName = it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                .background(Color(0xFF2E3B3C), RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp
                            ),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    innerTextField()
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column {
                        Text(
                            text = "Số điện thoại",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        BasicTextField(
                            value = addressDetail.phoneNumber,
                            onValueChange = { addressDetail = addressDetail.copy(phoneNumber = it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                .background(Color(0xFF2E3B3C), RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp
                            ),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    innerTextField()
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column {
                        Text(
                            text = "Tỉnh/Thành phố, Quận/Huyện, Phường/Xã",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        BasicTextField(
                            value = addressDetail.specificAddress,
                            onValueChange = { addressDetail = addressDetail.copy(specificAddress = it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                .background(Color(0xFF2E3B3C), RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp
                            ),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    innerTextField()
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column {
                        Text(
                            text = "Tên đường, Tòa nhà, Số nhà",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        BasicTextField(
                            value = addressDetail.street,
                            onValueChange = { addressDetail = addressDetail.copy(street = it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                .background(Color(0xFF2E3B3C), RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp
                            ),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    innerTextField()
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (user != null) {
                                coroutineScope.launch {
                                    try {
                                        db.collection("users")
                                            .document(user.uid)
                                            .collection("addresses")
                                            .document(addressId)
                                            .set(addressDetail)
                                            .await()
                                        snackbarHostState.showSnackbar("Đã cập nhật địa chỉ thành công!")
                                        navController.popBackStack()
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Lỗi khi lưu địa chỉ: ${e.message}")
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3),
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Lưu địa chỉ",
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFDE00),
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Xóa địa chỉ",
                                fontSize = 16.sp
                            )
                        }
                    }

                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text("Xác nhận xóa") },
                            text = { Text("Bạn có chắc muốn xóa địa chỉ này?") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        if (user != null) {
                                            coroutineScope.launch {
                                                try {
                                                    db.collection("users")
                                                        .document(user.uid)
                                                        .collection("addresses")
                                                        .document(addressId)
                                                        .delete()
                                                        .await()
                                                    snackbarHostState.showSnackbar("Đã xóa địa chỉ thành công!")
                                                    navController.popBackStack()
                                                } catch (e: Exception) {
                                                    snackbarHostState.showSnackbar("Lỗi khi xóa địa chỉ: ${e.message}")
                                                }
                                            }
                                        }
                                        showDeleteDialog = false
                                    }
                                ) {
                                    Text("Xóa")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteDialog = false }) {
                                    Text("Hủy")
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}