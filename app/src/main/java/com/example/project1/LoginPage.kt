package com.example.project1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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

class LoginPage : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                LoginScreen(
                    onLogin = { email, password ->
                        // Basic validation example (replace with API call)
                        if (email.isBlank() || password.isBlank()) {
                            Toast.makeText(
                                this,
                                "Please enter email and password",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Go to MainActivity (or ProfileActivity)
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
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
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

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
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
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
            onClick = { onLogin(email.trim(), password) },
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