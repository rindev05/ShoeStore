package com.example.shoestore.ui.screens.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shoestore.R
import com.example.shoestore.ui.screens.home.saveToRecentlyViewed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.Locale
import kotlin.random.Random

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val brand: String,
    val imageUrl: String,
    val description: String,
    val size: List<Int> = emptyList(),
    val rating: Float = 0f,
    val images: List<String> = emptyList(),
    val reviews: List<Review> = emptyList()
)

data class Review(
    val user: String,
    val rating: Float,
    val comment: String,
    val date: String,
    val imageAvatar: String
)

data class CartItem(
    val product: Product, // Sử dụng Product từ package này
    val size: Int,
    val quantity: MutableState<Int> // Sử dụng MutableState để Compose theo dõi thay đổi
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductDetailScreen(navController: NavController, productId: Int) {
    var product by remember { mutableStateOf<Product?>(null) }
    var recommendedProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // State để hiển thị thông báo
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Tải dữ liệu từ Firestore, bao gồm sub-collection "reviews"
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val collections = listOf("products-bitis", "products-nike", "products-adidas")

        for (collection in collections) {
            try {
                val snapshot = db.collection(collection)
                    .whereEqualTo("id", productId)
                    .get()
                    .await()

                if (snapshot.documents.isNotEmpty()) {
                    val doc = snapshot.documents.first()
                    val data = doc.data
                    val reviewsSnapshot = doc.reference.collection("reviews").get().await()
                    val reviews = reviewsSnapshot.documents.mapNotNull { reviewDoc ->
                        val reviewData = reviewDoc.data
                        reviewData?.let {
                            Review(
                                user = it["user"] as? String ?: "",
                                rating = (it["rating"] as? Number)?.toFloat() ?: 0f,
                                comment = it["comment"] as? String ?: "",
                                date = it["date"] as? String ?: "",
                                imageAvatar = it["imageAvatar"] as? String ?: ""
                            )
                        }
                    }

                    product = data?.let {
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
                            reviews = reviews
                        )
                    }

                    // Lấy sản phẩm đề xuất cùng brand
                    if (product != null) {
                        val allProductsSnapshot = db.collection(collection).get().await()
                        val allProducts = allProductsSnapshot.documents.mapNotNull { prodDoc ->
                            val prodData = prodDoc.data
                            prodData?.let {
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
                        }.filter { it.id != productId }

                        val filteredProducts = allProducts.filter { it.brand == product!!.brand }
                        recommendedProducts = if (filteredProducts.size >= 7) {
                            filteredProducts.shuffled(Random(System.currentTimeMillis())).take(7)
                        } else {
                            filteredProducts.shuffled(Random(System.currentTimeMillis()))
                        }
                    }
                    break
                }
            } catch (e: Exception) {
                println("Error loading product from $collection: $e")
            }
        }
        isLoading = false
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (product == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text("Product not found", fontSize = 18.sp, color = Color.Red)
        }
        return
    }

    val averageRating = if (product!!.reviews.isNotEmpty()) {
        product!!.reviews.map { it.rating }.average().toFloat()
    } else {
        product!!.rating
    }

    val productImages = if (product!!.images.isNotEmpty()) product!!.images else listOf(product!!.imageUrl)
    var selectedImage by remember { mutableStateOf(productImages[0]) }

    val numberFormat = remember {
        NumberFormat.getNumberInstance(Locale("vi", "VN")).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 0
        }
    }

    var selectedSize by remember { mutableStateOf<Int?>(null) }
    var showSizePopup by remember { mutableStateOf(false) }
    var showSizeGuidePopup by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded }
    )

    var showAllReviews by remember { mutableStateOf(false) }

    LaunchedEffect(showSizePopup) {
        if (showSizePopup) {
            if (showSizeGuidePopup) {
                showSizeGuidePopup = false
            }
            coroutineScope.launch { sheetState.show() }
        } else {
            coroutineScope.launch { sheetState.hide() }
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            SizeSelectionPopup(
                availableSizes = product!!.size,
                onSizeSelected = { size ->
                    selectedSize = size
                    showSizePopup = false
                },
                onCancel = {
                    showSizePopup = false
                }
            )
        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetBackgroundColor = Color.White
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = product!!.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    },
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp
                )
            },
            bottomBar = { BottomNavigationBar(navController = navController) },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                item {
                    Column(modifier = Modifier.padding(0.dp)) {
                        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
                        val imageHeight = screenHeight * 2 / 3

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(imageHeight)
                                .background(Color(0xFFF6F6F6))
                        ) {
                            AsyncImage(
                                model = selectedImage,
                                contentDescription = product!!.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(imageHeight)
                                    .statusBarsPadding(),
                                contentScale = ContentScale.Fit
                            )
                        }

                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(productImages) { imageUrl ->
                                val isSelected = imageUrl == selectedImage
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF6F6F6))
                                            .clickable {
                                                selectedImage = imageUrl
                                            }
                                    ) {
                                        AsyncImage(
                                            model = imageUrl,
                                            contentDescription = "Thumbnail",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .width(60.dp)
                                                .height(3.dp)
                                                .background(Color.Black)
                                                .padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                text = product!!.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Rating",
                                    tint = Color.Yellow
                                )
                                Text(
                                    text = "${String.format("%.1f", averageRating)} (${product!!.reviews.size} Đánh giá)",
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${numberFormat.format(product!!.price)} đ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(40.dp))
                            Text(
                                text = "Hướng dẫn chọn Size",
                                fontSize = 14.sp,
                                color = Color.Blue,
                                modifier = Modifier.clickable { showSizeGuidePopup = true },
                                fontWeight = FontWeight.Light
                            )
                            Spacer(modifier = Modifier.height(40.dp))
                            Button(
                                onClick = { showSizePopup = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .border(1.dp, Color.Gray, RoundedCornerShape(20.dp)),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.White,
                                    contentColor = Color.Black
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = selectedSize?.let { "Kích cỡ $it" } ?: "Kích cỡ",
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Select Size",
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    if (selectedSize == null) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Vui lòng chọn kích cỡ trước!")
                                        }
                                    } else {
                                        navController.navigate("CheckoutScreen/${product!!.id}/${selectedSize}")
                                        println("Navigating to Checkout with productId=${product!!.id}, size=$selectedSize") // Thêm log để debug
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.Black,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Mua ngay", color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    // Thêm vào giỏ hàng
                                    if (selectedSize == null) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Vui lòng chọn kích cỡ trước!")
                                        }
                                    } else {
                                        val user = FirebaseAuth.getInstance().currentUser
                                        if (user == null) {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Vui lòng đăng nhập để thêm vào giỏ hàng!")
                                            }
                                        } else {
                                            val db = FirebaseFirestore.getInstance()
                                            val cartDocId = "${product!!.id}_${selectedSize}" // Sử dụng id_size làm ID tài liệu
                                            val cartRef = db.collection("users")
                                                .document(user.uid)
                                                .collection("cart")
                                                .document(cartDocId)

                                            val cartItem = hashMapOf(
                                                "productId" to product!!.id,
                                                "name" to product!!.name,
                                                "price" to product!!.price,
                                                "size" to selectedSize,
                                                "quantity" to 1,
                                                "imageUrl" to product!!.imageUrl // Thêm imageUrl
                                            )

                                            cartRef.set(cartItem)
                                                .addOnSuccessListener {
                                                    coroutineScope.launch {
                                                        snackbarHostState.showSnackbar("Đã thêm sản phẩm vào giỏ hàng!")
                                                    }
                                                }
                                                .addOnFailureListener { e ->
                                                    coroutineScope.launch {
                                                        snackbarHostState.showSnackbar("Lỗi khi thêm vào giỏ hàng: ${e.message}")
                                                    }
                                                }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .border(1.dp, Color.Gray, RoundedCornerShape(20.dp)),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.White,
                                    contentColor = Color.Black
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text("Thêm vào giỏ", color = Color.Black)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBag,
                                        contentDescription = "Add to Cart",
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(40.dp))
                            Text(
                                text = "MÔ TẢ SẢN PHẨM",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = product!!.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Light
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = product!!.description, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(40.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "ĐÁNH GIÁ SẢN PHẨM",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (product!!.reviews.isNotEmpty()) {
                                    IconButton(onClick = { showAllReviews = !showAllReviews }) {
                                        Icon(
                                            imageVector = if (showAllReviews) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Toggle Reviews",
                                            tint = Color.Black
                                        )
                                    }
                                }
                            }
                            if (product!!.reviews.isEmpty()) {
                                Text(
                                    text = "Chưa có đánh giá",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                val reviewsToShow =
                                    if (showAllReviews) product!!.reviews else product!!.reviews.take(
                                        1
                                    )
                                reviewsToShow.forEach { review ->
                                    ReviewItem(review = review)
                                }
                            }

                            Spacer(modifier = Modifier.height(40.dp))
                            Text(
                                text = "BẠN CÓ THỂ THÍCH",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(recommendedProducts) { recProduct ->
                                    ProductItem(
                                        product = recProduct,
                                        onClick = {
                                            navController.navigate("product/${recProduct.id}")
                                            saveToRecentlyViewed(recProduct.id)
                                        },
                                        modifier = Modifier.width(160.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }
    }

    if (showSizeGuidePopup) {
        Dialog(
            onDismissRequest = { showSizeGuidePopup = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                var resetZoomKey by remember { mutableStateOf(0) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .zIndex(1f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { showSizeGuidePopup = false }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                    TextButton(onClick = {
                        resetZoomKey++
                    }) {
                        Text("Reset Zoom", color = Color.Black)
                    }
                }

                ZoomableImage(resetZoomKey = resetZoomKey)
            }
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    println("Rendering Product: ${product.name}")
    val numberFormat = remember {
        NumberFormat.getNumberInstance(Locale("vi", "VN")).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 0
        }
    }
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF6F6F6))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bestseller",
                color = Color.Red,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = product.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = product.brand,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "${numberFormat.format(product.price)} đ",
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ZoomableImage(resetZoomKey: Int) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val imageWidth = screenWidth.value
    val imageHeight = screenHeight.value * 0.8f

    LaunchedEffect(resetZoomKey) {
        scale = 1f
        offsetX = 0f
        offsetY = 0f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 4f)
                    val scaledWidth = imageWidth * scale
                    val scaledHeight = imageHeight * scale

                    val maxOffsetX = if (scaledWidth > screenWidth.value) {
                        (scaledWidth - screenWidth.value) / 2
                    } else {
                        0f
                    }
                    val maxOffsetY = if (scaledHeight > screenHeight.value) {
                        (scaledHeight - screenHeight.value) / 2
                    } else {
                        0f
                    }

                    offsetX += pan.x
                    offsetY += pan.y

                    offsetX = offsetX.coerceIn(-maxOffsetX, maxOffsetX)
                    offsetY = offsetY.coerceIn(-maxOffsetY, maxOffsetY)
                }
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.size_guide),
            contentDescription = "Size Guide",
            modifier = Modifier
                .fillMaxSize()
                .offset(x = offsetX.dp, y = offsetY.dp)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale
                ),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun SizeSelectionPopup(
    availableSizes: List<Int>,
    onSizeSelected: (Int) -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel", color = Color.Black)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Chọn Kích Cỡ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn {
            items(availableSizes) { size ->
                SizeItem(
                    size = size,
                    onClick = { onSizeSelected(size) }
                )
            }
        }
    }
}

@Composable
fun SizeItem(
    size: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Size $size",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ReviewItem(review: Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp)),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = review.imageAvatar,
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = review.user,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = Color.Yellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${review.rating}",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Text(
                    text = review.comment,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = review.date,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val selectedIndex = remember { mutableStateOf(1) }
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