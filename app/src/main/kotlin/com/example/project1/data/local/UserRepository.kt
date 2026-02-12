package com.example.project1.data.local

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(username: String, password: String): Boolean {
        val user = User(username = username, password = password)
        val result = userDao.insertUser(user)
        return result != -1L
    }

    suspend fun login(username: String, password: String): User? {
        val user = userDao.getByUsername(username)
        if (user != null && user.password == password) {
            return user
        }
        return null
    }

    suspend fun deleteUser(username: String): Boolean {
        return userDao.deleteByUsername(username) > 0
    }
}