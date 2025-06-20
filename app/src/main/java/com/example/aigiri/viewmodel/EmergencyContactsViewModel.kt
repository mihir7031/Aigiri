package com.example.aigiri.viewmodel

import com.example.aigiri.model.EmergencyContact
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.aigiri.TokenManager
import com.example.aigiri.repository.EmergencyContactsRepository



class EmergencyContactsViewModel(
    private val repository: EmergencyContactsRepository,
    private val tokenManager: TokenManager
) : ViewModel() {


    private val _contacts = mutableListOf<EmergencyContact>()
    val contacts: List<EmergencyContact> get() = _contacts

    var contactName by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var nameError by mutableStateOf<String?>(null)
    var phoneError by mutableStateOf<String?>(null)

    var contactToDelete by mutableStateOf<EmergencyContact?>(null)
    var showDeleteDialog by mutableStateOf(false)
    var showEmptyListDialog by mutableStateOf(false)


    fun addContact(onSuccess: () -> Unit, onError: (String) -> Unit) {
        nameError = null
        phoneError = null

        when {
            contactName.isBlank() -> nameError = "Contact name is required"
            phoneNumber.isBlank() -> phoneError = "Phone number is required"
            !phoneNumber.matches(Regex("\\d{10}")) -> phoneError = "Phone number must be exactly 10 digits"
            _contacts.any { it.name == contactName && it.phoneNumber != phoneNumber } ->
                nameError = "This name is already associated with a different number"
            _contacts.any { it.phoneNumber == phoneNumber && it.name != contactName } ->
                phoneError = "This number is already associated with a different name"
            _contacts.any { it.name == contactName && it.phoneNumber == phoneNumber } ->
                nameError = "This contact already exists"
            else -> {
                val newContact = EmergencyContact(contactName, phoneNumber, _contacts.size + 1)
                _contacts.add(newContact)
                contactName = ""
                phoneNumber = ""
                onSuccess()
            }
        }
    }
    fun deleteContact() {
        contactToDelete?.let { contact ->
            _contacts.remove(contact)
        }
        contactToDelete = null
        showDeleteDialog = false
    }


        fun reorder(from: Int, to: Int) {
                if (from in _contacts.indices && to in _contacts.indices) {
                    val moved = _contacts.removeAt(from)
                    _contacts.add(to, moved)
                }
            }

        fun removeContact(contact: EmergencyContact) {
            _contacts.remove(contact)
        }
    suspend fun saveAllContactsToDb(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val userId = tokenManager.getCachedUserId()
        if (userId.isNullOrBlank()) {
            onError("User not logged in.")
            return
        }

        val results = contacts.map { contact ->
            repository.addEmergencyContact(userId, contact)
        }

        val failed = results.filter { it.isFailure }
        if (failed.isEmpty()) {
            onSuccess()
        } else {
            onError("Failed to save ${failed.size} contact(s).")
        }
    }
}


