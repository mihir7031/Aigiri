package com.example.aigiri.repository

import com.example.aigiri.dao.UserDao
import com.example.aigiri.model.EmergencyContact
import com.example.aigiri.model.User
import org.mindrot.jbcrypt.BCrypt

class UserRepository(private val userDao: UserDao = UserDao()) {

    suspend fun saveUser(user: User): Result<String> {
        val hashedPassword = hashPassword(user.password)
        val userWithHashedPassword = user.copy(password = hashedPassword)
        return userDao.saveUser(userWithHashedPassword)
    }

    // Use UID instead of phoneNo
    suspend fun addEmergencyContact(userUid: String, contact: EmergencyContact): Result<Unit> {
        return userDao.addEmergencyContact(userUid, contact)
    }

    suspend fun getEmergencyContacts(userUid: String): Result<List<EmergencyContact>> {
        return userDao.getEmergencyContacts(userUid)
    }

    suspend fun getEmergencyContactIdByPhone(userUid: String, contactPhoneNo: String): Result<String?> {
        return userDao.getEmergencyContactIdByPhone(userUid, contactPhoneNo)
    }

    suspend fun deleteEmergencyContact(userUid: String, contactDocId: String): Result<Unit> {
        return userDao.deleteEmergencyContact(userUid, contactDocId)
    }

    suspend fun updateEmergencyContact(
        userUid: String,
        contactDocId: String,
        updatedFields: Map<String, Any>
    ): Result<Unit> {
        return userDao.updateEmergencyContact(userUid, contactDocId, updatedFields)
    }

    suspend fun isPhoneTaken(phoneNo: String): Result<Boolean> {
        return userDao.isPhoneTaken(phoneNo)
    }

    suspend fun isEmailTaken(email: String): Result<Boolean> {
        return userDao.isEmailTaken(email)
    }

    suspend fun isUsernameTaken(username: String): Result<Boolean> {
        return userDao.isUsernameTaken(username)
    }
    private fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(plainPassword, hashedPassword)
    }
    private fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
    suspend fun login(username: String, password: String): Result<User> {
        val userResult = userDao.getUserByUsername(username)
        if (userResult.isFailure) return Result.failure(userResult.exceptionOrNull()!!)

        val user = userResult.getOrNull()
        return if (user != null && verifyPassword(password, user.password)) {
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid username or password"))
        }
    }

}
