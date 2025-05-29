package com.example.shoestore.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shoestore.ui.account.AccountScreen
import com.example.shoestore.ui.address.AddressAddScreen
import com.example.shoestore.ui.address.AddressDetailScreen
import com.example.shoestore.ui.address.AddressScreen
import com.example.shoestore.ui.auth.ForgotPasswordScreen
import com.example.shoestore.ui.auth.login.LoginScreen
import com.example.shoestore.ui.auth.signup.SignUpScreen
import com.example.shoestore.ui.cart.CartScreen
import com.example.shoestore.ui.checkout.CheckoutScreen
import com.example.shoestore.ui.order.OrderSuccessScreen
import com.example.shoestore.ui.orderdetail.OrderDetailScreen
import com.example.shoestore.ui.orderhistory.OrderHistoryScreen
import com.example.shoestore.ui.screens.detail.ProductDetailScreen
import com.example.shoestore.ui.screens.home.HomeScreen
import com.example.shoestore.ui.screens.list.ProductListScreen
import com.example.shoestore.ui.screens.search.SearchScreen
import com.example.shoestore.ui.profile.ProfileScreen
import com.example.shoestore.ui.reviews.MyReviewsScreen
import com.example.shoestore.ui.reviews.ProductReviewScreen
import com.example.shoestore.ui.reviews.ReviewsEditScreen
import com.example.shoestore.ui.splash.SplashScreen
import com.example.shoestore.ui.welcome.WelcomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "SplashScreen") {
        composable("home") { HomeScreen(navController) }
        composable("productList") { ProductListScreen(navController) }
        composable("SearchScreen") { SearchScreen(navController) }
        composable("CartScreen") { CartScreen(navController) }
        composable("ProfileScreen") { ProfileScreen(navController) }
//        composable("CheckoutScreen") { CheckoutScreen(navController) }
        composable("AccountScreen") { AccountScreen(navController) }
        composable("OrderHistoryScreen") { OrderHistoryScreen(navController) }
        composable("AddressScreen") { AddressScreen(navController) }
        composable("MyReviewsScreen") { MyReviewsScreen(navController) }

        composable("OrderDetailScreen") {OrderDetailScreen(navController) }
        composable("AddressDetailScreen") {AddressDetailScreen(navController) }
        composable("AddressAddScreen") {AddressAddScreen(navController) }
        composable("ProductReviewScreen") {ProductReviewScreen(navController) }
        composable("ReviewsEditScreen") {ReviewsEditScreen(navController) }
        composable("OrderSuccessScreen") { OrderSuccessScreen(navController) }

        composable("LoginScreen") { LoginScreen(navController) }
        composable("SignUpScreen") { SignUpScreen(navController) }
        composable("WelcomeScreen") { WelcomeScreen(navController) }
        composable("SplashScreen") { SplashScreen(navController) }
        composable("ForgotPasswordScreen") { ForgotPasswordScreen(navController) }

        // Thêm route cho CheckoutScreen với argument từ "Mua ngay"
        composable(
            route = "CheckoutScreen/{productId}/{size}",
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType },
                navArgument("size") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            CheckoutScreen(navController, backStackEntry)
        }

        // Thêm route cho CheckoutScreen với query parameter từ "Thanh toán"
        composable(
            route = "CheckoutScreen?cartItems={cartItems}",
            arguments = listOf(
                navArgument("cartItems") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            CheckoutScreen(navController, backStackEntry)
        }

//        composable(
//            route = "order_detail/{orderId}",
//            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
//            OrderDetailScreen(navController, orderId)
//        }
        composable(
            route = "product/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 1
            ProductDetailScreen(navController, productId)
        }
    }
}