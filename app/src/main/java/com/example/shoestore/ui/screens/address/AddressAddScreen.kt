package com.example.shoestore.ui.address

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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

data class AddressAdd(
    val fullName: String,
    val phoneNumber: String,
    val specificAddress: String,
    val street: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressAddScreen(navController: NavController) {
    var addressDetail by remember {
        mutableStateOf(
            AddressAdd(
                fullName = "Đăng Văn Rin",
                phoneNumber = "0898554688",
                specificAddress = "Apt 4B",
                street = "New York"
            )
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ShoeStoreTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
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
                        text = "Thêm địa chỉ mới",
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
                        onValueChange = { newValue ->
                            addressDetail = addressDetail.copy(fullName = newValue)
                        },
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
                        onValueChange = { newValue ->
                            if (newValue.matches(Regex("^\\d{10,11}$"))) {
                                addressDetail = addressDetail.copy(phoneNumber = newValue)
                            }
                        },
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
                        onValueChange = { newValue ->
                            addressDetail = addressDetail.copy(specificAddress = newValue)
                        },
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
                        onValueChange = { newValue ->
                            addressDetail = addressDetail.copy(street = newValue)
                        },
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
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            if (addressDetail.fullName.isNotEmpty() && addressDetail.phoneNumber.isNotEmpty() &&
                                addressDetail.specificAddress.isNotEmpty() && addressDetail.street.isNotEmpty()) {
                                coroutineScope.launch {
                                    try {
                                        val db = FirebaseFirestore.getInstance()
                                        val addressData = hashMapOf(
                                            "fullName" to addressDetail.fullName,
                                            "phoneNumber" to addressDetail.phoneNumber,
                                            "specificAddress" to addressDetail.specificAddress,
                                            "street" to addressDetail.street,
                                            "timestamp" to System.currentTimeMillis()
                                        )
                                        db.collection("users")
                                            .document(user.uid)
                                            .collection("addresses")
                                            .add(addressData)
                                            .await()
                                        snackbarHostState.showSnackbar("Đã thêm địa chỉ thành công!")
                                        navController.popBackStack()
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Lỗi khi thêm địa chỉ: ${e.message}")
                                    }
                                }
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Vui lòng điền đầy đủ thông tin!")
                                }
                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Vui lòng đăng nhập để thêm địa chỉ!")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Thêm địa chỉ mới",
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}