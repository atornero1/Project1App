package com.example.project1.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User): Long

    @Query("DELETE FROM users WHERE username = :username")
    suspend fun deleteByUsername(username: String): Int

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getByUsername(username: String): User?

    @Query("UPDATE users SET password = :newPassword WHERE username = :username")
    suspend fun updatePassword(username: String, newPassword: String): Int

    @Query("UPDATE users SET username = :newUsername WHERE username = :username")
    suspend fun updateUsername(username: String, newUsername: String): Int

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getLoggedInUser(): Flow<User?>

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutAllUsers()

    @Query("UPDATE users SET isLoggedIn = 1 WHERE username = :username")
    suspend fun setLoggedIn(username: String)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecipe(recipe: SavedRecipes)

    @Query("SELECT * FROM saved_recipes WHERE userId = :userId")
    fun getSavedRecipes(userId: Int): kotlinx.coroutines.flow.Flow<List<SavedRecipes>>
    @Query("DELETE FROM saved_recipes WHERE id = :recipeId")
    suspend fun deleteRecipe(recipeId: Int)
}