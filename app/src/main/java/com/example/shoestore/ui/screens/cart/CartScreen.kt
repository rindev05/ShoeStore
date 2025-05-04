package com.example.shoestore.ui.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoestore.R
import com.example.shoestore.data.model.Product
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch

data class CartItem(val product: Product, val size: Int, var quantity: Int)

@Composable
fun CartScreen(navController: NavController) {
    // Danh sách sản phẩm trong giỏ hàng
    val cartItems = remember {
        mutableStateListOf(
            CartItem(
                product = Product(1, "Air Zoom Pegasus 37", 4599000.0, 4.5f, R.drawable.sa12_1, "Men's Shoes"),
                size = 41,
                quantity = 1
            ),
            CartItem(
                product = Product(2, "Air Zoom Pegasus 38", 4599000.0, 4.0f, R.drawable.sa13_1, "Men's Road Running Shoes"),
                size = 39,
                quantity = 2
            ),
            CartItem(
                product = Product(3, "Air Zoom Pegasus 38", 4599000.0, 4.0f, R.drawable.sb13_1, "Men's Road Running Shoes"),
                size = 45,
                quantity = 3
            ),
            CartItem(
                product = Product(4, "Air Zoom Pegasus 38", 4599000.0, 4.0f, R.drawable.sn16_1, "Men's Road Running Shoes"),
                size = 40,
                quantity = 4
            )

        )
    }

    // Tính tổng tiền
    val totalPrice by remember(cartItems) {
        derivedStateOf {
            cartItems.sumOf { it.product.price * it.quantity }
        }
    }

    // State cho mã giảm giá
    var couponCode by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                elevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.shoe_logo),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(120.dp)
                    )
                }
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Danh sách sản phẩm trong giỏ hàng
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(cartItems) { cartItem ->
                    CartItemRow(cartItem = cartItem)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Phần mã giảm giá và tổng tiền
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .padding(top = 5.dp)
            ) {
                // Mã giảm giá
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = couponCode,
                        onValueChange = { couponCode = it },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(0.dp)).border(1.dp,Color.Black),
                        placeholder = { Text("Nhập mã giảm giá của bạn") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ),
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { /* Áp dụng mã giảm giá */ },
                        modifier = Modifier
                            .height(50.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
                    ) {
                        Text(
                            text = "Áp dụng",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tổng tiền
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tổng:",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${NumberFormat.getNumberInstance(Locale("vi", "VN")).apply {
                            minimumFractionDigits = 0
                            maximumFractionDigits = 0
                        }.format(totalPrice)} đ",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nút Thanh toán
                Button(
                    onClick = { navController.navigate("CheckoutScreen") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
                ) {
                    Text(
                        text = "Thanh toán",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemRow(cartItem: CartItem) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN")).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)) // Bo góc nhẹ
            .background(Color(0xFFF6F6F6)) // Màu nền xám nhạt
            .padding(12.dp), // Padding vừa phải
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hình ảnh sản phẩm
        Image(
            painter = painterResource(id = cartItem.product.imageUrl),
            contentDescription = cartItem.product.name,
            modifier = Modifier
                .size(80.dp) // Kích thước ảnh
                .clip(RoundedCornerShape(12.dp)) // Bo góc ảnh
        )

        Spacer(modifier = Modifier.width(12.dp)) // Khoảng cách giữa ảnh và thông tin

        // Thông tin sản phẩm
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = cartItem.product.name,
                fontSize = 14.sp, // Font chữ vừa
                fontWeight = FontWeight.Bold,
                maxLines = 1 // Giới hạn 1 dòng
            )
            Text(
                text = "Kích cỡ: ${cartItem.size}",
                fontSize = 12.sp, // Font chữ nhỏ hơn
                color = Color.Gray
            )
            Text(
                text = "${numberFormat.format(cartItem.product.price)} đ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Bộ đếm số lượng
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (cartItem.quantity > 1) cartItem.quantity--
                },
                modifier = Modifier
                    .size(28.dp) // Kích thước nút nhỏ hơn
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)) // Màu nền
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    tint = Color.Black, // Màu icon
                    modifier = Modifier.size(16.dp) // Kích thước icon nhỏ hơn
                )
            }

            Text(
                text = cartItem.quantity.toString(),
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .width(24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 14.sp // Font chữ vừa
            )

            IconButton(
                onClick = { cartItem.quantity++ },
                modifier = Modifier
                    .size(28.dp) // Kích thước nút nhỏ hơn
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)) // Màu nền
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = Color.Black, // Màu icon
                    modifier = Modifier.size(16.dp) // Kích thước icon nhỏ hơn
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val selectedIndex = remember { mutableStateOf(2) }
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
                    androidx.compose.material3.Icon(
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
                    androidx.compose.material3.Icon(
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
                    androidx.compose.material3.Icon(
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
                    androidx.compose.material.Icon(
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