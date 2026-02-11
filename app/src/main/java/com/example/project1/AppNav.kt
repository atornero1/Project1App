package com.example.project1

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object RecipeList : Screen("recipes")
    data object RecipeDetail : Screen("recipe/{id}") {
        fun createRoute(id: Int) = "recipe/$id"
    }
}

@Composable
fun AppNav(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLogin = { email, password ->
                    // You can replace this with real auth later
                    if (email.isNotBlank() && password.isNotBlank()) {
                        navController.navigate(Screen.RecipeList.route) {
                            popUpTo(Screen.Login.route) { inclusive = true } // prevents going back to login
                        }
                    }
                },
                onCreateAccount = {
                    // optional: show a screen later
                },
                onForgotPassword = {
                    // optional: show a screen later
                }
            )
        }

        composable(Screen.RecipeList.route) {
            RecipeListScreen(
                recipes = RecipeRepo.recipes,
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                }
            )
        }

        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            val recipe = RecipeRepo.getById(id)

            RecipeDetailScreen(
                recipe = recipe,
                onBack = { navController.popBackStack() }
            )
        }
    }
}