package com.example.shoestore.ui.screens.admin.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoestore.ui.theme.ShoeStoreTheme
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.*

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val status: String = "",
    val totalPrice: Double = 0.0,
    val orderDate: String = "",
    val timestamp: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderManagementScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var filteredOrders by remember { mutableStateOf<List<Order>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val userSnapshot = db.collection("users").get().await()
                val allOrders = mutableListOf<Order>()

                for (userDoc in userSnapshot.documents) {
                    val userId = userDoc.id
                    val orderSnapshot = db.collection("users")
                        .document(userId)
                        .collection("orders")
                        .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .get()
                        .await()

                    val userOrders = orderSnapshot.documents.mapNotNull { doc ->
                        val data = doc.data
                        data?.let {
                            Order(
                                orderId = it["orderId"] as? String ?: "",
                                userId = userId,
                                status = it["status"] as? String ?: "Chưa xác nhận",
                                totalPrice = (it["totalPrice"] as? Number)?.toDouble() ?: 0.0,
                                orderDate = it["orderDate"] as? String ?: "",
                                timestamp = (it["timestamp"] as? Long) ?: 0L
                            )
                        }
                    }
                    allOrders.addAll(userOrders)
                }

                orders = allOrders.sortedByDescending { it.timestamp }
                filteredOrders = orders
            } catch (e: Exception) {
                println("Error fetching orders: ${e.message}")
            }
        }
    }

    // Lọc danh sách dựa trên searchQuery
    LaunchedEffect(searchQuery) {
        filteredOrders = if (searchQuery.isBlank()) {
            orders
        } else {
            orders.filter {
                it.orderId.contains(searchQuery, ignoreCase = true) ||
                        it.orderDate.contains(searchQuery, ignoreCase = true)
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
                                    text = "Quản lý đơn hàng",
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
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1C2526)
                    )
                )
            },
            bottomBar = { BottomNavigationBar3(navController = navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1C2526))
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // Thanh tìm kiếm
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Tìm đơn hàng (ID hoặc ngày)", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textStyle = TextStyle(color = Color.White),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color.White
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                )

                Divider(
                    color = Color(0xFF2196F3),
                    thickness = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredOrders) { order ->
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

    // Màu sắc cho trạng thái
    val statusColor = when (order.status) {
        "Chưa xác nhận" -> Color.Red
        "Đã xác nhận" -> Color.Blue
        "Đã giao" -> Color.Green
        else -> Color.Gray
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
                color = statusColor
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
            onClick = { navController.navigate("AdminOrderDetailScreen?orderId=${order.orderId}&userId=${order.userId}") },
            shape = RoundedCornerShape(8.dp),
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

@Composable
fun BottomNavigationBar3(navController: NavController) {
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
                navController.navigate("AdminDashboard")
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
                        Icons.Default.Dashboard,
                        contentDescription = "Dashboard",
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
                navController.navigate("AdminProductManagement")
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
                        Icons.Default.ShoppingBag,
                        contentDescription = "ProductManagement",
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
                navController.navigate("AdminUserManagement")
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
                        Icons.Default.Person,
                        contentDescription = "UserManagement",
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
                navController.navigate("AdminOrderManagement")
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
                        Icons.Default.ShoppingCart,
                        contentDescription = "OrderManagement",
                        tint = if (selectedIndex.value == 3) Color.White else Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            alwaysShowLabel = false
        )
        BottomNavigationItem(
            selected = selectedIndex.value == 4,
            onClick = {
                scope.launch { selectedIndex.value = 4 }
                navController.navigate("ProfileAdmin")
            },
            icon = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (selectedIndex.value == 4) Color.Black else Color.Transparent,
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = "ProfileAdmin",
                        tint = if (selectedIndex.value == 4) Color.White else Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            alwaysShowLabel = false
        )
    }
}