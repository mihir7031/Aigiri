package com.example.aigiri.dao

import com.example.aigiri.model.EmergencyContact
import com.example.aigiri.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserDao(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private val usersCollection = db.collection("User")

    // Save user to Firestore with auto-generated document ID
    // Returns the Firestore-generated user document ID on success
    suspend fun saveUser(user: User): Result<String> {
        return try {
            val docRef = usersCollection
                .add(user)  // Firestore generates a unique document ID automatically
                .await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Add emergency contact to user's subcollection by Firestore user document ID
    suspend fun addEmergencyContact(userDocId: String, contact: EmergencyContact): Result<Unit> {
        return try {
            usersCollection
                .document(userDocId)
                .collection("emergency_contacts")
                .add(contact)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get user's emergency contacts by Firestore user document ID
    suspend fun getEmergencyContacts(userDocId: String): Result<List<EmergencyContact>> {
        return try {
            val snapshot = usersCollection
                .document(userDocId)
                .collection("emergency_contacts")
                .get()
                .await()
            val contacts = snapshot.toObjects(EmergencyContact::class.java)
            Result.success(contacts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get emergency contact document ID by contactNo (phone number)
    suspend fun getEmergencyContactIdByPhone(userDocId: String, contactPhoneNo: String): Result<String?> {
        return try {
            val snapshot = usersCollection
                .document(userDocId)
                .collection("emergency_contacts")
                .whereEqualTo("contactNo", contactPhoneNo)
                .get()
                .await()

            val document = snapshot.documents.firstOrNull()
            Result.success(document?.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete emergency contact by document ID
    suspend fun deleteEmergencyContact(userDocId: String, contactDocId: String): Result<Unit> {
        return try {
            usersCollection
                .document(userDocId)
                .collection("emergency_contacts")
                .document(contactDocId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update emergency contact fields
    suspend fun updateEmergencyContact(
        userDocId: String,
        contactDocId: String,
        updatedFields: Map<String, Any>
    ): Result<Unit> {
        return try {
            usersCollection
                .document(userDocId)
                .collection("emergency_contacts")
                .document(contactDocId)
                .update(updatedFields)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Check if phone number exists inside users collection (query phoneNo field)
    suspend fun isPhoneTaken(phoneNo: String): Result<Boolean> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("phone_no", phoneNo)
                .get()
                .await()
            Result.success(!snapshot.isEmpty)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Check if email is already used
    suspend fun isEmailTaken(email: String): Result<Boolean> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("email", email)
                .get()
                .await()
            Result.success(!snapshot.isEmpty)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Check if username is already used
    suspend fun isUsernameTaken(username: String): Result<Boolean> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("username", username)
                .get()
                .await()
            Result.success(!snapshot.isEmpty)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getUserByUsername(username: String): Result<User?> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .await()

            val user = if (snapshot.documents.isNotEmpty())
                snapshot.documents[0].toObject(User::class.java)
            else
                null

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
