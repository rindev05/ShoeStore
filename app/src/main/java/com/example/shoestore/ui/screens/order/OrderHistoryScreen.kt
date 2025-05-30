package com.example.shoestore.ui.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoestore.ui.theme.ShoeStoreTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.Locale

// Cập nhật data class Order để chứa thông tin cần thiết
data class Order(
    val orderId: String = "",
    val status: String = "",
    val totalPrice: Double = 0.0,
    val orderDate: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Lấy danh sách đơn hàng từ Firestore
    LaunchedEffect(user) {
        if (user != null) {
            coroutineScope.launch {
                try {
                    val snapshot = db.collection("users")
                        .document(user.uid)
                        .collection("orders")
                        .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .get()
                        .await()
                    orders = snapshot.documents.mapNotNull { doc ->
                        val data = doc.data
                        data?.let {
                            Order(
                                orderId = it["orderId"] as? String ?: "",
                                status = it["status"] as? String ?: "Chưa xác nhận",
                                totalPrice = (it["totalPrice"] as? Number)?.toDouble() ?: 0.0,
                                orderDate = it["orderDate"] as? String ?: ""
                            )
                        }
                    }
                } catch (e: Exception) {
                    println("Error fetching orders: ${e.message}")
                }
            }
        }
    }

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
                            // Tiêu đề "LỊCH SỬ ĐẶT HÀNG" với nút quay lại
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
                                    text = "Lịch sử đặt hàng",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.size(48.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1C2526)
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFF1C2526))
                    .padding(horizontal = 16.dp)
            ) {
                Divider(
                    color = Color(0xFF2196F3),
                    thickness = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Danh sách đơn hàng
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(orders) { order ->
                        OrderItemRow(order = order, navController = navController)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemRow(order: Order, navController: NavController) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN")).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF2E3B3C))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Đơn hàng ${order.orderId}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = order.status,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = Color.Red
            )
            Text(
                text = "${numberFormat.format(order.totalPrice)} đ",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = Color.White
            )
            Text(
                text = order.orderDate,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        OutlinedButton(
            onClick = { navController.navigate("OrderDetailScreen?orderId=${order.orderId}") },
            shape = RoundedCornerShape(8.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Xem chi tiết",
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}