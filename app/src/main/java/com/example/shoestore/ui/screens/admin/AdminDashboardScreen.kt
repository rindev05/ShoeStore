package com.example.shoestore.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

// Data classes
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = ""
)

data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val imageUrl: String = ""
)

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val status: String = "",
    val totalPrice: Double = 0.0,
    val timestamp: Long = 0L,
    val isReceipt: Boolean = false
)

data class RevenueData(
    val date: String,
    val revenue: Double,
    val orderCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var dailyRevenue by remember { mutableStateOf<List<RevenueData>>(emptyList()) }
    var weeklyRevenue by remember { mutableStateOf<RevenueData?>(null) }
    var monthlyRevenue by remember { mutableStateOf<RevenueData?>(null) }
    var totalUsers by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    var selectedMonth by remember { mutableStateOf(Calendar.getInstance().time) }

    // Format ngày hiện tại
    val currentDate = Calendar.getInstance().time
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val monthFormat = SimpleDateFormat("MM-yyyy", Locale.getDefault())

    // Tính startDate và endDate cho 7 ngày gần nhất
    val calendar = Calendar.getInstance().apply { time = currentDate }
    val startDate = calendar.apply { add(Calendar.DAY_OF_YEAR, -6) }.time
    val endDate = currentDate

    // Lấy dữ liệu ban đầu (hôm nay và 7 ngày gần nhất)
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                // Lấy tất cả đơn hàng đã giao (isReceipt: true) từ tất cả user
                val allOrders = mutableListOf<Order>()
                val usersSnapshot = db.collection("users").get().await()
                for (userDoc in usersSnapshot) {
                    val ordersSnapshot = db.collection("users")
                        .document(userDoc.id)
                        .collection("orders")
                        .whereEqualTo("isReceipt", true)
                        .get()
                        .await()
                    allOrders.addAll(ordersSnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Order::class.java)?.copy(orderId = doc.id)
                    })
                }

                // Tổng người dùng
                totalUsers = usersSnapshot.size()

                // Doanh thu hôm nay
                val today = dateFormat.format(currentDate)
                val todayOrders = allOrders.filter { order ->
                    val orderDate = Date(order.timestamp).let { dateFormat.format(it) }
                    orderDate == today
                }
                val todayRevenue = todayOrders.sumOf { it.totalPrice }
                val todayOrderCount = todayOrders.size

                // Doanh thu 7 ngày gần nhất
                val weekOrders = allOrders.filter { order ->
                    val orderDate = Date(order.timestamp)
                    orderDate in startDate..endDate
                }
                val weekRevenue = weekOrders.sumOf { it.totalPrice }
                val weekOrderCount = weekOrders.size

                // Cập nhật dữ liệu
                dailyRevenue = listOf(RevenueData(today, todayRevenue, todayOrderCount))
                weeklyRevenue = RevenueData("${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}", weekRevenue, weekOrderCount)
            } catch (e: Exception) {
                println("Error fetching dashboard data: ${e.message}")
            }
        }
    }

    // Lấy dữ liệu doanh thu theo tháng khi selectedMonth thay đổi
    LaunchedEffect(selectedMonth) {
        coroutineScope.launch {
            try {
                // Lấy tất cả đơn hàng đã giao (isReceipt: true) từ tất cả user
                val allOrders = mutableListOf<Order>()
                val usersSnapshot = db.collection("users").get().await()
                for (userDoc in usersSnapshot) {
                    val ordersSnapshot = db.collection("users")
                        .document(userDoc.id)
                        .collection("orders")
                        .whereEqualTo("isReceipt", true)
                        .get()
                        .await()
                    allOrders.addAll(ordersSnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Order::class.java)?.copy(orderId = doc.id)
                    })
                }

                // Doanh thu theo tháng đã chọn
                val selectedMonthStr = monthFormat.format(selectedMonth)
                val monthOrders = allOrders.filter { order ->
                    val orderMonth = monthFormat.format(Date(order.timestamp))
                    orderMonth == selectedMonthStr
                }
                val monthRevenue = monthOrders.sumOf { it.totalPrice }
                val monthOrderCount = monthOrders.size

                // Cập nhật dữ liệu
                monthlyRevenue = RevenueData(selectedMonthStr, monthRevenue, monthOrderCount)
            } catch (e: Exception) {
                println("Error fetching monthly revenue: ${e.message}")
            }
        }
    }

    ShoeStoreTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Admin Dashboard", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1C2526))
                )
            },
            bottomBar = { BottomNavigationBar(navController = navController) }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1C2526))
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // Doanh thu hôm nay
                item {
                    Text(
                        "Doanh thu hôm nay (${dateFormat.format(currentDate)})",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatCard("Doanh thu", formatNumber(dailyRevenue.firstOrNull()?.revenue ?: 0.0))
                        StatCard("Đơn hàng", formatNumber(dailyRevenue.firstOrNull()?.orderCount?.toDouble() ?: 0.0))
                        StatCard("Người dùng", formatNumber(totalUsers.toDouble()))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Doanh thu 7 ngày gần nhất
                item {
                    Text(
                        "Doanh thu 7 ngày gần nhất (${dateFormat.format(startDate)} - ${dateFormat.format(endDate)})",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatCard("Doanh thu", formatNumber(weeklyRevenue?.revenue ?: 0.0))
                        StatCard("Đơn hàng", formatNumber(weeklyRevenue?.orderCount?.toDouble() ?: 0.0))
                        StatCard("Người dùng", formatNumber(totalUsers.toDouble()))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Doanh thu theo tháng
                item {
                    Text(
                        "Doanh thu theo tháng (${monthFormat.format(selectedMonth)})",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ScrollableDatePicker(
                        selectedDate = selectedMonth,
                        onDateSelected = { selectedMonth = it }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatCard("Doanh thu", formatNumber(monthlyRevenue?.revenue ?: 0.0))
                        StatCard("Đơn hàng", formatNumber(monthlyRevenue?.orderCount?.toDouble() ?: 0.0))
                        StatCard("Người dùng", formatNumber(totalUsers.toDouble()))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// Hàm định dạng số theo quy tắc
fun formatNumber(value: Double): String {
    return when {
        value >= 1_000_000_000 -> String.format("%.1f", value / 1_000_000_000) + "b"
        value >= 1_000_000 -> String.format("%.1f", value / 1_000_000) + "m"
        value >= 1_000 -> String.format("%.1f", value / 1_000) + "k"
        else -> String.format("%.1f", value)
    }
}

@Composable
fun StatCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3B3C))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, color = Color.Gray, fontSize = 12.sp)
            Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(110.dp)
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
    ) {
        Text(label, fontSize = 12.sp, color = Color.White)
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val selectedIndex = remember { mutableStateOf(0) }
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

@Composable
fun ScrollableDatePicker(selectedDate: Date, onDateSelected: (Date) -> Unit) {
    val calendarState = remember { Calendar.getInstance().apply { time = selectedDate } }
    var selectedYear by remember { mutableStateOf(calendarState.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(calendarState.get(Calendar.MONTH)) }

    // Mảng cố định chứa tên các tháng
    val months = listOf(
        "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
        "Tháng 7", "Tháng 9", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Picker cho tháng
        Column(
            modifier = Modifier
                .weight(1f)
                .height(200.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(months.size) { index ->
                    val monthName = months[index]
                    Text(
                        text = monthName,
                        color = if (index == selectedMonth) Color.White else Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                if (index == selectedMonth) Color(0xFF2196F3) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedMonth = index
                                val newDate = Calendar.getInstance().apply {
                                    set(selectedYear, selectedMonth, 1)
                                }.time
                                onDateSelected(newDate)
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Picker cho năm
        Column(
            modifier = Modifier
                .weight(1f)
                .height(200.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(100) { index ->
                    val year = 2025 - index
                    Text(
                        text = year.toString(),
                        color = if (year == selectedYear) Color.White else Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                if (year == selectedYear) Color(0xFF2196F3) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedYear = year
                                val newDate = Calendar.getInstance().apply {
                                    set(selectedYear, selectedMonth, 1)
                                }.time
                                onDateSelected(newDate)
                            }
                    )
                }
            }
        }
    }
}