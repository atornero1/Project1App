package com.example.project1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.project1.ui.theme.HomePage
import com.example.project1.ui.theme.Project1Theme

/**
 * MainActivity represents the main part of the app AFTER the user logs in.
 * The app always starts on the Home page here.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
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
                                // TODO: Replace with Profile Info screen later
                                currentScreen = "profile"
                            },
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
                        SimpleScreenText("Generate Recipes Page (Coming Soon)")
                    }

                    "saved" -> {
                        SimpleScreenText("Saved Recipes Page (Coming Soon)")
                    }

                    "profile" -> {
                        SimpleScreenText("Profile Info Page (Coming Soon)")
                    }
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
