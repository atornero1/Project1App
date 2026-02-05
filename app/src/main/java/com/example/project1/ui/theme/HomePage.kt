package com.example.project1.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomePage(
    onGoGenerate: () -> Unit,
    onGoSaved: () -> Unit,
    onGoProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Home",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Button for generate recipes
        Button(
            onClick = onGoGenerate,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate Recipes")
        }

        // Button for saved recipes
        Button(
            onClick = onGoSaved,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Saved Recipes")
        }

        // Button for profile info
        Button(
            onClick = onGoProfile,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Profile Info")
        }

        Spacer(modifier = Modifier.weight(1f))

        // Button for log out
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Out")
        }
    }
}
// Enables preview for HomePage

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    Project1Theme {
        HomePage(
            onGoGenerate = {},
            onGoSaved = {},
            onGoProfile = {},
            onLogout = {}
        )
    }
}
