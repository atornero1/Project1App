package com.example.project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Added it as a class so that it can work with mainactivity / with the changes I made
class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    ProfileScreen(
                        onChangeUsername = { },
                        onChangePassword = { }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    onChangeUsername: () -> Unit,
    onChangePassword: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var passwordForUsername by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword1 by remember { mutableStateOf("") }
    var newPassword2 by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hi, username",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Change Username",
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("New Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = passwordForUsername,
            onValueChange = { passwordForUsername = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedButton(
            onClick = onChangeUsername,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Change Username") }

        Text(
            text = "Change Password",
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = { Text("Old Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = newPassword1,
            onValueChange = { newPassword1 = it },
            label = { Text("New Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = newPassword2,
            onValueChange = { newPassword2 = it },
            label = { Text("Re-enter New Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedButton(
            onClick = onChangePassword,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Change Password") }
    }
}
