package com.example.shoestore.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shoestore.R
import com.google.accompanist.pager.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.Locale
import kotlin.random.Random
import com.example.shoestore.ui.screens.search.SearchPopup

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val brand: String,
    val imageUrl: String = "" // Thêm mặc định để tương thích với dữ liệu từ Firestore
)

@Composable
fun AppBar(onSearchClick: () -> Unit) {
    TopAppBar(
        backgroundColor = Color.Transparent,
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
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onSearchClick() }
            )
        }
    }
}

// Composable mới để hiển thị giao diện Splash mà không có logic điều hướng
@Composable
fun SplashLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.shoe_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(180.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    var showSearchPopup by remember { mutableStateOf(false) }
    var bestsellers by remember { mutableStateOf<List<Product>>(emptyList()) }
    var recentlyViewed by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val productsBitis = remember { mutableStateListOf<com.example.shoestore.ui.screens.search.Product>() }
    val productsNike = remember { mutableStateListOf<com.example.shoestore.ui.screens.search.Product>() }
    val productsAdidas = remember { mutableStateListOf<com.example.shoestore.ui.screens.search.Product>() }
    val allProducts_search = remember { mutableStateListOf<com.example.shoestore.ui.screens.search.Product>() }

    // Tải dữ liệu từ Firestore cho Bestsellers và Recently Viewed
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val collections = listOf("products-bitis", "products-nike", "products-adidas")
        val allProducts = mutableListOf<Product>()

        // Tìm kiếm sản phẩm
        val collections1 = mapOf(
            "products-bitis" to productsBitis,
            "products-nike" to productsNike,
            "products-adidas" to productsAdidas
        )

        collections1.forEach { (collectionName, productList) ->
            try {
                val result = db.collection(collectionName).get().await()
                val productListData = result.documents.mapNotNull { document ->
                    val data = document.data
                    println("Document data from $collectionName: $data")
                    data?.let {
                        com.example.shoestore.ui.screens.search.Product(
                            id = (it["id"] as? Long)?.toInt() ?: 0,
                            name = it["name"] as? String ?: "",
                            price = (it["price"] as? Number)?.toDouble() ?: 0.0,
                            brand = it["brand"] as? String ?: "",
                            imageUrl = it["imageUrl"] as? String ?: ""
                        )
                    }
                }
                productList.clear()
                productList.addAll(productListData)
            } catch (e: Exception) {
                println("Error getting products from $collectionName: $e")
            }
        }
        // Cập nhật allProducts bằng cách gộp và xáo trộn
        allProducts_search.clear()
        allProducts_search.addAll(productsBitis)
        allProducts_search.addAll(productsNike)
        allProducts_search.addAll(productsAdidas)
        allProducts_search.shuffle()

        // Tải dữ liệu từ 3 collections cho Bestsellers
        for (collection in collections) {
            try {
                val snapshot = db.collection(collection).get().await()
                val products = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data
                    data?.let {
                        Product(
                            id = (it["id"] as? Long)?.toInt() ?: 0,
                            name = it["name"] as? String ?: "",
                            price = (it["price"] as? Number)?.toDouble() ?: 0.0,
                            brand = it["brand"] as? String ?: "",
                            imageUrl = it["imageUrl"] as? String ?: ""
                        )
                    }
                }
                allProducts.addAll(products)
            } catch (e: Exception) {
                println("Error loading products from $collection: $e")
            }
        }

        // Lấy ngẫu nhiên 6 sản phẩm cho Bestsellers
        bestsellers = if (allProducts.size >= 6) {
            allProducts.shuffled(Random(System.currentTimeMillis())).take(6)
        } else {
            allProducts.shuffled(Random(System.currentTimeMillis()))
        }

        // Tải dữ liệu Recently Viewed cho user hiện tại
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            try {
                val userDoc = db.collection("users").document(user.uid).get().await()
                val recentlyViewedIds = userDoc.get("recentlyViewed") as? List<Long> ?: emptyList()

                // Lấy thông tin sản phẩm từ các collections dựa trên danh sách recentlyViewedIds
                val recentlyViewedProducts = mutableListOf<Product>()
                for (id in recentlyViewedIds) {
                    for (collection in collections) {
                        val productSnapshot = db.collection(collection)
                            .whereEqualTo("id", id)
                            .get()
                            .await()
                        if (productSnapshot.documents.isNotEmpty()) {
                            val doc = productSnapshot.documents.first()
                            val data = doc.data
                            val product = data?.let {
                                Product(
                                    id = (it["id"] as? Long)?.toInt() ?: 0,
                                    name = it["name"] as? String ?: "",
                                    price = (it["price"] as? Number)?.toDouble() ?: 0.0,
                                    brand = it["brand"] as? String ?: "",
                                    imageUrl = it["imageUrl"] as? String ?: ""
                                )
                            }
                            if (product != null) {
                                recentlyViewedProducts.add(product)
                                break
                            }
                        }
                    }
                }
                // Hiển thị theo thứ tự mới nhất ở bên phải
                recentlyViewed = recentlyViewedProducts.reversed().take(10)
            } catch (e: Exception) {
                println("Error loading recently viewed: $e")
            }
        }
        isLoading = false
    }

    if (isLoading) {
        SplashLoadingScreen()
        return
    }

    Scaffold(
        topBar = {
            AppBar(onSearchClick = { showSearchPopup = true })
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { BannerSection() }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { ShopByBrandSection() }
                item { Spacer(modifier = Modifier.height(5.dp)) }
                item { BestsellersSection(navController, bestsellers) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { RecentlyViewedSection(navController, recentlyViewed) }
            }
        }

        if (showSearchPopup) {
            SearchPopup(
                allProducts = allProducts_search,
                navController = navController,
                onDismiss = { showSearchPopup = false }
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BannerSection() {
    val pagerState = rememberPagerState()
    val images = listOf(
        R.drawable.banner2,
        R.drawable.banner1,
        R.drawable.banner3,
        R.drawable.banner4,
        R.drawable.banner5,
        R.drawable.banner7,
        R.drawable.banner8
    )

    Column(modifier = Modifier.fillMaxWidth().padding(5.dp)) {
        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Banner Image $page",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp),
            activeColor = Color.Black
        )
    }
}

@Composable
fun ShopByBrandSection() {
    val brandLogos = listOf(
        R.drawable.logo_nike,
        R.drawable.logo_adidas,
        R.drawable.logo_bitis,
        R.drawable.logo_converse,
        R.drawable.logo_new_banlance,
        R.drawable.logo_puma
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Shop By Brand", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow {
            items(brandLogos.size) { index ->
                BrandItem(logoResId = brandLogos[index])
            }
        }
    }
}

@Composable
fun BrandItem(logoResId: Int) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = logoResId),
            contentDescription = "Brand Logo",
            modifier = Modifier.size(80.dp)
        )
    }
}

@Composable
fun BestsellersSection(navController: NavController, products: List<Product>) {
    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Our Bestsellers",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(start = 5.dp)
            )
            TextButton(onClick = {
                navController.navigate("productList")
            }) {
                Text(text = "View All", color = Color.Black, modifier = Modifier.padding(end = 5.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Hiển thị 6 sản phẩm trong 2 hàng, mỗi hàng 3 sản phẩm
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                products.take(3).forEach { product ->
                    ProductItem(product = product, onClick = {
                        navController.navigate("product/${product.id}")
                        // Lưu vào recently viewed khi click vào sản phẩm
                        saveToRecentlyViewed(product.id)
                    })
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                products.drop(3).forEach { product ->
                    ProductItem(product = product, onClick = {
                        navController.navigate("product/${product.id}")
                        // Lưu vào recently viewed khi click vào sản phẩm
                        saveToRecentlyViewed(product.id)
                    })
                }
            }
        }
    }
}

@Composable
fun RecentlyViewedSection(navController: NavController, recentlyViewed: List<Product>) {
    if (recentlyViewed.isEmpty()) return

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Recently Viewed", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            reverseLayout = true // Đảo chiều hiển thị từ phải sang trái
        ) {
            items(recentlyViewed.size) { index ->
                ProductItem(
                    product = recentlyViewed[index],
                    onClick = {
                        navController.navigate("product/${recentlyViewed[index].id}")
                        // Lưu lại vào recently viewed khi click
                        saveToRecentlyViewed(recentlyViewed[index].id)
                    }
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

    Box(
        modifier = Modifier
            .width(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (product.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image", color = Color.White, fontSize = 12.sp)
                }
            }
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = product.brand,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = "${numberFormat.format(product.price)} đ",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(4.dp)
            )
        }
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

// Hàm lưu sản phẩm vào danh sách recently viewed của user
fun saveToRecentlyViewed(productId: Int) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(user.uid)

        // Cập nhật danh sách recentlyViewed, thêm productId vào đầu và giới hạn 10 phần tử
        userRef.update("recentlyViewed", FieldValue.arrayUnion(productId.toLong()))
            .addOnSuccessListener {
                userRef.get().addOnSuccessListener { document ->
                    val currentList = document.get("recentlyViewed") as? List<Long> ?: emptyList()
                    if (currentList.size > 10) {
                        val trimmedList = currentList.takeLast(10) // Giữ 10 phần tử cuối, xóa phần tử đầu
                        userRef.update("recentlyViewed", trimmedList)
                    }
                }
            }
            .addOnFailureListener { e ->
                println("Error saving to recently viewed: $e")
            }
    }
}