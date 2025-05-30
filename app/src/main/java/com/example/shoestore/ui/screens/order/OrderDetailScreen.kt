package com.example.shoestore.ui.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shoestore.ui.theme.ShoeStoreTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.Locale

data class OrderItem(
    val imageUrl: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val size: Int
)

data class OrderDetail(
    val orderId: String = "",
    val status: String = "",
    val address: Map<String, String> = emptyMap(),
    val items: List<OrderItem> = emptyList(),
    val totalPrice: Double = 0.0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(navController: NavController, orderId: String = "") {
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    var order by remember { mutableStateOf<OrderDetail?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Lấy chi tiết đơn hàng từ Firestore
    LaunchedEffect(orderId, user) {
        if (user != null && orderId.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    println("Fetching order with orderId: $orderId for user: ${user.uid}")
                    val snapshot = db.collection("users")
                        .document(user.uid)
                        .collection("orders")
                        .whereEqualTo("orderId", orderId)
                        .get()
                        .await()

                    if (snapshot.isEmpty) {
                        errorMessage = "Không tìm thấy đơn hàng với mã $orderId"
                        println("No order found with orderId: $orderId")
                        return@launch
                    }

                    val doc = snapshot.documents.first()
                    val data = doc.data
                    println("Order data: $data")
                    order = data?.let {
                        OrderDetail(
                            orderId = it["orderId"] as? String ?: "",
                            status = it["status"] as? String ?: "Chưa xác nhận",
                            address = it["address"] as? Map<String, String> ?: emptyMap(),
                            items = (it["items"] as? List<Map<String, Any>>)?.map { item ->
                                OrderItem(
                                    imageUrl = item["imageUrl"] as? String ?: "",
                                    name = item["name"] as? String ?: "",
                                    price = (item["price"] as? Number)?.toDouble() ?: 0.0,
                                    quantity = (item["quantity"] as? Number)?.toInt() ?: 0,
                                    size = (item["size"] as? Number)?.toInt() ?: 0
                                )
                            } ?: emptyList(),
                            totalPrice = (it["totalPrice"] as? Number)?.toDouble() ?: 0.0
                        )
                    }
                } catch (e: Exception) {
                    errorMessage = "Lỗi khi lấy dữ liệu: ${e.message}"
                    println("Error fetching order detail: ${e.message}")
                }
            }
        } else {
            errorMessage = if (user == null) "Vui lòng đăng nhập!" else "Không có mã đơn hàng!"
        }
    }

    ShoeStoreTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFF1C2526))
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Tiêu đề "Đơn hàng #12345" với nút quay lại
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
                        text = "Đơn hàng ${order?.orderId ?: orderId}",
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
                Divider(
                    color = Color(0xFF2196F3),
                    thickness = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Kiểm tra trạng thái dữ liệu
                when {
                    errorMessage != null -> {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    order == null -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = Color.White
                        )
                    }
                    else -> {
                        // Trạng thái đơn hàng
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OrderStatusStep(
                                icon = Icons.Default.CalendarToday,
                                label = "Đã xác nhận",
                                isCompleted = order?.status?.contains("xác nhận", ignoreCase = true) == true
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Divider(
                                    color = if (order?.status?.contains("xác nhận", ignoreCase = true) == true) Color(0xFF4CAF50) else Color.Gray,
                                    thickness = 2.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.Center)
                                )
                            }

                            OrderStatusStep(
                                icon = Icons.Default.LocalShipping,
                                label = "Đang giao",
                                isCompleted = order?.status?.contains("giao", ignoreCase = true) == true
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Divider(
                                    color = if (order?.status?.contains("giao", ignoreCase = true) == true) Color(0xFF4CAF50) else Color.Gray,
                                    thickness = 2.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.Center)
                                )
                            }

                            OrderStatusStep(
                                icon = Icons.Default.Checklist,
                                label = "Chờ xác nhận",
                                isCompleted = order?.status?.contains("chờ", ignoreCase = true) == true
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Divider(
                                    color = if (order?.status?.contains("chờ", ignoreCase = true) == true) Color(0xFF4CAF50) else Color.Gray,
                                    thickness = 2.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.Center)
                                )
                            }

                            OrderStatusStep(
                                icon = Icons.Default.Star,
                                label = "Đánh giá",
                                isCompleted = order?.status?.contains("đánh giá", ignoreCase = true) == true
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Phần giữa: Địa chỉ nhận hàng, Sản phẩm, Chi tiết thanh toán
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            item {
                                // Địa chỉ nhận hàng
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF2E3B3C))
                                        .padding(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Location",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Địa chỉ nhận hàng",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${order?.address?.get("name") ?: ""} - ${order?.address?.get("phone") ?: ""}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = order?.address?.get("details") ?: "",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))

                                // Sản phẩm
                                order?.items?.forEach { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF2E3B3C))
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = item.imageUrl,
                                            contentDescription = item.name,
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFF6F6F6))
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = item.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(item.price)} đ",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontSize = 14.sp,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "Size: ${item.size}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        Text(
                                            text = "x${item.quantity}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))

                                // Chi tiết thanh toán
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF2E3B3C))
                                        .padding(16.dp)
                                ) {
                                    // Tổng tiền hàng
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Tổng tiền hàng",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(order?.totalPrice ?: 0.0)} đ",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Phí vận chuyển (giả định miễn phí)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Phí vận chuyển",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "0 đ",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Voucher giảm giá
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Voucher giảm giá",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "0 đ",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Thành tiền
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Thành tiền: ",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 16.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(order?.totalPrice ?: 0.0)} đ",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 16.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Nút "Đã nhận hàng" và "Đánh giá"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = { /* Navigate to review screen */ },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .alpha(if (order?.status?.contains("giao", ignoreCase = true) == true) 1f else 0.5f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(width = 2.dp, color = Color.Red),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Red
                                ),
                                enabled = order?.status?.contains("giao", ignoreCase = true) == true
                            ) {
                                Text("Đã nhận hàng", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = { navController.navigate("ProductReviewScreen") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .alpha(if (order?.status?.contains("giao", ignoreCase = true) == true) 1f else 0.5f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(width = 2.dp, color = Color.White),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                enabled = order?.status?.contains("giao", ignoreCase = true) == true
                            ) {
                                Text("Đánh giá", fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusStep(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isCompleted: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = if (isCompleted) Color.Green else Color.Gray,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isCompleted) Color.Green else Color.Gray,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp,
            color = if (isCompleted) Color.Green else Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}