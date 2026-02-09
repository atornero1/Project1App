package com.example.project1

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.project1.ui.theme.Project1Theme

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Project1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProfileScreen(
                        // Will change username and password in database once setup
                        onChangeUsername = {},
                        onChangePassword = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(
    onChangeUsername: () -> Unit,
    onChangePassword: () -> Unit
) {
    var username by remember { mutableStateOf("") } // Input for username
    var passwordForUsername by remember { mutableStateOf("") } // Input for password un change
    var oldPassword by remember { mutableStateOf("") } // Input for old password
    var newPassword1 by remember { mutableStateOf("") } // Input 1 for new password
    var newPassword2 by remember { mutableStateOf("") } // Input 2 for new password
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome text for specific user
        Text(
            text = "Hi, ",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        // Text to indicate change username section
        Text(
            text = "Change Username",
            style = MaterialTheme.typography.bodyLarge
        )
        // Text input for new username
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("New Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        // Text input for password to change username
        OutlinedTextField(
            value = passwordForUsername,
            onValueChange = { passwordForUsername = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        // Button to change username
        OutlinedButton(
            onClick = onChangeUsername,
            modifier = Modifier.fillMaxWidth()
        ) {Text("Change Username")}
        // Text to indicate change password section
        Text(
            text = "Change Password",
            style = MaterialTheme.typography.bodyLarge
        )
        // Text input for old password to change password
        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = { Text("Old Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        // Text input for new password to change password
        OutlinedTextField(
            value = newPassword1,
            onValueChange = { newPassword1 = it },
            label = { Text("New Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        // Text input to re-enter new password to change password
        OutlinedTextField(
            value = newPassword2,
            onValueChange = { newPassword2 = it },
            label = { Text("Re-enter New Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        // Button to change username
        OutlinedButton(
            onClick = onChangePassword,
            modifier = Modifier.fillMaxWidth()
        ) {Text("Change Password")}
    }
}