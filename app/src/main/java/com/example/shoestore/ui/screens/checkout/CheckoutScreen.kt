package com.example.shoestore.ui.checkout

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shoestore.R
import com.example.shoestore.ui.screens.detail.Product
import com.example.shoestore.ui.theme.ShoeStoreTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class CartItem(val product: Product, val size: Int, val quantity: Int)
data class Address(
    val name: String,
    val phone: String,
    val details: String,
    val isDefault: Boolean = false,
    val id: String = ""
)

data class OrderItem(
    val imageUrl: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val size: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController, navBackStackEntry: NavBackStackEntry) {
    val args = navBackStackEntry.arguments
    val cartItems = remember { mutableStateListOf<CartItem>() }
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    var defaultAddress by remember { mutableStateOf<Address?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(user) {
        if (user != null) {
            try {
                val snapshot = db.collection("users")
                    .document(user.uid)
                    .collection("addresses")
                    .whereEqualTo("isDefault", true)
                    .get()
                    .await()
                if (!snapshot.isEmpty) {
                    val doc = snapshot.documents.first()
                    defaultAddress = Address(
                        id = doc.id,
                        name = doc.getString("fullName") ?: "",
                        phone = doc.getString("phoneNumber") ?: "",
                        details = "${doc.getString("specificAddress") ?: ""}, ${doc.getString("street") ?: ""}",
                        isDefault = true
                    )
                } else {
                    defaultAddress = null
                }
            } catch (e: Exception) {
                println("Error fetching default address: ${e.message}")
            }
        }
    }

    val productId = args?.getInt("productId")
    val size = args?.getInt("size")
    val cartItemsParam = args?.getString("cartItems")
    val isFromCart = cartItemsParam != null && cartItemsParam.isNotEmpty() // Kiểm tra nguồn từ "Thanh toán"

    LaunchedEffect(productId, size) {
        if (productId != null && size != null) {
            println("Received args for Buy Now: productId=$productId, size=$size")
            val db = FirebaseFirestore.getInstance()
            val collections = listOf("products-bitis", "products-nike", "products-adidas")

            var fetchedProduct: Product? = null
            for (collection in collections) {
                val snapshot = db.collection(collection)
                    .whereEqualTo("id", productId)
                    .get()
                    .await()
                if (snapshot.documents.isNotEmpty()) {
                    val doc = snapshot.documents.first()
                    val data = doc.data
                    fetchedProduct = data?.let {
                        Product(
                            id = (it["id"] as? Long)?.toInt() ?: 0,
                            name = it["name"] as? String ?: "",
                            price = (it["price"] as? Number)?.toDouble() ?: 0.0,
                            brand = it["brand"] as? String ?: "",
                            imageUrl = it["imageUrl"] as? String ?: "",
                            description = it["description"] as? String ?: "",
                            size = (it["size"] as? List<*>)?.filterIsInstance<Number>()
                                ?.map { it.toInt() } ?: emptyList(),
                            rating = (it["rating"] as? Number)?.toFloat() ?: 0f,
                            images = (it["images"] as? List<*>)?.filterIsInstance<String>()
                                ?: emptyList(),
                            reviews = emptyList()
                        )
                    }
                    break
                }
            }

            fetchedProduct?.let {
                cartItems.clear()
                cartItems.add(CartItem(it, size, 1))
                println("Added CartItem for Buy Now: ${it.name}, size=$size, quantity=1")
            }
        }
    }

    LaunchedEffect(cartItemsParam) {
        if (cartItemsParam != null && cartItemsParam.isNotEmpty()) {
            println("Received cartItemsParam: $cartItemsParam")
            val items = Uri.decode(cartItemsParam).split(";").mapNotNull { item ->
                val parts = item.split(",")
                if (parts.size == 3) {
                    val productIdFromCart = parts[0].toIntOrNull() ?: return@mapNotNull null
                    val db = FirebaseFirestore.getInstance()
                    val collections = listOf("products-bitis", "products-nike", "products-adidas")

                    var fetchedProduct: Product? = null
                    for (collection in collections) {
                        val snapshot = db.collection(collection)
                            .whereEqualTo("id", productIdFromCart)
                            .get()
                            .await()
                        if (snapshot.documents.isNotEmpty()) {
                            val doc = snapshot.documents.first()
                            val data = doc.data
                            fetchedProduct = data?.let {
                                Product(
                                    id = (it["id"] as? Long)?.toInt() ?: 0,
                                    name = it["name"] as? String ?: "",
                                    price = (it["price"] as? Number)?.toDouble() ?: 0.0,
                                    brand = it["brand"] as? String ?: "",
                                    imageUrl = it["imageUrl"] as? String ?: "",
                                    description = it["description"] as? String ?: "",
                                    size = (it["size"] as? List<*>)?.filterIsInstance<Number>()
                                        ?.map { it.toInt() } ?: emptyList(),
                                    rating = (it["rating"] as? Number)?.toFloat() ?: 0f,
                                    images = (it["images"] as? List<*>)?.filterIsInstance<String>()
                                        ?: emptyList(),
                                    reviews = emptyList()
                                )
                            }
                            break
                        }
                    }

                    fetchedProduct?.let {
                        CartItem(
                            product = it,
                            size = parts[1].toIntOrNull() ?: 0,
                            quantity = parts[2].toIntOrNull() ?: 0
                        )
                    }
                } else null
            }
            cartItems.clear()
            cartItems.addAll(items)
            println("Loaded ${cartItems.size} items from cartItemsParam")
        }
    }

    val totalPrice = cartItems.sumOf { it.product.price * it.quantity }

    ShoeStoreTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

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
                                tint = Color.Red,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "THANH TOÁN",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Địa chỉ nhận hàng",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            if (defaultAddress != null) {
                                Text(
                                    text = "${defaultAddress!!.name} - ${defaultAddress!!.phone}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = defaultAddress!!.details,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            } else {
                                Text(
                                    text = "Chưa có địa chỉ mặc định. Vui lòng thêm địa chỉ!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 14.sp,
                                    color = Color.Red
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Edit Address",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { navController.navigate("AddressScreen") }
                        )
                    }

                    Divider(color = Color.LightGray, thickness = 0.5.dp)

                    cartItems.forEach { cartItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = cartItem.product.imageUrl,
                                contentDescription = cartItem.product.name,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF6F6F6)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = cartItem.product.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Kích cỡ: ${cartItem.size}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(cartItem.product.price)} đ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "x${cartItem.quantity}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Divider(color = Color.LightGray, thickness = 0.5.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = "Location",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Phương thức vận chuyển",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Giao hàng nhanh - Miễn phí",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Edit Shipping",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { /* Handle edit shipping */ }
                        )
                    }

                    Divider(color = Color.LightGray, thickness = 0.5.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.Money,
                            contentDescription = "Location",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Phương thức thanh toán",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Thanh toán khi nhận hàng",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
//                            Image(
//                                painter = painterResource(id = R.drawable.momo_logo),
//                                contentDescription = "MOMO Logo",
//                                modifier = Modifier.size(24.dp)
//                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Edit Payment",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { /* Handle edit payment */ }
                        )
                    }

                    Divider(color = Color.LightGray, thickness = 0.5.dp)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Chi tiết thanh toán",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tổng tiền hàng:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(totalPrice)} đ",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tổng tiền vận chuyển:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "0 đ",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Voucher giảm giá:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "0 đ",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tổng thanh toán:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(totalPrice)} đ",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (user != null) {
                                if (defaultAddress != null) {
                                    coroutineScope.launch {
                                        try {
                                            // Tạo mã đơn hàng ngẫu nhiên
                                            val orderId = "ORDER-${UUID.randomUUID().toString().take(8).uppercase()}"
                                            val orderDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                                            val orderItems = cartItems.map { item ->
                                                OrderItem(
                                                    imageUrl = item.product.imageUrl,
                                                    name = item.product.name,
                                                    price = item.product.price,
                                                    quantity = item.quantity,
                                                    size = item.size
                                                )
                                            }

                                            // Lưu địa chỉ nhận hàng
                                            val addressData = defaultAddress?.let {
                                                hashMapOf(
                                                    "name" to it.name,
                                                    "phone" to it.phone,
                                                    "details" to it.details
                                                )
                                            } ?: hashMapOf()

                                            // Lưu đơn hàng vào Firestore
                                            val orderData = hashMapOf(
                                                "orderId" to orderId,
                                                "orderDate" to orderDate,
                                                "items" to orderItems,
                                                "totalPrice" to totalPrice,
                                                "address" to addressData,
                                                "timestamp" to System.currentTimeMillis()
                                            )

                                            db.collection("users")
                                                .document(user.uid)
                                                .collection("orders")
                                                .add(orderData)
                                                .await()

                                            // Xóa giỏ hàng nếu đặt từ "Thanh toán"
                                            if (isFromCart) {
                                                val cartSnapshot = db.collection("users")
                                                    .document(user.uid)
                                                    .collection("cart")
                                                    .get()
                                                    .await()
                                                cartSnapshot.documents.forEach { doc ->
                                                    doc.reference.delete().await()
                                                }
                                                snackbarHostState.showSnackbar("Đã xóa giỏ hàng!")
                                            }

                                            // Điều hướng đến OrderSuccessScreen với orderId
                                            navController.navigate("OrderSuccessScreen?orderId=$orderId")
                                        } catch (e: Exception) {
                                            snackbarHostState.showSnackbar("Lỗi khi đặt hàng: ${e.message}")
                                        }
                                    }
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Vui lòng chọn địa chỉ mặc định!")
                                    }
                                }
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Vui lòng đăng nhập để đặt hàng!")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Đặt hàng", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}