package com.example.project1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.project1.data.local.AppDatabase
import com.example.project1.data.local.UserRepository
import com.example.project1.ui.theme.Project1Theme
import kotlinx.coroutines.launch

class CreateAccountPage : ComponentActivity() {
    private lateinit var userRepository: UserRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()
        userRepository = UserRepository(userDao)
        setContent {
            Project1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CreateAccountScreen(
                        onCreateAccount = { username, password ->
                            lifecycleScope.launch {
                                val success = userRepository.registerUser(username, password)
                                if (success) {
                                    Toast.makeText(this@CreateAccountPage, "Account created!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@CreateAccountPage, LoginPage::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@CreateAccountPage, "Username already taken", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onBack = {
                            val intent = Intent(this, LoginPage::class.java)
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

// This composable function holds all the UI elements for the create account screen.
@Composable
private fun CreateAccountScreen(onCreateAccount: (String, String) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // A simple text element for the title.
        Text(
            text = "Create an Account",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(28.dp))

        // An input field for the username.
        OutlinedTextField(
            value = username, // The text to display
            onValueChange = { username = it }, // Update the state when the user types
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // An input field for the password.
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            // This hides the text, showing dots instead.
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(12.dp))

        // An input field for confirming the password.
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(28.dp))

        // The main button for creating the account.
        Button(
            onClick = {
                if (password != confirmPassword) {
                    // Show a pop-up message if passwords don't match.
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                } else if (username.isBlank() || password.isBlank()) {
                    // Show a message if any field is empty.
                    Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                } else {
                    onCreateAccount(username.trim(), password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Create Account")
        }

        Spacer(Modifier.height(12.dp))

        // The secondary button to go back.
        Button(
            onClick = {
                onBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Back")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateAccountScreenPreview() {
    Project1Theme {
        CreateAccountScreen(onCreateAccount = { _, _ -> }, onBack = {})
    }
}