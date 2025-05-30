package com.example.shoestore.ui.screens.admin.product

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shoestore.data.service.CloudinaryService
import com.example.shoestore.ui.theme.ShoeStoreTheme
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.*
import kotlin.random.Random

// Đổi id thành Int để đồng bộ với ProductDetailScreen
data class Product(
    val id: Int = 0, // Đổi từ String sang Int
    val brand: String = "",
    val description: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val size: List<Int> = emptyList(),
    val imageUrl: String = "",
    val images: List<String> = emptyList(),
    val collection: String = ""
)

// Hàm tạo ID ngẫu nhiên và kiểm tra trùng lặp
suspend fun generateUniqueProductId(collection: String): Int {
    val db = FirebaseFirestore.getInstance()
    var newId: Int
    do {
        // Tạo số ngẫu nhiên từ 10000 đến 99999 (5 chữ số)
        newId = Random.nextInt(10000, 100000)
        val snapshot = db.collection(collection)
            .whereEqualTo("id", newId)
            .get()
            .await()
    } while (!snapshot.isEmpty) // Lặp lại nếu ID đã tồn tại
    return newId
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductManagementScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var bitisProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var adidasProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var nikeProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val cloudinaryService = CloudinaryService.getInstance(context)

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val bitisSnapshot = db.collection("products-bitis").get().await()
                bitisProducts = bitisSnapshot.documents.mapNotNull { doc ->
                    val data = doc.data
                    data?.let {
                        Product(
                            id = (it["id"] as? Number)?.toInt() ?: 0, // Đổi từ String sang Int
                            brand = it["brand"] as? String ?: "",
                            description = it["description"] as? String ?: "",
                            name = it["name"] as? String ?: "",
                            price = (it["price"] as? Number)?.toDouble() ?: 0.0,
                            size = (it["size"] as? List<*>)?.mapNotNull { size -> (size as? Number)?.toInt() } ?: emptyList(),
                            imageUrl = it["imageUrl"] as? String ?: "",
                            images = (it["images"] as? List<*>)?.mapNotNull { img -> img as? String } ?: emptyList(),
                            collection = "products-bitis"
                        )
                    }
                }

                val adidasSnapshot = db.collection("products-adidas").get().await()
                adidasProducts = adidasSnapshot.documents.mapNotNull { doc ->
                    val data = doc.data
                    data?.let {
                        Product(
                            id = (it["id"] as? Number)?.toInt() ?: 0,
                            brand = it["brand"] as? String ?: "",
                            description = it["description"] as? String ?: "",
                            name = it["name"] as? String ?: "",
                            price = (it["price"] as? Number)?.toDouble() ?: 0.0,
                            size = (it["size"] as? List<*>)?.mapNotNull { size -> (size as? Number)?.toInt() } ?: emptyList(),
                            imageUrl = it["imageUrl"] as? String ?: "",
                            images = (it["images"] as? List<*>)?.mapNotNull { img -> img as? String } ?: emptyList(),
                            collection = "products-adidas"
                        )
                    }
                }

                val nikeSnapshot = db.collection("products-nike").get().await()
                nikeProducts = nikeSnapshot.documents.mapNotNull { doc ->
                    val data = doc.data
                    data?.let {
                        Product(
                            id = (it["id"] as? Number)?.toInt() ?: 0,
                            brand = it["brand"] as? String ?: "",
                            description = it["description"] as? String ?: "",
                            name = it["name"] as? String ?: "",
                            price = (it["price"] as? Number)?.toDouble() ?: 0.0,
                            size = (it["size"] as? List<*>)?.mapNotNull { size -> (size as? Number)?.toInt() } ?: emptyList(),
                            imageUrl = it["imageUrl"] as? String ?: "",
                            images = (it["images"] as? List<*>)?.mapNotNull { img -> img as? String } ?: emptyList(),
                            collection = "products-nike"
                        )
                    }
                }
            } catch (e: Exception) {
                println("Error fetching products: ${e.message}")
            }
        }
    }

    val filteredBitisProducts = remember(searchQuery, bitisProducts) {
        if (searchQuery.isEmpty()) bitisProducts
        else bitisProducts.filter { it.name.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true) }
    }
    val filteredAdidasProducts = remember(searchQuery, adidasProducts) {
        if (searchQuery.isEmpty()) adidasProducts
        else adidasProducts.filter { it.name.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true) }
    }
    val filteredNikeProducts = remember(searchQuery, nikeProducts) {
        if (searchQuery.isEmpty()) nikeProducts
        else nikeProducts.filter { it.name.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true) }
    }

    ShoeStoreTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Quản lý sản phẩm", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1C2526))
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Color(0xFF2196F3)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product", tint = Color.White)
                }
            },
            bottomBar = { BottomNavigationBar1(navController = navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1C2526))
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Tìm kiếm sản phẩm", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                LazyColumn {
                    item {
                        Text("Giày Bitis", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (filteredBitisProducts.isEmpty()) {
                        item {
                            Text("Không có sản phẩm nào", color = Color.Gray, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    } else {
                        items(filteredBitisProducts) { product ->
                            ProductItemRow(product, onEdit = {
                                selectedProduct = product
                                showEditDialog = true
                            }, onDelete = {
                                coroutineScope.launch {
                                    try {
                                        // Tìm tài liệu có trường "id" khớp với product.id
                                        val snapshot = db.collection("products-bitis")
                                            .whereEqualTo("id", product.id)
                                            .get()
                                            .await()
                                        if (!snapshot.isEmpty) {
                                            val docId = snapshot.documents.first().id
                                            db.collection("products-bitis").document(docId).delete().await()
                                            bitisProducts = bitisProducts.filter { it.id != product.id }
                                        }
                                    } catch (e: Exception) {
                                        println("Error deleting product: ${e.message}")
                                    }
                                }
                            })
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Giày Adidas", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (filteredAdidasProducts.isEmpty()) {
                        item {
                            Text("Không có sản phẩm nào", color = Color.Gray, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    } else {
                        items(filteredAdidasProducts) { product ->
                            ProductItemRow(product, onEdit = {
                                selectedProduct = product
                                showEditDialog = true
                            }, onDelete = {
                                coroutineScope.launch {
                                    try {
                                        val snapshot = db.collection("products-adidas")
                                            .whereEqualTo("id", product.id)
                                            .get()
                                            .await()
                                        if (!snapshot.isEmpty) {
                                            val docId = snapshot.documents.first().id
                                            db.collection("products-adidas").document(docId).delete().await()
                                            adidasProducts = adidasProducts.filter { it.id != product.id }
                                        }
                                    } catch (e: Exception) {
                                        println("Error deleting product: ${e.message}")
                                    }
                                }
                            })
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Giày Nike", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (filteredNikeProducts.isEmpty()) {
                        item {
                            Text("Không có sản phẩm nào", color = Color.Gray, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    } else {
                        items(filteredNikeProducts) { product ->
                            ProductItemRow(product, onEdit = {
                                selectedProduct = product
                                showEditDialog = true
                            }, onDelete = {
                                coroutineScope.launch {
                                    try {
                                        val snapshot = db.collection("products-nike")
                                            .whereEqualTo("id", product.id)
                                            .get()
                                            .await()
                                        if (!snapshot.isEmpty) {
                                            val docId = snapshot.documents.first().id
                                            db.collection("products-nike").document(docId).delete().await()
                                            nikeProducts = nikeProducts.filter { it.id != product.id }
                                        }
                                    } catch (e: Exception) {
                                        println("Error deleting product: ${e.message}")
                                    }
                                }
                            })
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { newProduct ->
                coroutineScope.launch {
                    try {
                        // Tạo ID ngẫu nhiên
                        val newId = generateUniqueProductId(newProduct.collection)
                        // Sử dụng ID ngẫu nhiên làm document ID
                        val docRef = db.collection(newProduct.collection).document(newId.toString())
                        docRef.set(newProduct.copy(id = newId)).await()
                        when (newProduct.collection) {
                            "products-bitis" -> bitisProducts = bitisProducts + newProduct.copy(id = newId)
                            "products-adidas" -> adidasProducts = adidasProducts + newProduct.copy(id = newId)
                            "products-nike" -> nikeProducts = nikeProducts + newProduct.copy(id = newId)
                        }
                        showAddDialog = false
                    } catch (e: Exception) {
                        println("Error adding product: ${e.message}")
                    }
                }
            },
            cloudinaryService = cloudinaryService
        )
    }

    if (showEditDialog && selectedProduct != null) {
        EditProductDialog(
            product = selectedProduct!!,
            onDismiss = {
                showEditDialog = false
                selectedProduct = null
            },
            onSave = { updatedProduct ->
                coroutineScope.launch {
                    try {
                        // Tìm tài liệu có trường "id" khớp với updatedProduct.id
                        val snapshot = db.collection(updatedProduct.collection)
                            .whereEqualTo("id", updatedProduct.id)
                            .get()
                            .await()
                        if (!snapshot.isEmpty) {
                            val docId = snapshot.documents.first().id
                            db.collection(updatedProduct.collection).document(docId).set(updatedProduct).await()
                            when (updatedProduct.collection) {
                                "products-bitis" -> bitisProducts = bitisProducts.map { if (it.id == updatedProduct.id) updatedProduct else it }
                                "products-adidas" -> adidasProducts = adidasProducts.map { if (it.id == updatedProduct.id) updatedProduct else it }
                                "products-nike" -> nikeProducts = nikeProducts.map { if (it.id == updatedProduct.id) updatedProduct else it }
                            }
                        }
                        showEditDialog = false
                        selectedProduct = null
                    } catch (e: Exception) {
                        println("Error updating product: ${e.message}")
                    }
                }
            },
            cloudinaryService = cloudinaryService
        )
    }
}

@Composable
fun ProductItemRow(product: Product, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3B3C))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF6F6F6))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(product.price)} đ",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text("Thương hiệu: ${product.brand}", color = Color.Gray, fontSize = 14.sp)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2196F3))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAdd: (Product) -> Unit,
    cloudinaryService: CloudinaryService
) {
    var brand by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var mainImageUri by remember { mutableStateOf<Uri?>(null) }
    var subImageUris by remember { mutableStateOf<List<Uri?>>(List(6) { null }) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val mainImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        mainImageUri = uri
    }

    val subImageLaunchers = List(6) { index ->
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            subImageUris = subImageUris.toMutableList().apply { this[index] = uri }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3B3C))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                Text("Thêm sản phẩm", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                var expanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { /* Không cho phép nhập tay */ },
                        label = { Text("Thương hiệu", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.White,
                                modifier = Modifier.clickable { expanded = true }
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color(0xFF2E3B3C))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Bitis", color = Color.White) },
                            onClick = {
                                brand = "Bitis"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Adidas", color = Color.White) },
                            onClick = {
                                brand = "Adidas"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Nike", color = Color.White) },
                            onClick = {
                                brand = "Nike"
                                expanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên sản phẩm", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Giá", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = size,
                    onValueChange = { size = it },
                    label = { Text("Kích thước (VD: 38,39,40)", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { mainImageLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("Chọn ảnh chính", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (mainImageUri != null) {
                        AsyncImage(
                            model = mainImageUri,
                            contentDescription = "Main Image",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF6F6F6))
                        )
                    } else {
                        Text("Chưa chọn ảnh", color = Color.Red, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    Text("Chọn 6 ảnh phụ", color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    subImageUris.forEachIndexed { index, uri ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { subImageLaunchers[index].launch("image/*") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                            ) {
                                Text("Ảnh ${index + 1}", color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            if (uri != null) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Sub Image ${index + 1}",
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF6F6F6))
                                )
                            } else {
                                Text("Chưa chọn", color = Color.Red, fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Hủy", color = Color.Red)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (mainImageUri == null || subImageUris.any { it == null }) {
                                Toast.makeText(context, "Vui lòng chọn đầy đủ ảnh (1 chính, 6 phụ)", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            coroutineScope.launch {
                                try {
                                    val folder = when (brand) {
                                        "Bitis" -> "products-bitis"
                                        "Adidas" -> "products-adidas"
                                        "Nike" -> "products-nike"
                                        else -> ""
                                    }
                                    if (folder.isEmpty()) {
                                        Toast.makeText(context, "Thương hiệu không hợp lệ", Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }
                                    val mainUrl = cloudinaryService.uploadImage(mainImageUri!!, folder)
                                    val subUrls = subImageUris.map { it?.let { uri -> cloudinaryService.uploadImage(uri, folder) } ?: "" }
                                    val sizes = size.split(",").mapNotNull { it.trim().toIntOrNull() }
                                    onAdd(
                                        Product(
                                            id = 0, // Sẽ được cập nhật bởi generateUniqueProductId
                                            brand = brand,
                                            description = description,
                                            name = name,
                                            price = price.toDoubleOrNull() ?: 0.0,
                                            size = sizes,
                                            imageUrl = mainUrl,
                                            images = listOf(mainUrl) + subUrls,
                                            collection = folder
                                        )
                                    )
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Lỗi tải ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("Thêm", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun EditProductDialog(
    product: Product,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit,
    cloudinaryService: CloudinaryService
) {
    var brand by remember { mutableStateOf(product.brand) }
    var description by remember { mutableStateOf(product.description) }
    var name by remember { mutableStateOf(product.name) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var size by remember { mutableStateOf(product.size.joinToString(",")) }
    var mainImageUri by remember { mutableStateOf<Uri?>(null) }
    var subImageUris by remember { mutableStateOf<List<Uri?>>(List(6) { null }) }
    var mainImageUrl by remember { mutableStateOf(product.imageUrl) }
    var subImageUrls by remember { mutableStateOf(product.images.drop(1).take(6)) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val mainImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        mainImageUri = uri
    }

    val subImageLaunchers = List(6) { index ->
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            subImageUris = subImageUris.toMutableList().apply { this[index] = uri }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3B3C))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                Text("Chỉnh sửa sản phẩm", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                var expanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { /* Không cho phép nhập tay */ },
                        label = { Text("Thương hiệu", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.White,
                                modifier = Modifier.clickable { expanded = true }
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color(0xFF2E3B3C))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Bitis", color = Color.White) },
                            onClick = {
                                brand = "Bitis"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Adidas", color = Color.White) },
                            onClick = {
                                brand = "Adidas"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Nike", color = Color.White) },
                            onClick = {
                                brand = "Nike"
                                expanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên sản phẩm", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Giá", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = size,
                    onValueChange = { size = it },
                    label = { Text("Kích thước (VD: 38,39,40)", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { mainImageLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("Chọn ảnh chính", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    AsyncImage(
                        model = mainImageUri ?: mainImageUrl.takeIf { it.isNotEmpty() },
                        contentDescription = "Main Image",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF6F6F6))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    Text("Chọn 6 ảnh phụ", color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    subImageUris.forEachIndexed { index, uri ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { subImageLaunchers[index].launch("image/*") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                            ) {
                                Text("Ảnh ${index + 1}", color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            AsyncImage(
                                model = uri ?: subImageUrls.getOrNull(index)?.takeIf { it.isNotEmpty() },
                                contentDescription = "Sub Image ${index + 1}",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF6F6F6))
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Hủy", color = Color.Red)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (mainImageUri == null && mainImageUrl.isEmpty() || subImageUris.any { it == null && subImageUrls[subImageUris.indexOf(it)].isEmpty() }) {
                                Toast.makeText(context, "Vui lòng chọn đầy đủ ảnh (1 chính, 6 phụ)", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            coroutineScope.launch {
                                try {
                                    val folder = when (brand) {
                                        "Bitis" -> "products-bitis"
                                        "Adidas" -> "products-adidas"
                                        "Nike" -> "products-nike"
                                        else -> ""
                                    }
                                    if (folder.isEmpty()) {
                                        Toast.makeText(context, "Thương hiệu không hợp lệ", Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }
                                    val newMainUrl = if (mainImageUri != null) cloudinaryService.uploadImage(mainImageUri!!, folder) else mainImageUrl
                                    val newSubUrls = subImageUris.mapIndexed { index, uri ->
                                        uri?.let { cloudinaryService.uploadImage(it, folder) } ?: subImageUrls[index]
                                    }
                                    val allImages = listOf(newMainUrl) + newSubUrls
                                    val sizes = size.split(",").mapNotNull { it.trim().toIntOrNull() }
                                    val collection = when (brand) {
                                        "Bitis" -> "products-bitis"
                                        "Adidas" -> "products-adidas"
                                        "Nike" -> "products-nike"
                                        else -> ""
                                    }
                                    if (collection.isEmpty()) {
                                        Toast.makeText(context, "Thương hiệu không hợp lệ", Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }
                                    onSave(
                                        product.copy(
                                            brand = brand,
                                            description = description,
                                            name = name,
                                            price = price.toDoubleOrNull() ?: 0.0,
                                            size = sizes,
                                            imageUrl = newMainUrl,
                                            images = allImages,
                                            collection = collection
                                        )
                                    )
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Lỗi tải ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("Lưu", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar1(navController: NavController) {
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
                navController.navigate("AdminDashboard")
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
                        Icons.Default.Dashboard,
                        contentDescription = "Dashboard",
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
                navController.navigate("AdminProductManagement")
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
                        Icons.Default.ShoppingBag,
                        contentDescription = "ProductManagement",
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
                navController.navigate("AdminUserManagement")
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
                        Icons.Default.Person,
                        contentDescription = "UserManagement",
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
                navController.navigate("AdminOrderManagement")
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
                        Icons.Default.ShoppingCart,
                        contentDescription = "OrderManagement",
                        tint = if (selectedIndex.value == 3) Color.White else Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            alwaysShowLabel = false
        )
        BottomNavigationItem(
            selected = selectedIndex.value == 4,
            onClick = {
                scope.launch { selectedIndex.value = 4 }
                navController.navigate("ProfileAdmin")
            },
            icon = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (selectedIndex.value == 4) Color.Black else Color.Transparent,
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = "ProfileAdmin",
                        tint = if (selectedIndex.value == 4) Color.White else Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            alwaysShowLabel = false
        )
    }
}