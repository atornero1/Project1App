package com.example.project1.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_recipes")
data class SavedRecipes(
    @PrimaryKey val id: Int, // The Spoonacular ID
    val userId: Int,         // links recipe to a specific User
    val title: String,
    val imageUrl: String?,
    val summary: String      // Short description of ingredients
)