package com.example.shoestore.ui.reviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoestore.R
import com.example.shoestore.ui.theme.ShoeStoreTheme

data class Review(
    val email: String,
    val dateTime: String,
    val rating: Int,
    val product: String,
    val size: Int,
    val productImageResId: Int, // Hình ảnh sản phẩm
    val reviewImageResId: Int? = null // Hình ảnh đánh giá (có thể null nếu không có)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReviewsScreen(navController: NavController) {
    // Danh sách đánh giá
    val reviews = listOf(
        Review(
            email = "van.rin@email.com",
            dateTime = "23-04-2025 13:30",
            rating = 5,
            product = "Nike Pegasus 41",
            size = 40,
            productImageResId = R.drawable.s1,
            reviewImageResId = R.drawable.s1 // Hình ảnh đánh giá (có thể để null)
        ),
        Review(
            email = "van.rin@email.com",
            dateTime = "23-04-2025 13:30",
            rating = 5,
            product = "Nike Pegasus 41",
            size = 41,
            productImageResId = R.drawable.s2,
            reviewImageResId = R.drawable.s2
        ),
        Review(
            email = "van.rin@email.com",
            dateTime = "23-04-2025 13:30",
            rating = 5,
            product = "Nike Pegasus 41",
            size = 41,
            productImageResId = R.drawable.s3,
            reviewImageResId = null // Không có hình ảnh đánh giá
        )
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

                // Tiêu đề "ĐÁNH GIÁ CỦA TÔI" với nút quay lại
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
                        text = "Đánh giá của tôi",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(48.dp)) // Để cân đối với IconButton
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Danh sách đánh giá
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(reviews) { review ->
                        ReviewItemRow(review = review, navController = navController)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nút "Thêm đánh giá mới"
//                Button(
//                    onClick = { navController.navigate("productReviewScreen") },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(50.dp),
//                    shape = RoundedCornerShape(8.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFF2196F3),
//                        contentColor = Color.White
//                    )
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Add,
//                            contentDescription = "Add Review",
//                            tint = Color.White,
//                            modifier = Modifier.size(24.dp)
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(
//                            text = "Thêm đánh giá mới",
//                            fontSize = 16.sp
//                        )
//                    }
//                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ReviewItemRow(review: Review, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2E3B3C))
            .padding(16.dp)
    ) {
        // Row 1: Avatar, email, sao, ngày giờ, nút "Sửa"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top // Căn trên cùng theo chiều dọc
        ) {
            Image(
                painter = painterResource(id = R.drawable.sa12_5),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = review.email,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Row {
                    repeat(review.rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = Color(0xFFFFD700), // Màu vàng
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = review.dateTime,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = { navController.navigate("ReviewsEditScreen") },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Sửa",
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Row 2: Hình ảnh sản phẩm và tên sản phẩm
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = review.productImageResId),
                contentDescription = review.product,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF6F6F6))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Phẩm loại: ${review.product} - Size ${review.size}",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Row 3: Hình ảnh liên quan đến đánh giá
        review.reviewImageResId?.let { imageResId ->
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Hình ảnh đánh giá",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF6F6F6))
            )
        }
    }
}