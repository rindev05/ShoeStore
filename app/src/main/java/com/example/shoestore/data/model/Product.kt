package com.example.shoestore.data.model

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val rating: Float,
    val imageUrl: Int,
    val description: String,
    val reviews: List<Review> = emptyList(),
    val images: List<Int> = emptyList() // Danh sách ảnh phụ
)

data class Review(
    val user: String,
    val rating: Float,
    val comment: String,
    val date: String,
    val imageAvatar: Int
)