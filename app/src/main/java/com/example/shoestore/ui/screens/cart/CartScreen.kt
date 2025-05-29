package com.example.shoestore.ui.cart

import android.net.Uri
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
import coil.compose.AsyncImage
import com.example.shoestore.R
import com.example.shoestore.ui.screens.detail.Product // Import Product từ package detail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.Locale

data class CartItem(
    val product: Product,
    val size: Int,
    val quantity: MutableState<Int>
)

@Composable
fun CartScreen(navController: NavController) {
    // Danh sách sản phẩm trong giỏ hàng từ Firestore
    val cartItems = remember { mutableStateListOf<CartItem>() }
    val coroutineScope = rememberCoroutineScope()

    // Tính tổng tiền
    val totalPrice by remember(cartItems) {
        derivedStateOf {
            cartItems.sumOf { it.product.price * it.quantity.value }
        }
    }

    // State cho mã giảm giá
    var couponCode by remember { mutableStateOf("") }

    // Tải dữ liệu giỏ hàng từ Firestore
    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val cartSnapshot = db.collection("users")
                .document(user.uid)
                .collection("cart")
                .get()
                .await()

            val cartItemsFromFirestore = cartSnapshot.documents.mapNotNull { doc ->
                val data = doc.data
                data?.let {
                    CartItem(
                        product = Product(
                            id = (it["productId"] as? Long)?.toInt() ?: 0,
                            name = it["name"] as? String ?: "",
                            price = (it["price"] as? Number)?.toDouble() ?: 0.0,
                            brand = "",
                            imageUrl = it["imageUrl"] as? String ?: "",
                            description = ""
                        ),
                        size = (it["size"] as? Long)?.toInt() ?: 0,
                        quantity = mutableStateOf((it["quantity"] as? Long)?.toInt() ?: 1)
                    )
                }
            }
            cartItems.clear()
            cartItems.addAll(cartItemsFromFirestore)
        }
    }

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
                    CartItemRow(
                        cartItem = cartItem,
                        onQuantityChanged = { newQuantity ->
                            val user = FirebaseAuth.getInstance().currentUser
                            if (user != null) {
                                val db = FirebaseFirestore.getInstance()
                                val cartDocId = "${cartItem.product.id}_${cartItem.size}"
                                if (newQuantity > 0) {
                                    // Cập nhật số lượng trên Firestore
                                    db.collection("users")
                                        .document(user.uid)
                                        .collection("cart")
                                        .document(cartDocId)
                                        .update("quantity", newQuantity)
                                } else {
                                    // Xóa sản phẩm khỏi Firestore
                                    db.collection("users")
                                        .document(user.uid)
                                        .collection("cart")
                                        .document(cartDocId)
                                        .delete()
                                        .addOnSuccessListener {
                                            // Xóa sản phẩm khỏi danh sách giao diện
                                            cartItems.remove(cartItem)
                                        }
                                }
                            }
                        }
                    )
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
                            .clip(RoundedCornerShape(0.dp))
                            .border(1.dp, Color.Black),
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
                    onClick = {
                        val cartItemsJson = Uri.encode(cartItems.map { "${it.product.id},${it.size},${it.quantity.value}" }.joinToString(";"))
                        navController.navigate("CheckoutScreen?cartItems=$cartItemsJson")
                    },
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
fun CartItemRow(cartItem: CartItem, onQuantityChanged: (Int) -> Unit) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN")).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF6F6F6))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hình ảnh sản phẩm
        AsyncImage(
            model = cartItem.product.imageUrl,
            contentDescription = cartItem.product.name,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Thông tin sản phẩm
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = cartItem.product.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = "Kích cỡ: ${cartItem.size}",
                fontSize = 12.sp,
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
                    val newQuantity = cartItem.quantity.value - 1
                    cartItem.quantity.value = newQuantity
                    onQuantityChanged(newQuantity)
                },
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = cartItem.quantity.value.toString(),
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .width(24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 14.sp
            )

            IconButton(
                onClick = {
                    val newQuantity = cartItem.quantity.value + 1
                    cartItem.quantity.value = newQuantity
                    onQuantityChanged(newQuantity)
                },
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
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