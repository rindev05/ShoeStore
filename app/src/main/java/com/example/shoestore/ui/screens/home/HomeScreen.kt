package com.example.shoestore.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.example.shoestore.R
import com.example.shoestore.data.model.Product
import java.text.NumberFormat
import java.util.Locale

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
                modifier = Modifier.size(120.dp) // Điều chỉnh kích thước logo nếu cần
            )
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onSearchClick() } // Gọi hàm mở popup khi nhấn
            )
        }
    }
}

@Composable
fun SearchPopup(onDismiss: () -> Unit) {
    // State để quản lý nội dung tìm kiếm
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Box để tạo popup toàn màn hình
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
                // Nút quay lại
                IconButton(onClick = {
                    focusManager.clearFocus() // Đóng bàn phím
                    onDismiss() // Đóng popup
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                // Thanh tìm kiếm
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
//                        .clip(RoundedCornerShape(50.dp)),
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
                        // Xử lý tìm kiếm (có thể thêm logic ở đây)
                        focusManager.clearFocus()
                    }),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }
            // Có thể thêm nội dung khác cho popup, như gợi ý tìm kiếm
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Search Suggestions or Results",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

    // Tự động mở bàn phím khi popup xuất hiện
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    // State để kiểm soát hiển thị popup
    var showSearchPopup by remember { mutableStateOf(false) }

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
                item { BestsellersSection(navController) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { RecentlyViewedSection(navController) }
            }
        }

        // Hiển thị popup tìm kiếm khi showSearchPopup = true
        if (showSearchPopup) {
            SearchPopup(onDismiss = { showSearchPopup = false })
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

    Column (modifier = Modifier.fillMaxWidth().padding(5.dp))
    {
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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BestsellersSection(navController: NavController) {

    // Sử dụng danh sách Product thay vì danh sách imageResId
    val products = listOf(
        Product(1, "Nike Dunk Low Retro", 2929000.0, 4.5f, R.drawable.sa12_1, "Men's Shoes"),
        Product(2, "Nike Pegasus Plus", 5279000.0, 4.0f, R.drawable.sa13_1, "Men's Road Running Shoes"),
        Product(3, "Nike Pegasus 41", 2929000.0, 4.2f, R.drawable.sb13_1, "Men's Road Running Shoes"),
        Product(4, "Nike P-6000", 2929000.0, 4.3f, R.drawable.sn16_1, "Shoes"),
        Product(1, "Nike Dunk Low Retro", 2929000.0, 4.5f, R.drawable.sa12_1, "Men's Shoes"),
        Product(2, "Nike Pegasus Plus", 5279000.0, 4.0f, R.drawable.sa13_1, "Men's Road Running Shoes")
    )

    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Our Bestsellers", style = MaterialTheme.typography.h6, modifier = Modifier.padding(start = 5.dp))
            TextButton(onClick = {
                navController.navigate("productList")
            }) {
                Text(text = "View All", color = Color.Black, modifier = Modifier.padding(end = 5.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Hiển thị 6 sản phẩm trong 2 hàng và 3 cột
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { // Giảm khoảng cách giữa các hàng
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp) // Giảm khoảng cách giữa các sản phẩm
            ) {
                products.take(3).forEach { product ->
                    ProductItem(product = product, onClick = { navController.navigate("product/${product.id}") })
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp) // Giảm khoảng cách giữa các sản phẩm
            ) {
                products.drop(3).forEach { product ->
                    ProductItem(product = product, onClick = { navController.navigate("product/${product.id}") })
                }
            }
        }
    }
}


@Composable
fun RecentlyViewedSection(navController: NavController) {
    // Sử dụng danh sách Product thay vì danh sách imageResId
    val recentlyViewed = listOf(
        Product(3, "Nike Pegasus 41", 2929000.0, 4.2f, R.drawable.sb13_1, "Men's Road Running Shoes"),
        Product(4, "Nike P-6000", 2929000.0, 4.3f, R.drawable.sn16_1, "Shoes"),
        Product(1, "Nike Dunk Low Retro", 2929000.0, 4.5f, R.drawable.sa12_1, "Men's Shoes"),
        Product(2, "Nike Pegasus Plus", 5279000.0, 4.0f, R.drawable.sa13_1, "Men's Road Running Shoes")
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Recently Viewed", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(recentlyViewed.size) { index ->
                ProductItem(
                    product = recentlyViewed[index],
                    onClick = { navController.navigate("product/${recentlyViewed[index].id}") }
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
            .clickable(onClick = onClick) // Thêm sự kiện nhấp
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = product.imageUrl),
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(4.dp))
            Text(text = "${numberFormat.format(product.price)} đ", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(4.dp))
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
            alwaysShowLabel = false // Không hiển thị text
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