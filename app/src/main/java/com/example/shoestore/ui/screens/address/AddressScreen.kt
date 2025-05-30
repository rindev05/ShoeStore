package com.example.shoestore.ui.address

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoestore.ui.theme.ShoeStoreTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Address(
    val name: String,
    val phone: String,
    val details: String,
    val isDefault: Boolean = false,
    val id: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var addresses by remember { mutableStateOf<List<Address>>(emptyList()) }
    var listenerRegistration by remember { mutableStateOf<ListenerRegistration?>(null) }

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            try {
                listenerRegistration?.remove()
                listenerRegistration = db.collection("users")
                    .document(user.uid)
                    .collection("addresses")
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Lỗi khi theo dõi địa chỉ: ${e.message}")
                            }
                            return@addSnapshotListener
                        }
                        snapshot?.let {
                            addresses = it.documents.map { doc ->
                                Address(
                                    id = doc.id,
                                    name = doc.getString("fullName") ?: "",
                                    phone = doc.getString("phoneNumber") ?: "",
                                    details = "${doc.getString("specificAddress") ?: ""}, ${doc.getString("street") ?: ""}",
                                    isDefault = doc.getBoolean("isDefault") ?: false
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Lỗi khi tải địa chỉ: ${e.message}")
                }
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Vui lòng đăng nhập để xem địa chỉ!")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            listenerRegistration?.remove()
        }
    }

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
                        text = "Địa chỉ nhận hàng",
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

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(addresses) { address ->
                        AddressItemRow(
                            address = address,
                            navController = navController,
                            coroutineScope = coroutineScope,
                            snackbarHostState = snackbarHostState
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("AddressAddScreen") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
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

@Composable
fun AddressItemRow(
    address: Address,
    navController: NavController,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2E3B3C))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Set as Default",
            tint = if (address.isDefault) Color.Red else Color.Gray,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    if (user != null) {
                        coroutineScope.launch {
                            try {
                                db.collection("users")
                                    .document(user.uid)
                                    .collection("addresses")
                                    .get()
                                    .await()
                                    .documents
                                    .forEach { doc ->
                                        db.collection("users")
                                            .document(user.uid)
                                            .collection("addresses")
                                            .document(doc.id)
                                            .update("isDefault", doc.id == address.id)
                                            .await()
                                    }
                                snackbarHostState.showSnackbar("Đã đặt địa chỉ mặc định thành công!")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Lỗi khi đặt mặc định: ${e.message}")
                            }
                        }
                    }
                }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "${address.name} - ${address.phone}",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = Color.White
            )
            Text(
                text = address.details,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        IconButton(
            onClick = { navController.navigate("AddressDetailScreen/${address.id}") },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Edit Address",
                tint = Color.White
            )
        }
    }
}