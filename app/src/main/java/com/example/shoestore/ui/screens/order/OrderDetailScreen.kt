package com.example.shoestore.ui.orderdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoestore.R
import com.example.shoestore.ui.theme.ShoeStoreTheme
import java.text.NumberFormat
import java.util.Locale

data class OrderDetail(
    val orderId: String,
    val status: String,
    val address: String,
    val name: String,
    val phone: String,
    val productName: String,
    val productPrice: Double,
    val quantity: Int,
    val totalPrice: Double,
    val shippingFee: Double,
    val discount: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(navController: NavController) {
    val order = OrderDetail(
        orderId = "#12345",
        status = "Đã xác nhận",
        address = "Trường Đại học Việt Hàn, Đường Trần Đại Nghĩa, Phường Hòa Quý, Quận Ngũ Hành Sơn, Đà Nẵng",
        name = "Đăng Văn Rin",
        phone = "0989554689",
        productName = "Nike Air Zoom Pegasus",
        productPrice = 2990000.0,
        quantity = 1,
        totalPrice = 3990000.0,
        shippingFee = 0.0,
        discount = 0.0
    )

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

                // Tiêu đề "Đơn hàng #12345" với nút quay lại (cố định)
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
                        text = "Đơn hàng ${order.orderId}",
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

                // Trạng thái đơn hàng (cố định)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OrderStatusStep(
                        icon = Icons.Default.CalendarToday,
                        label = "Đã xác nhận",
                        isCompleted = true
                    )

                    // Đường thẳng giữa "Đã xác nhận" và "Đang giao" (màu xanh)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Divider(
                            color = Color(0xFF4CAF50),
                            thickness = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                        )
                    }

                    OrderStatusStep(
                        icon = Icons.Default.LocalShipping,
                        label = "Đang giao",
                        isCompleted = true
                    )

                    // Đường thẳng giữa "Đang giao" và "Chờ xác nhận" (màu xám)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Divider(
                            color = Color.Gray,
                            thickness = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                        )
                    }

                    OrderStatusStep(
                        icon = Icons.Default.Checklist,
                        label = "Chờ xác nhận",
                        isCompleted = true
                    )

                    // Đường thẳng giữa "Chờ xác nhận" và "Đánh giá" (màu xám)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Divider(
                            color = Color.Gray,
                            thickness = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                        )
                    }

                    OrderStatusStep(
                        icon = Icons.Default.Star,
                        label = "Đánh giá",
                        isCompleted = false
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Phần giữa: Địa chỉ nhận hàng, Sản phẩm, Chi tiết thanh toán (cuộn được)
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
                                    text = "${order.name} - ${order.phone}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = order.address,
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2E3B3C))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.s1),
                                contentDescription = order.productName,
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
                                    text = order.productName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(order.productPrice)} đ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "x${order.quantity}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2E3B3C))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.s1),
                                contentDescription = order.productName,
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
                                    text = order.productName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(order.productPrice)} đ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "x${order.quantity}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp,
                                color = Color.White
                            )
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
                                    text = "${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(order.totalPrice)} đ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Phí vận chuyển
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
                                    text = "${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(order.shippingFee)} đ",
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
                                    text = "${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(order.discount)} đ",
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
                                    text = "${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(order.totalPrice + order.shippingFee - order.discount)} đ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nút "Đã nhận hàng" và "Đánh giá" (cố định)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { /* Navigate to review screen */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .alpha(0.5f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(width = 2.dp, color = Color.Red),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text("Đã nhận hàng", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { navController.navigate("ProductReviewScreen") },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .alpha(0.5f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(width = 2.dp, color = Color.White),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("Đánh giá", fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
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
//                .background(Color.Black) // Nền màu đen mặc định
                .border(
                    width = 1.dp,
                    color = if (isCompleted) Color.Green else Color.Gray, // Màu viền thay đổi dựa trên isCompleted
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isCompleted) Color.Green else Color.Gray, // Màu của Icon thay đổi dựa trên isCompleted
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
            color = if (isCompleted) Color.Green else Color.Gray, // Màu của Text thay đổi dựa trên isCompleted
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}