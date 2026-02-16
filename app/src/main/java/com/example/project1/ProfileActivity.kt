package com.example.project1

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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.project1.data.local.AppDatabase
import com.example.project1.data.local.UserRepository
import kotlinx.coroutines.launch

// Added it as a class so that it can work with mainactivity / with the changes I made
class ProfileActivity : ComponentActivity() {
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()
        userRepository = UserRepository(userDao)

        setContent {
            MaterialTheme {
                Surface {
                    val loggedInUser by userRepository
                        .getLoggedInUser()
                        .collectAsState(initial = null)

                    ProfileScreen(
                        usernameFromDb = loggedInUser?.username ?: "",
                        onChangeUsername = { username, passwordForUsername ->
                            lifecycleScope.launch {
                                if (passwordForUsername != loggedInUser?.password) {
                                    Toast.makeText(
                                        this@ProfileActivity,
                                        "Password incorrect",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    try {
                                        userDao.updateUsername(
                                            loggedInUser!!.username,
                                            username
                                        )
                                        Toast.makeText(
                                            this@ProfileActivity,
                                            "Username changed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            this@ProfileActivity,
                                            "Username already taken",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        },
                        onChangePassword = { oldPassword, newPassword1, newPassword2 ->
                            lifecycleScope.launch {
                                if (loggedInUser?.password != oldPassword) {
                                    Toast.makeText(
                                        this@ProfileActivity,
                                        "Password incorrect",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (newPassword1 != newPassword2) {
                                    Toast.makeText(
                                        this@ProfileActivity,
                                        "Passwords do not match",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    userDao.updatePassword(
                                        loggedInUser!!.username,
                                        newPassword1
                                    )
                                    Toast.makeText(
                                        this@ProfileActivity,
                                        "Password changed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        onGoHome = {
                            startActivity(Intent(this@ProfileActivity,
                                MainActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    usernameFromDb: String,
    onChangeUsername: (String, String) -> Unit,
    onChangePassword: (String, String, String) -> Unit,
    onGoHome: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var passwordForUsername by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword1 by remember { mutableStateOf("") }
    var newPassword2 by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hi, $usernameFromDb",
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
            onClick = { onChangeUsername(username, passwordForUsername) },
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
            onClick = { onChangePassword(oldPassword, newPassword1, newPassword2) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Change Password") }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = onGoHome,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Go Home") }
    }
}
