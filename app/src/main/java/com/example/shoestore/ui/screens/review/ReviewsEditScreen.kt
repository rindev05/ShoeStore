package com.example.shoestore.ui.reviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoestore.R
import com.example.shoestore.ui.theme.ShoeStoreTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsEditScreen(navController: NavController) {
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }

    // Danh sách đánh giá
//    val reviews = listOf(
//        Review(
//            email = "van.rin@email.com",
//            dateTime = "23-04-2025 13:30",
//            rating = 5,
//            product = "Nike Pegasus 41",
//            size = 40,
//            productImageResId = R.drawable.s1,
//            reviewImageResId = R.drawable.s1 // Hình ảnh đánh giá (có thể để null)
//        ),
//        Review(
//            email = "van.rin@email.com",
//            dateTime = "23-04-2025 13:30",
//            rating = 5,
//            product = "Nike Pegasus 41",
//            size = 41,
//            productImageResId = R.drawable.s2,
//            reviewImageResId = R.drawable.s2
//        ),
//        Review(
//            email = "van.rin@email.com",
//            dateTime = "23-04-2025 13:30",
//            rating = 5,
//            product = "Nike Pegasus 41",
//            size = 41,
//            productImageResId = R.drawable.s3,
//            reviewImageResId = null // Không có hình ảnh đánh giá
//        )
//    )

    val reviews = Review(
            email = "van.rin@email.com",
            dateTime = "23-04-2025 13:30",
            rating = 5,
            product = "Nike Pegasus 41",
            size = 40,
            productImageResId = R.drawable.s1,
            reviewImageResId = R.drawable.s1 // Hình ảnh đánh giá (có thể để null)
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

                // Tiêu đề "Đánh giá sản phẩm" với nút quay lại
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
                        text = "Chỉnh sửa đánh giá",
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

                // Thông tin sản phẩm
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .background(Color(0xFF2E3B3C), RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Placeholder cho hình ảnh sản phẩm
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Image(
//                            painter = painterResource(id = R.drawable.s1),
//                            contentDescription = Review.product,
//                            modifier = Modifier
//                                .size(60.dp)
//                                .clip(RoundedCornerShape(8.dp))
//                                .background(Color(0xFFF6F6F6))
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(
//                            text = "Phẩm loại: ${reviews.product} - Size ${reviews.size}",
//                            style = MaterialTheme.typography.bodyMedium,
//                            fontSize = 14.sp,
//                            color = Color.White
//                        )
//                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = reviews.productImageResId),
                            contentDescription = reviews.product,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF6F6F6))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Phẩm loại: ${reviews.product} - Size ${reviews.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Đánh giá sao
                Column {
                    Text(
                        text = "Đánh giá của bạn",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        repeat(5) { index ->
                            IconButton(
                                onClick = { rating = index + 1 },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Star ${index + 1}",
                                    tint = if (index < rating) Color(0xFFFFD700) else Color.Gray,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ô nhập nhận xét
                Column {
                    Text(
                        text = "Nhận xét",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    BasicTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .background(Color(0xFF2E3B3C), RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                if (reviewText.isEmpty()) {
                                    Text(
                                        text = "Viết nhận xét của bạn...",
                                        color = Color.Gray,
                                        fontSize = 16.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nút "Gửi đánh giá"
                Button(
                    onClick = { /* Handle submit review action */ },
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
                            contentDescription = "Submit",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lưu đánh giá",
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}