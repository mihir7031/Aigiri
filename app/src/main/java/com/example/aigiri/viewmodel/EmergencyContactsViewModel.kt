package com.example.aigiri.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aigiri.model.EmergencyContact
import com.example.aigiri.network.TokenManager
import com.example.aigiri.repository.EmergencyContactsRepository
import kotlinx.coroutines.launch


class EmergencyContactsViewModel(
    private val repository: EmergencyContactsRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var showEmptyListDialog by mutableStateOf(false)
    var contacts by mutableStateOf<List<EmergencyContact>>(emptyList())
        private set

    var contactName by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var nameError by mutableStateOf<String?>(null)
    var phoneError by mutableStateOf<String?>(null)

    var contactToDelete by mutableStateOf<EmergencyContact?>(null)
    var showDeleteDialog by mutableStateOf(false)

    init {
        loadContacts()
    }

    fun loadContacts(onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val userId = tokenManager.getCachedUserId()
            if (userId == null) {
                onError("User not logged in.")
                return@launch
            }

            val result = repository.getEmergencyContacts(userId)
            if (result.isSuccess) {
                contacts = result.getOrNull().orEmpty()
            } else {
                val errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Unknown error occurred"
                onError("Failed to load contacts: $errorMessage")
            }
        }
    }



    fun addContact(onSuccess: () -> Unit, onError: (String) -> Unit) {
        nameError = null
        phoneError = null

        when {
            contactName.isBlank() -> {
                nameError = "Name required"
                return
            }
            phoneNumber.isBlank() -> {
                phoneError = "Phone required"
                return
            }
            !phoneNumber.matches(Regex("\\d{10}")) -> {
                phoneError = "Must be 10 digits"
                return
            }
        }

        viewModelScope.launch {
            val userId = tokenManager.getCachedUserId()
            Log.d("AddContact", "User ID: $userId")
            if (userId == null) {
                onError("User ID not found")
                return@launch
            }
            val newContact = EmergencyContact(name = contactName, phoneNumber = phoneNumber)

            val result = repository.addEmergencyContact(userId, newContact)
            if (result.isSuccess) {
                contactName = ""
                phoneNumber = ""
                loadContacts()
                onSuccess()
            } else {
                onError("Failed to add contact")
            }
        }
    }

    fun promptDelete(contact: EmergencyContact) {
        contactToDelete = contact
        showDeleteDialog = true
    }

    fun confirmDelete(onError: (String) -> Unit) {
        val contact = contactToDelete ?: return
        viewModelScope.launch {
            val userId = tokenManager.getCachedUserId() ?: return@launch
            val result = repository.deleteEmergencyContact(userId, contact.id)
            if (result.isSuccess) {
                showDeleteDialog = false
                contactToDelete = null
                loadContacts()
            } else {
                onError("Failed to delete contact")
            }
        }
    }

    fun moveContact(fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex) return
        val updatedList = contacts.toMutableList()
        val item = updatedList.removeAt(fromIndex)
        updatedList.add(toIndex, item)

        updatedList.forEachIndexed { index, contact ->
            contact.priority = index + 1
        }

        contacts = updatedList
    }

    fun saveAll(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val userId = tokenManager.getCachedUserId() ?: return@launch
            try {
                contacts.forEach { contact ->
                    repository.updateEmergencyContact(
                        userUid = userId,
                        contactDocId = contact.id,
                        updatedFields = mapOf("priority" to contact.priority)
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }
    suspend fun isEmergencyContactAdded(): Boolean {
        val userId = tokenManager.getCachedUserId()
        if (userId.isNullOrBlank()) {
            // You can log this, show a toast, or handle it as needed
          println("Your account could not be fetched. Please log in again.")
            return false
        }

        val result = repository.getEmergencyContacts(userId)

        val contacts = result.getOrNull()
        return contacts != null && contacts.isNotEmpty()
    }


}
