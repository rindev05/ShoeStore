package com.example.shoestore.ui.orderhistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoestore.R
import com.example.shoestore.ui.theme.ShoeStoreTheme
import java.text.NumberFormat
import java.util.Locale

// Cập nhật data class Order để chứa đầy đủ thông tin chi tiết
data class Order(
    val orderId: String,
    val status: String,
    val address: String,
    val name: String,
    val phone: String,
    val products: List<ProductItem>,
    val totalPrice: Double,
    val shippingFee: Double,
    val discount: Double
)

data class ProductItem(
    val productName: String,
    val productPrice: Double,
    val quantity: Int,
    val imageResId: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(navController: NavController) {
    // Danh sách đơn hàng với đầy đủ thông tin
    val orders = listOf(
        Order(
            orderId = "#12345",
            status = "Chưa xác nhận",
            address = "Trường Đại học Việt Hàn, Đường Trần Đại Nghĩa, Phường Hòa Quý, Quận Ngũ Hành Sơn, Đà Nẵng",
            name = "Đăng Văn Rin",
            phone = "0989554689",
            products = listOf(
                ProductItem(
                    productName = "Nike Air Zoom Pegasus",
                    productPrice = 2990000.0,
                    quantity = 1,
                    imageResId = R.drawable.s1
                )
            ),
            totalPrice = 2990000.0,
            shippingFee = 0.0,
            discount = 0.0
        ),
        Order(
            orderId = "#67890",
            status = "Đang giao",
            address = "123 Đường Lê Lợi, Quận 1, TP. Hồ Chí Minh",
            name = "Nguyễn Văn A",
            phone = "0912345678",
            products = listOf(
                ProductItem(
                    productName = "Nike Air Max 90",
                    productPrice = 3500000.0,
                    quantity = 1,
                    imageResId = R.drawable.s1
                ),
                ProductItem(
                    productName = "Nike React Infinity",
                    productPrice = 1490000.0,
                    quantity = 1,
                    imageResId = R.drawable.s1
                )
            ),
            totalPrice = 4990000.0,
            shippingFee = 0.0,
            discount = 0.0
        ),
        Order(
            orderId = "#11223",
            status = "Đã giao hàng",
            address = "456 Đường Nguyễn Huệ, Quận 3, TP. Hồ Chí Minh",
            name = "Trần Thị B",
            phone = "0934567890",
            products = listOf(
                ProductItem(
                    productName = "Nike Air Force 1",
                    productPrice = 2500000.0,
                    quantity = 2,
                    imageResId = R.drawable.s1
                ),
                ProductItem(
                    productName = "Nike SB Dunk",
                    productPrice = 2990000.0,
                    quantity = 1,
                    imageResId = R.drawable.s1
                )
            ),
            totalPrice = 7990000.0,
            shippingFee = 0.0,
            discount = 0.0
        )
    )

    ShoeStoreTheme {
        Scaffold(
            // bottomBar = { com.example.shoestore.ui.profile.BottomNavigationBar(navController = navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFF1C2526))
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

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
        }
        OutlinedButton(
            onClick = { navController.navigate("OrderDetailScreen") },
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