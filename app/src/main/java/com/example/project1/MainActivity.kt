package com.example.project1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.project1.data.local.AppDatabase
import com.example.project1.data.local.UserRepository
import com.example.project1.ui.theme.Project1Theme
import kotlinx.coroutines.launch

/**
 * MainActivity represents the main part of the app AFTER the user logs in.
 * The app always starts on the Home page here.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // On startup, this fetches all users from the database and prints them to the Logcat.
        // Can be used to debug or see which users exist for testing purposes too
        val db = AppDatabase.getDatabase(this)
        val repository = UserRepository(db.userDao())

        val userDao = db.userDao()
        lifecycleScope.launch {
            val users = userDao.getAllUsers()
            Log.d("DatabaseUsers", "Users in database: $users")
        }

        setContent {
            val loggedInUser by repository
                .getLoggedInUser()
                .collectAsState(initial = null)
            val currentUserId = loggedInUser?.id ?: -1

            Project1Theme {

                /*
                 * currentScreen keeps track of which page the user is viewing.
                 * Screens are switched using simple state.
                 */
                var currentScreen by remember { mutableStateOf("home") }

                when (currentScreen) {

                    // ---------- HOME SCREEN ----------
                    "home" -> {
                        HomePage(
                            onGoGenerate = {
                                // TODO: Replace with Generate Recipes screen later
                                currentScreen = "generate"
                            },
                            onGoSaved = {
                                // TODO: Replace with Saved Recipes screen later
                                currentScreen = "saved"
                            },
                            onGoProfile = {
                                startActivity(Intent(this@MainActivity, ProfileActivity::class.java))                            },
                            onLogout = {
                                // TODO: Add real logout code later (database/auth)

                                // Go back to LoginPage activity
                                startActivity(
                                    Intent(this@MainActivity, LoginPage::class.java)
                                )
                                finish() // Prevent going back into the app
                            }
                        )
                    }

                    // ---------- PLACEHOLDER SCREENS ----------
                    "generate" -> {
                        GenerateRecipesPage(
                            onBack = { currentScreen = "home" },
                            apiKey = "6dcaf962a3614a588fbadc15730ab3cd",
                            modifier = Modifier.fillMaxSize(),
                            userRepository = repository, // Passing it here
                            userId = currentUserId,      // Passing it here
                            numberOfResults = 15
                        )
                    }


                    "saved" -> {
                        SavedRecipesPage(
                            userRepository = repository,
                            userId = currentUserId,
                            onBack = { currentScreen = "home" }
                        )
                    }

                    "profile" -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Opening Profile...")                    }
                }
            }
        }
    }
}

/**
 * Simple placeholder screen for pages that haven't been built yet.
 */
@Composable
fun SimpleScreenText(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}
}