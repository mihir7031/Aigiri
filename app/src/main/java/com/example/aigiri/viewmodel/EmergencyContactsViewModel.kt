//package com.example.aigiri.viewmodel
//
//import com.example.aigiri.model.EmergencyContact
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.aigiri.repository.EmergencyContactsRepository
//import com.example.aigiri.repository.UserRepository
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.asSharedFlow
//import kotlinx.coroutines.launch
//
//
//class EmergencyContactsViewModel( private val repository: EmergencyContactsRepository) : ViewModel() {
//
//
//    private val _contacts = mutableStateListOf<EmergencyContact>()
//    val contacts: List<EmergencyContact> get() = _contacts
//
//    var contactName by mutableStateOf("")
//    var phoneNumber by mutableStateOf("")
//    var nameError by mutableStateOf<String?>(null)
//    var phoneError by mutableStateOf<String?>(null)
//
//    var contactToDelete by mutableStateOf<EmergencyContact?>(null)
//    var showDeleteDialog by mutableStateOf(false)
//    var showEmptyListDialog by mutableStateOf(false)
//        private set
//    fun setShowEmptyListDialog(visible: Boolean) {
//        showEmptyListDialog = visible
//    }
//
//    init {
//        fetchContacts()
//    }
//
//    private fun fetchContacts() {
//        repository.listenToContacts(
//            onData = { list ->
//                _contacts.clear()
//                _contacts.addAll(list.sortedBy { it.priority })
//            },
//            onError = {
//                // handle error if needed
//            }
//        )
//    }
//
//    fun addContact(onSuccess: () -> Unit, onError: (String) -> Unit) {
//        nameError = null
//        phoneError = null
//
//        when {
//            contactName.isBlank() -> nameError = "Contact name is required"
//            phoneNumber.isBlank() -> phoneError = "Phone number is required"
//            !phoneNumber.matches(Regex("\\d{10}")) -> phoneError = "Phone number must be exactly 10 digits"
//            _contacts.any { it.name == contactName && it.phoneNumber != phoneNumber } ->
//                nameError = "This name is already associated with a different number"
//            _contacts.any { it.phoneNumber == phoneNumber && it.name != contactName } ->
//                phoneError = "This number is already associated with a different name"
//            _contacts.any { it.name == contactName && it.phoneNumber == phoneNumber } ->
//                nameError = "This contact already exists"
//            else -> {
//                val newContact = EmergencyContact(contactName, phoneNumber, _contacts.size + 1)
//                repository.addContact(newContact, {
//                    contactName = ""
//                    phoneNumber = ""
//                    onSuccess()
//                }, { e ->
//                    onError(e.message ?: "Unknown error")
//                })
//            }
//        }
//    }
//
//    fun deleteContact() {
//        contactToDelete?.let { contact ->
//            repository.deleteContact(contact.name, {
//                val updatedList = _contacts.filter { it != contact }
//                updatePriorities(updatedList)
//                contactToDelete = null
//                showDeleteDialog = false
//            }, {
//                // Handle delete error
//            })
//        }
//    }
//
//    fun updatePriorities(newList: List<EmergencyContact>) {
//        _contacts.clear()
//        _contacts.addAll(newList.mapIndexed { i, c -> c.copy(priority = i + 1) })
//        repository.updateContacts(_contacts) { /* Handle batch failure */ }
//    }
//
//    fun reorder(fromIndex: Int, toIndex: Int) {
//        val mutable = _contacts.toMutableList()
//        mutable.add(toIndex, mutable.removeAt(fromIndex))
//        updatePriorities(mutable)
//    }
//    fun saveContactList(contact: List<EmergencyContact>)
//    {
//
//    }
//}
