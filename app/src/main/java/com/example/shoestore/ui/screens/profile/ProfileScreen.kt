package com.example.shoestore.ui.profile

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    // States cho dữ liệu người dùng
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var profileImage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Lấy UID của người dùng hiện tại
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    // Lấy dữ liệu từ Firestore
    LaunchedEffect(userId) {
        if (userId != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        firstName = document.getString("first_name") ?: ""
                        lastName = document.getString("last_name") ?: ""
                        email = document.getString("email") ?: ""
                        profileImage = document.getString("profile_image") ?: ""
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        } else {
            isLoading = false
            navController.navigate("LoginScreen") {
                popUpTo("ProfileScreen") { inclusive = true }
            }
        }
    }

    ShoeStoreTheme {
        Scaffold(
            bottomBar = { BottomNavigationBar(navController = navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp),
                        color = Color.Black
                    )
                } else {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Avatar
                    Image(
                        painter = if (profileImage.isNotEmpty()) {
                            painterResource(id = R.drawable.sa12_4) // Thay bằng logic tải ảnh từ URL nếu có
                        } else {
                            painterResource(id = R.drawable.sa12_4)
                        },
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tên và email
                    Text(
                        text = "$firstName $lastName",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Danh sách tùy chọn
                    ProfileOptionItem(
                        icon = Icons.Default.History,
                        text = "Lịch sử đặt hàng",
                        onClick = { navController.navigate("OrderHistoryScreen") }
                    )
                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                    ProfileOptionItem(
                        icon = Icons.Default.Person,
                        text = "Tài khoản của tôi",
                        onClick = { navController.navigate("AccountScreen") }
                    )
                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                    ProfileOptionItem(
                        icon = Icons.Default.LocationOn,
                        text = "Địa chỉ nhận hàng",
                        onClick = { navController.navigate("AddressScreen") }
                    )
                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                    ProfileOptionItem(
                        icon = Icons.Default.Star,
                        text = "Đánh giá của tôi",
                        onClick = { navController.navigate("MyReviewsScreen") }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Nút Đăng xuất
                    OutlinedButton(
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("LoginScreen") {
                                popUpTo("ProfileScreen") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Đăng xuất", fontSize = 16.sp)
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Logout",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileOptionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(24.dp),
            tint = Color.Black
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val selectedIndex = remember { mutableStateOf(3) }
    val scope = rememberCoroutineScope()
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black
    ) {
        BottomNavigationItem(
            selected = selectedIndex.value == 0,
            onClick = {
                scope.launch { selectedIndex.value = 0 }
                navController.navigate("home")
            },
            icon = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (selectedIndex.value == 0) Color.Black else Color.Transparent,
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home",
                        tint = if (selectedIndex.value == 0) Color.White else Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            alwaysShowLabel = false
        )
        BottomNavigationItem(
            selected = selectedIndex.value == 1,
            onClick = {
                scope.launch { selectedIndex.value = 1 }
                navController.navigate("SearchScreen")
            },
            icon = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (selectedIndex.value == 1) Color.Black else Color.Transparent,
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (selectedIndex.value == 1) Color.White else Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            alwaysShowLabel = false
        )
        BottomNavigationItem(
            selected = selectedIndex.value == 2,
            onClick = {
                scope.launch { selectedIndex.value = 2 }
                navController.navigate("CartScreen")
            },
            icon = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (selectedIndex.value == 2) Color.Black else Color.Transparent,
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.ShoppingBag,
                        contentDescription = "Cart",
                        tint = if (selectedIndex.value == 2) Color.White else Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            alwaysShowLabel = false
        )
        BottomNavigationItem(
            selected = selectedIndex.value == 3,
            onClick = {
                scope.launch { selectedIndex.value = 3 }
                navController.navigate("ProfileScreen")
            },
            icon = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (selectedIndex.value == 3) Color.Black else Color.Transparent,
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = if (selectedIndex.value == 3) Color.White else Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            alwaysShowLabel = false
        )
    }
}