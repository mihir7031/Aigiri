package com.example.aigiri.dao

import com.example.aigiri.model.EmergencyContact
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EmergencyContactDao(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    private val usersCollection = db.collection("User")

    suspend fun addEmergencyContact(userDocId: String, contact: EmergencyContact): Result<Unit> {
        return try {
            usersCollection
                .document(userDocId)
                .collection("emergency_contacts")
                .add(contact.toMap())
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

}