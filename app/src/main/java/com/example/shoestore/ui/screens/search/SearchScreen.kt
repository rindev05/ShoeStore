package com.example.shoestore.ui.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoestore.R
import com.google.accompanist.pager.*
import com.google.firebase.firestore.FirebaseFirestore
import coil.compose.AsyncImage
import com.example.shoestore.ui.screens.home.saveToRecentlyViewed
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.Locale

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val brand: String,
    val imageUrl: String
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

@Composable
fun SearchPopup(
    allProducts: List<Product>,
    navController: NavController,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Lọc sản phẩm dựa trên searchQuery
    val filteredProducts = allProducts.filter { product ->
        product.name.lowercase().contains(searchQuery.lowercase()) ||
                product.brand.lowercase().contains(searchQuery.lowercase())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    focusManager.clearFocus()
                    onDismiss()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    placeholder = { Text("Search for shoes...") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        focusManager.clearFocus()
                    }),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Hiển thị kết quả tìm kiếm
            if (searchQuery.isEmpty()) {
                Text(
                    text = "Search Suggestions or Results",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (filteredProducts.isEmpty()) {
                Text(
                    text = "No results found for \"$searchQuery\"",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn {
                    items(filteredProducts.chunked(2)) { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            rowItems.forEach { product ->
                                ProductItem(
                                    product = product,
                                    onClick = {
                                        navController.navigate("product/${product.id}")
                                        saveToRecentlyViewed(product.id)
                                        onDismiss() // Đóng popup sau khi chọn sản phẩm
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                )
                            }
                            if (rowItems.size < 2) {
                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun SearchScreen(navController: NavController) {
    val productsBitis = remember { mutableStateListOf<Product>() }
    val productsNike = remember { mutableStateListOf<Product>() }
    val productsAdidas = remember { mutableStateListOf<Product>() }
    val allProducts = remember { mutableStateListOf<Product>() }
    val scope = rememberCoroutineScope()
    var showSearchPopup by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }

    // Load dữ liệu từ 3 collection
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val collections = mapOf(
            "products-bitis" to productsBitis,
            "products-nike" to productsNike,
            "products-adidas" to productsAdidas
        )

        collections.forEach { (collectionName, productList) ->
            try {
                val result = db.collection(collectionName).get().await()
                val productListData = result.documents.mapNotNull { document ->
                    val data = document.data
                    println("Document data from $collectionName: $data")
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
                productList.clear()
                productList.addAll(productListData)
            } catch (e: Exception) {
                println("Error getting products from $collectionName: $e")
            }
        }

        // Cập nhật allProducts bằng cách gộp và xáo trộn
        allProducts.clear()
        allProducts.addAll(productsBitis)
        allProducts.addAll(productsNike)
        allProducts.addAll(productsAdidas)
        allProducts.shuffle()
    }

    // Xác định danh sách hiển thị dựa trên tab được chọn
    val displayedProducts = when (selectedTabIndex) {
        0 -> allProducts // All
        1 -> productsAdidas // Adidas
        2 -> productsNike // Nike
        3 -> productsBitis // Biti's
        else -> allProducts
    }

    Scaffold(
        topBar = {
            AppBar(onSearchClick = { showSearchPopup = true })
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Spacer(modifier = Modifier.height(5.dp))
                BannerSection()
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Thanh lọc (Filter Tabs) với ScrollableTabRow
            item {
                val tabList = listOf("All", "Adidas", "Nike", "Biti's")
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    backgroundColor = Color.White,
                    contentColor = Color.Black,
                    edgePadding = 8.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color.Black
                        )
                    }
                ) {
                    tabList.forEachIndexed { index, tabTitle ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            modifier = Modifier.padding(horizontal = 8.dp),
                            text = {
                                Text(
                                    text = tabTitle,
                                    fontSize = 14.sp,
                                    color = if (selectedTabIndex == index) Color.Black else Color.Gray
                                )
                            }
                        )
                    }
                }
            }

            // Danh sách sản phẩm: mỗi hàng 2 sản phẩm
            items(displayedProducts.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowItems.forEach { product ->
                        ProductItem(
                            product = product,
                            onClick = {
                                navController.navigate("product/${product.id}")
                                // Lưu vào recently viewed khi click vào sản phẩm
                                saveToRecentlyViewed(product.id)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                        )
                    }
                    if (rowItems.size < 2) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }

        if (showSearchPopup) {
            SearchPopup(
                allProducts = allProducts,
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
        R.drawable.banner3,
        R.drawable.banner2,
        R.drawable.banner1,
        R.drawable.banner4,
        R.drawable.banner5,
        R.drawable.banner7,
        R.drawable.banner8
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
    ) {
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