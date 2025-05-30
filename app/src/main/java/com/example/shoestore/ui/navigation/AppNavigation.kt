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
import com.example.shoestore.ui.checkout.OrderSuccessScreen
import com.example.shoestore.ui.order.OrderDetailScreen
import com.example.shoestore.ui.order.OrderHistoryScreen
import com.example.shoestore.ui.screens.detail.ProductDetailScreen
import com.example.shoestore.ui.screens.home.HomeScreen
import com.example.shoestore.ui.screens.list.ProductListScreen
import com.example.shoestore.ui.screens.search.SearchScreen
import com.example.shoestore.ui.profile.ProfileScreen
import com.example.shoestore.ui.reviews.MyReviewsScreen
import com.example.shoestore.ui.order.ProductReviewScreen
import com.example.shoestore.ui.reviews.ReviewsEditScreen
import com.example.shoestore.ui.screens.admin.AdminDashboardScreen
import com.example.shoestore.ui.screens.admin.order.AdminOrderManagementScreen
import com.example.shoestore.ui.screens.admin.product.AdminProductManagementScreen
import com.example.shoestore.ui.screens.admin.user.AdminUserManagementScreen
import com.example.shoestore.ui.screens.admin.account.ProfileAdmin
import com.example.shoestore.ui.screens.admin.order.AdminOrderDetailScreen
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
        composable("AccountScreen") { AccountScreen(navController) }
        composable("OrderHistoryScreen") { OrderHistoryScreen(navController) }
        composable("AddressScreen") { AddressScreen(navController) }
        composable("MyReviewsScreen") { MyReviewsScreen(navController) }

//        composable("OrderDetailScreen") {OrderDetailScreen(navController) }

        // Thêm route cho OrderDetailScreen với tham số orderId
        composable(
            route = "OrderDetailScreen?orderId={orderId}",
            arguments = listOf(
                navArgument("orderId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(navController, orderId)
        }

        composable(
            route = "AddressDetailScreen/{addressId}",
            arguments = listOf(navArgument("addressId") { type = NavType.StringType })
        ) { backStackEntry ->
            val addressId = backStackEntry.arguments?.getString("addressId") ?: ""
            AddressDetailScreen(navController, addressId)
        }

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

        // Admin
        composable("ProfileAdmin") { ProfileAdmin(navController) }
        composable("AdminDashboard") { AdminDashboardScreen(navController) }
        composable("AdminUserManagement") { AdminUserManagementScreen(navController) }
        composable("AdminProductManagement") { AdminProductManagementScreen(navController) }
        composable("AdminOrderManagement") { AdminOrderManagementScreen(navController) }

        composable(
            "AdminOrderDetailScreen?orderId={orderId}&userId={userId}",
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType; defaultValue = "" },
                navArgument("userId") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            AdminOrderDetailScreen(
                navController = navController,
                orderId = backStackEntry.arguments?.getString("orderId") ?: "",
                userId = backStackEntry.arguments?.getString("userId") ?: ""
            )
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