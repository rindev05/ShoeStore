package com.example.shoestore.ui.screens.list

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoestore.R
import com.example.shoestore.data.model.Product
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductListScreen(navController: NavController) {

    // State để kiểm soát hiển thị popup tìm kiếm
    var showSearchPopup by remember { mutableStateOf(false) }

    val products = listOf(
        Product(1, "Nike Dunk Low Retro", 2929000.0, 4.5f, R.drawable.sa12_1, "Men's Shoes"),
        Product(2, "Nike Pegasus Plus", 5279000.0, 4.0f, R.drawable.sa13_1, "Men's Road Running Shoes"),
        Product(3, "Nike Pegasus 41", 2929000.0, 4.2f, R.drawable.sb13_1, "Men's Road Running Shoes"),
        Product(4, "Nike P-6000", 2929000.0, 4.3f, R.drawable.sn16_1, "Shoes")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Our Bestsellers") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showSearchPopup = true }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                },
                backgroundColor = Color.White
            )
        },
        bottomBar = {
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
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Thanh lọc (Filter Tabs) với ScrollableTabRow
            val tabList = listOf(
                "All", "Adidas", "Nike", "Biti's", "Converse", "New Balance", "Puma"
            )
            var selectedTabIndex by remember { mutableStateOf(0) }

            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                backgroundColor = Color.White,
                contentColor = Color.Black,
                edgePadding = 8.dp, // Khoảng cách hai bên để trông đẹp hơn
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
                        modifier = Modifier
                            .padding(horizontal = 8.dp), // Khoảng cách giữa các tab
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

            // Danh sách sản phẩm
            LazyColumn {
                items(products.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { product ->
                            ProductItem(product = product, onClick = { navController.navigate("product/${product.id}") })
                        }
                        // Điền khoảng trống nếu hàng không đủ 2 mục
                        if (rowItems.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        // Hiển thị popup tìm kiếm khi showSearchPopup = true
        if (showSearchPopup) {
            SearchPopup(onDismiss = { showSearchPopup = false })
        }
    }
}

// Định nghĩa SearchPopup (giữ nguyên từ code bạn cung cấp)
@Composable
fun SearchPopup(onDismiss: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

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
            Text(
                text = "Search Suggestions or Results",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
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
//                Icon(
//
//                    imageVector = Icons.Default.FavoriteBorder,
//                    contentDescription = "Favorite",
//                    modifier = Modifier
//                        .align(Alignment.TopEnd)
//                        .padding(8.dp)
//                        .background(Color.White, shape = CircleShape)
//                        .padding(4.dp)
//                        .clickable { /* Handle favorite */ },
//                    tint = Color.Gray
//
//                )
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
                text = product.description,
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
