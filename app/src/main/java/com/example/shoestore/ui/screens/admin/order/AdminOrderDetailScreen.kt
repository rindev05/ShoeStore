package com.example.shoestore.ui.screens.admin.order

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shoestore.ui.theme.ShoeStoreTheme
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
    val totalPrice: Double = 0.0,
    val isConfirm: Boolean = false,
    val isDelivery: Boolean = false,
    val isReceipt: Boolean = false,
    val isRate: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderDetailScreen(
    navController: NavController,
    orderId: String = "",
    userId: String = ""
) {
    val db = FirebaseFirestore.getInstance()
    var order by remember { mutableStateOf<OrderDetail?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Lấy chi tiết đơn hàng từ Firestore và lưu document ID
    var documentId by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(orderId, userId) {
        if (userId.isNotEmpty() && orderId.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    println("Fetching order with orderId: $orderId for user: $userId")
                    val snapshot = db.collection("users")
                        .document(userId)
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
                    documentId = doc.id // Lưu document ID
                    val data = doc.data
                    println("Order data: $data, Document ID: $documentId")
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
                            totalPrice = (it["totalPrice"] as? Number)?.toDouble() ?: 0.0,
                            isConfirm = it["isConfirm"] as? Boolean ?: false,
                            isDelivery = it["isDelivery"] as? Boolean ?: false,
                            isReceipt = it["isReceipt"] as? Boolean ?: false,
                            isRate = it["isRate"] as? Boolean ?: false
                        )
                    }
                } catch (e: Exception) {
                    errorMessage = "Lỗi khi lấy dữ liệu: ${e.message}"
                    println("Error fetching order detail: ${e.message}")
                }
            }
        } else {
            errorMessage = "Thiếu thông tin userId hoặc orderId!"
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
                        textAlign = TextAlign.Center
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
                                label = if (order?.isConfirm == true) "Đã xác nhận" else "Chưa xác nhận",
                                isCompleted = order?.isConfirm == true
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Divider(
                                    color = if (order?.isConfirm == true) Color(0xFF4CAF50) else Color.Gray,
                                    thickness = 2.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.Center)
                                )
                            }

                            OrderStatusStep(
                                icon = Icons.Default.LocalShipping,
                                label = if (order?.isDelivery == true) "Đang giao" else "Đang giao",
                                isCompleted = order?.isDelivery == true
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Divider(
                                    color = if (order?.isDelivery == true) Color(0xFF4CAF50) else Color.Gray,
                                    thickness = 2.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.Center)
                                )
                            }

                            OrderStatusStep(
                                icon = Icons.Default.Checklist,
                                label = if (order?.isReceipt == true) "Chờ xác nhận" else "Chờ xác nhận",
                                isCompleted = order?.isReceipt == true
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Divider(
                                    color = if (order?.isReceipt == true) Color(0xFF4CAF50) else Color.Gray,
                                    thickness = 2.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.Center)
                                )
                            }

                            OrderStatusStep(
                                icon = Icons.Default.Star,
                                label = if (order?.isRate == true) "Đánh giá" else "Đánh giá",
                                isCompleted = order?.isRate == true
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

                        // Nút "Xác nhận" và "Đã giao"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (order?.status != "Đã xác nhận" && order?.status != "Đã giao") {
                                Button(
                                    onClick = {
                                        if (documentId != null) {
                                            coroutineScope.launch {
                                                try {
                                                    db.collection("users")
                                                        .document(userId)
                                                        .collection("orders")
                                                        .document(documentId!!)
                                                        .update(
                                                            mapOf(
                                                                "isConfirm" to true,
                                                                "isDelivery" to true,
                                                                "status" to "Đã xác nhận"
                                                            )
                                                        )
                                                        .await()
                                                    println("Order confirmed successfully: $documentId")
                                                    order = order?.copy(
                                                        isConfirm = true,
                                                        isDelivery = true,
                                                        status = "Đã xác nhận"
                                                    )
                                                } catch (e: Exception) {
                                                    println("Error confirming order: ${e.message}")
                                                    errorMessage = "Lỗi khi xác nhận đơn hàng: ${e.message}"
                                                }
                                            }
                                        } else {
                                            println("Document ID is null, cannot confirm order")
                                            errorMessage = "Không tìm thấy ID đơn hàng để xác nhận!"
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2196F3)
                                    )
                                ) {
                                    Text("Xác nhận", fontSize = 14.sp, color = Color.White)
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            if (order?.status == "Đã xác nhận") {
                                Button(
                                    onClick = {
                                        if (documentId != null) {
                                            coroutineScope.launch {
                                                try {
                                                    db.collection("users")
                                                        .document(userId)
                                                        .collection("orders")
                                                        .document(documentId!!)
                                                        .update(
                                                            mapOf(
                                                                "isReceipt" to true,
                                                                "status" to "Đã giao"
                                                            )
                                                        )
                                                        .await()
                                                    println("Order marked as delivered: $documentId")
                                                    order = order?.copy(
                                                        isReceipt = true,
                                                        status = "Đã giao"
                                                    )
                                                } catch (e: Exception) {
                                                    println("Error marking order as delivered: ${e.message}")
                                                    errorMessage = "Lỗi khi đánh dấu đơn hàng đã giao: ${e.message}"
                                                }
                                            }
                                        } else {
                                            println("Document ID is null, cannot mark as delivered")
                                            errorMessage = "Không tìm thấy ID đơn hàng để cập nhật!"
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Green
                                    )
                                ) {
                                    Text("Đã giao", fontSize = 14.sp, color = Color.White)
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
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
fun OrderStatusStep(icon: ImageVector, label: String, isCompleted: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = if (isCompleted) Color(0xFF4CAF50) else Color.Gray,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isCompleted) Color(0xFF4CAF50) else Color.Gray,
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
            color = if (isCompleted) Color(0xFF4CAF50) else Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}