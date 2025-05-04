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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.shoestore.R
import com.example.shoestore.data.model.Product
import com.example.shoestore.data.model.Review
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductDetailScreen(navController: NavController, productId: Int) {
    // Dữ liệu sản phẩm đồng bộ với ProductListScreen
    val products = listOf(
        Product(1, "Nike Dunk Low Retro", 2929000.0, 4.5f, R.drawable.sa12_1, "Chinh phục mọi hành trình với Nike Air Zoom Pegasus 37. Đệm Zoom Air cải tiến mang lại độ phản hồi tối ưu, trong khi lớp foam nhẹ giúp bạn tăng tốc mượt mà. Thoáng khí, ôm chân, hoàn hảo cho chạy bộ và phong cách hàng ngày.", reviews = listOf(
            Review("Test User", 4f, "Loved it! Amazing product. Can’t go wrong with this one. 100% recommend.", "5/15/2022", R.drawable.s1),
            Review("Test User", 3f, "Loved it! Amazing product. Can’t go wrong with this one. 100% recommend.", "5/15/2022", R.drawable.s2),
            Review("Test User", 2f, "Loved it! Amazing product. Can’t go wrong with this one. 100% recommend.", "5/15/2022", R.drawable.s3),
            Review("Test User", 5f, "Loved it! Amazing product. Can’t go wrong with this one. 100% recommend.", "5/15/2022", R.drawable.s4),
            Review("Ahmed Khan", 5f, "Excellent quality for the price.", "7/20/2022", R.drawable.s1)
        ),
            images = listOf(R.drawable.sa12_1, R.drawable.sa12_2, R.drawable.sa12_3, R.drawable.sa12_4, R.drawable.sa12_5)
            ),
        Product(2, "Nike Pegasus Plus", 5279000.0, 4.0f, R.drawable.sa13_1, "Chinh phục mọi hành trình với Nike Air Zoom Pegasus 37. Đệm Zoom Air cải tiến mang lại độ phản hồi tối ưu, trong khi lớp foam nhẹ giúp bạn tăng tốc mượt mà. Thoáng khí, ôm chân, hoàn hảo cho chạy bộ và phong cách hàng ngày.", reviews = listOf(
            Review("John Doe", 4f, "Very comfortable shoes!", "8/10/2022", R.drawable.s1),
            Review("Test User", 5f, "Loved it! Amazing product. Can’t go wrong with this one. 100% recommend.", "5/15/2022", R.drawable.s2),
            Review("Test User", 5f, "Loved it! Amazing product. Can’t go wrong with this one. 100% recommend.", "5/15/2022", R.drawable.s3),
            Review("Sara Lee", 5f, "Worth every penny!", "9/01/2022", R.drawable.s4)
        ),
            images = listOf(R.drawable.sa13_1, R.drawable.sa13_2, R.drawable.sa13_3, R.drawable.sa13_4, R.drawable.sa13_5)
            ),
        Product(3, "Nike Pegasus 41", 2929000.0, 4.2f, R.drawable.sb13_1, "Chinh phục mọi hành trình với Nike Air Zoom Pegasus 37. Đệm Zoom Air cải tiến mang lại độ phản hồi tối ưu, trong khi lớp foam nhẹ giúp bạn tăng tốc mượt mà. Thoáng khí, ôm chân, hoàn hảo cho chạy bộ và phong cách hàng ngày.", reviews = listOf(
            Review("Test User", 5f, "Loved it! Amazing product. Can’t go wrong with this one. 100% recommend.", "5/15/2022", R.drawable.s1),
            Review("Test User", 5f, "Loved it! Amazing product. Can’t go wrong with this one. 100% recommend.", "5/15/2022", R.drawable.s2),
            Review("Test User", 5f, "Loved it! Amazing product. Can’t go wrong with this one. 100% recommend.", "5/15/2022", R.drawable.s3),
            Review("Alex Smith", 4.5f, "Great for running!", "6/20/2022", R.drawable.s4)
        ),
            images = listOf(R.drawable.sb13_1, R.drawable.sb13_2, R.drawable.sb13_3, R.drawable.sb13_4, R.drawable.sb13_5)
            ),
        Product(4, "Nike P-6000", 2929000.0, 4.3f, R.drawable.sn16_1, "Chinh phục mọi hành trình với Nike Air Zoom Pegasus 37. Đệm Zoom Air cải tiến mang lại độ phản hồi tối ưu, trong khi lớp foam nhẹ giúp bạn tăng tốc mượt mà. Thoáng khí, ôm chân, hoàn hảo cho chạy bộ và phong cách hàng ngày.", reviews = listOf(
            Review("Emma Watson", 4f, "Stylish and comfy!", "7/25/2022", R.drawable.s1)
        ),
            images = listOf(R.drawable.sn16_1, R.drawable.sn16_2, R.drawable.sn16_3, R.drawable.sn16_4, R.drawable.sn16_5)
            )
    )

    // Tìm sản phẩm theo productId
    val product = products.find { it.id == productId } ?: products[0] // Mặc định lấy sản phẩm đầu tiên nếu không tìm thấy

    // Tính số sao trung bình từ danh sách đánh giá
    val averageRating = if (product.reviews.isNotEmpty()) {
        product.reviews.map { it.rating }.average().toFloat()
    } else {
        0f
    }

    // Danh sách ảnh phụ (giả lập, bạn có thể thay bằng danh sách thực tế từ dữ liệu sản phẩm)
    val productImages = product.images

    // Trạng thái để theo dõi ảnh phụ được chọn
    var selectedImage by remember { mutableStateOf(productImages[0]) }


    val numberFormat = remember {
        NumberFormat.getNumberInstance(Locale("vi", "VN")).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 0
        }
    }

    // Trạng thái cho kích cỡ được chọn và hiển thị popup
    var selectedSize by remember { mutableStateOf<Int?>(null) }
    var showSizePopup by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // ModalBottomSheet
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded }
    )

    // Trạng thái hiển thị popup hướng dẫn chọn size
    var showSizeGuidePopup by remember { mutableStateOf(false) }

    // Trạng thái hiển thị toàn bộ đánh giá
    var showAllReviews by remember { mutableStateOf(false) }

    // Xử lý hiển thị/ẩn popup
    LaunchedEffect(showSizePopup) {
        if (showSizePopup) {
            coroutineScope.launch { sheetState.show() }
        } else {
            coroutineScope.launch { sheetState.hide() }
        }
    }
    

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            SizeSelectionPopup(
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
                    title = { Text(product.name) },
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
                    actions = {
//                        IconButton(onClick = { /* Xử lý tìm kiếm */ }) {
//                            Icon(
//                                imageVector = Icons.Default.Search,
//                                contentDescription = "Search",
//                                tint = Color.Black,
//                                modifier = Modifier.padding(end = 8.dp)
//                            )
//                        }
                    },
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp
                )
            },
            bottomBar = { BottomNavigationBar(navController = navController) }
        ) { paddingValues ->
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                item {
                    Column(modifier = Modifier.padding(0.dp)) {

                        // Hình ảnh chính chiếm 2/3 màn hình
                        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
                        val imageHeight = screenHeight * 2 / 3 // Chiếm 2/3 chiều cao màn hình

                        // Chọn ảnh để hiển thị: nếu selectedImage không null thì dùng nó, nếu không thì dùng product.imageUrl
                        val displayImage = selectedImage ?: product.imageUrl

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(imageHeight)
                                .background(Color(0xFFF6F6F6)) // Màu nền cho hình ảnh
                        ) {
                            Image(
                                painter = painterResource(id = displayImage),
                                contentDescription = product.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(imageHeight)
                                    .statusBarsPadding(), // Đảm bảo hình ảnh bắt đầu từ thanh trạng thái
                                contentScale = ContentScale.Fit // Đảm bảo hình ảnh không bị cắt
                            )
                        }

                        // Danh sách ảnh phụ
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(productImages) { imageRes ->
                                val isSelected = imageRes == selectedImage
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF6F6F6))
                                            .clickable {
                                                selectedImage = imageRes
                                            }
                                    ) {
                                        Image(
                                            painter = painterResource(id = imageRes),
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

                        // Nội dung còn lại
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                text = product.name,
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
                                    text = "${String.format("%.1f", averageRating)} (${product.reviews.size} Đánh giá)",
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${numberFormat.format(product.price)} đ",
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
                                    containerColor = Color.White,
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
                                onClick = { /* Add to cart logic */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Mua ngay", color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { /* Add to cart logic */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .border(1.dp, Color.Gray, RoundedCornerShape(20.dp)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Black
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text("Thêm vào giỏ ", color = Color.Black)
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
                                text = "MÔ TẢ SẢN PHẨM ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = product.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Light
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = product.description, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(40.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "ĐÁNH GIÁ SẢN PHẨM ",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (product.reviews.isNotEmpty()) {
                                    IconButton(onClick = { showAllReviews = !showAllReviews }) {
                                        Icon(
                                            imageVector = if (showAllReviews) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Toggle Reviews",
                                            tint = Color.Black
                                        )
                                    }
                                }
                            }
                            if (product.reviews.isEmpty()) {
                                Text(
                                    text = "Chưa có đánh giá",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                val reviewsToShow = if (showAllReviews) product.reviews else product.reviews.take(1)
                                reviewsToShow.forEach { review ->
                                    ReviewItem(review = review)
                                }
                            }

                            Spacer(modifier = Modifier.height(40.dp))
                            Text(
                                text = "BẠN CÓ THỂ THÍCH ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Column(modifier = Modifier.padding(0.dp)) {
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    items(products.size) { index ->
                                        ProductItem(
                                            product = products[index],
                                            onClick = { navController.navigate("product/${products[index].id}") }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }
    }
    // Popup hướng dẫn chọn size
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
                // Trạng thái để kích hoạt reset zoom
                var resetZoomKey by remember { mutableStateOf(0) }

                // Nút quay lại và nút reset zoom
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
                        resetZoomKey++ // Tăng key để kích hoạt reset zoom
                    }) {
                        Text("Reset Zoom", color = Color.Black)
                    }
                }

                // Hình ảnh hướng dẫn chọn size với tính năng zoom
                ZoomableImage(resetZoomKey = resetZoomKey)
            }
        }
    }
}

@Composable
fun ZoomableImage(resetZoomKey: Int) {
    // Trạng thái để theo dõi mức zoom (scale) và vị trí di chuyển (offset)
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // Lấy kích thước màn hình
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // Giả sử kích thước ảnh gốc (có thể thay đổi dựa trên ảnh thực tế)
    val imageWidth = screenWidth.value
    val imageHeight = screenHeight.value * 0.8f

    // Reset zoom khi key thay đổi
    LaunchedEffect(resetZoomKey) {
        scale = 1f
        offsetX = 0f
        offsetY = 0f
    }

    Box(
        modifier = Modifier
            .fillMaxSize() // Thay vì dùng weight, sử dụng fillMaxSize để tránh lỗi ColumnScope
            .clipToBounds()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // Cập nhật mức zoom, giới hạn từ 1x đến 4x
                    scale = (scale * zoom).coerceIn(1f, 4f)

                    // Tính toán kích thước ảnh sau khi zoom
                    val scaledWidth = imageWidth * scale
                    val scaledHeight = imageHeight * scale

                    // Tính giới hạn di chuyển
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

                    // Cập nhật vị trí di chuyển
                    offsetX += pan.x
                    offsetY += pan.y

                    // Giới hạn di chuyển để ảnh không ra ngoài vùng hiển thị
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
    onSizeSelected: (Int) -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // Chiếm khoảng 2/3 màn hình
            .padding(16.dp)
    ) {
        // Nút Cancel
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
        // Danh sách kích cỡ
        LazyColumn {
            items((38..47).toList()) { size ->
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
            // Avatar bên trái
            Image(
                painter = painterResource(id = review.imageAvatar),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            // Nội dung đánh giá bên phải
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
fun ProductItem(product: Product, onClick: () -> Unit) {
    val numberFormat = remember {
        NumberFormat.getNumberInstance(Locale("vi", "VN")).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 0
        }
    }
    Card(
        modifier = Modifier
            .padding(0.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Image(
                    painter = painterResource(id = product.imageUrl),
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF6F6F6))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material.Text(
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
                text = "${numberFormat.format(product.price)} đ",
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
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
                    androidx.compose.material.Icon(
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
                    androidx.compose.material.Icon(
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
                    androidx.compose.material.Icon(
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