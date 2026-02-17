package com.example.project1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.project1.data.local.AppDatabase
import com.example.project1.data.local.UserRepository
import kotlinx.coroutines.launch

class LoginPage : ComponentActivity() {

    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the coroutine scope and get instances of the database and repository.
        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()
        userRepository = UserRepository(userDao)

        // On first launch, this will create a default "admin" user for testing.
        // If the user already exists, it will be ignored, making it safe to run every time.
        lifecycleScope.launch {
            userRepository.registerUser("admin", "password")
        }

        setContent {
            val context = LocalContext.current
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                LoginScreen(
                    onLogin = { username, password ->
                        lifecycleScope.launch {
                            val user = userRepository.login(username, password)
                            if (user != null) {
                                val intent = Intent(this@LoginPage, MainActivity::class.java)

                                // Go to MainActivity
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@LoginPage,
                                    "Invalid username or password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    onCreateAccount = {
                        val intent = Intent(context, CreateAccountPage::class.java)
                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    },
                    onForgotPassword = {
                        Toast.makeText(this, "Forgot password tapped", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
private fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onCreateAccount: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(28.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation()
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = showPassword,
                onCheckedChange = { showPassword = it }
            )
            Text(
                text = "Show password",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.weight(1f))

            TextButton(onClick = onForgotPassword) {
                Text("Forgot?")
            }
        }

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = { onLogin(username.trim(), password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            elevation = ButtonDefaults.buttonElevation()
        ) {
            Text("Log in")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onCreateAccount,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Create account")
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "By continuing, you agree to Terms & Privacy",
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
    }
}