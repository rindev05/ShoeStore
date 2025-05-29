package com.example.shoestore.ui.screens.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoestore.R
import com.example.shoestore.ui.screens.home.saveToRecentlyViewed
import com.example.shoestore.ui.screens.search.BottomNavigationBar
import com.example.shoestore.ui.screens.search.ProductItem
import com.example.shoestore.ui.screens.search.SearchPopup
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


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
            Text("Our Bestsellers", style = MaterialTheme.typography.h5, modifier = Modifier.padding(start = 5.dp))
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
fun ProductListScreen(navController: NavController) {
    val productsBitis = remember { mutableStateListOf<com.example.shoestore.ui.screens.search.Product>() }
    val productsNike = remember { mutableStateListOf<com.example.shoestore.ui.screens.search.Product>() }
    val productsAdidas = remember { mutableStateListOf<com.example.shoestore.ui.screens.search.Product>() }
    val allProducts = remember { mutableStateListOf<com.example.shoestore.ui.screens.search.Product>() }
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
            AppBar(onSearchClick = {
                showSearchPopup = true
            })
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
//                BannerSection()
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