package com.example.aigiri.ui.screens
import android.app.Activity
import android.content.Intent
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.collection.emptyLongSet
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aigiri.model.EmergencyContact
import com.example.aigiri.ui.components.ContactItem
import com.example.aigiri.viewmodel.EmergencyContactsViewModel
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.reorderable
import org.burnoutcrew.reorderable.rememberReorderableLazyListState

// Compose
import androidx.compose.runtime.getValue
// Layout
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Contacts

// UI
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

@Composable
fun EmergencyContactsScreen(
    navController: NavController,
    viewModel: EmergencyContactsViewModel,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
//
    val contactList by remember { derivedStateOf { viewModel.contacts } }

    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val animatedDragOffset by animateFloatAsState(
        targetValue = dragOffset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "dragOffsetAnimation"
    )
    LaunchedEffect(snackbarHostState) {
        snackbarHostState.showSnackbar("Contact added successfully!")
    }
    val itemHeights = remember { mutableStateMapOf<Int, Float>() }

    val nameFocusRequester = remember { FocusRequester() }
//
    // Contact picker launcher
    val pickContactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri ->
        if (uri == null) {
            scope.launch {
                snackbarHostState.showSnackbar("No contact selected")
            }
            return@rememberLauncherForActivityResult
        }

        try {
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use { c ->
                if (c.moveToFirst()) {
                    val nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    val contactIdIndex = c.getColumnIndex(ContactsContract.Contacts._ID)

                    if (nameIndex < 0 || contactIdIndex < 0) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Error: Contact data unavailable")
                        }
                        return@use
                    }

                    val name = c.getString(nameIndex) ?: ""
                    val contactId = c.getString(contactIdIndex)

                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(contactId),
                        null
                    )

                    phoneCursor?.use { pc ->
                        if (pc.moveToFirst()) {
                            val phoneIndex =
                                pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            if (phoneIndex >= 0) {
                                val rawPhone =
                                    pc.getString(phoneIndex)?.replace("[^0-9]".toRegex(), "") ?: ""
                                val formattedPhone =
                                    if (rawPhone.length > 10) rawPhone.takeLast(10) else rawPhone

                                viewModel.contactName = name
                                viewModel.phoneNumber = formattedPhone
                                viewModel.nameError = null
                                viewModel.phoneError = null
                            } else {
                                viewModel.phoneNumber = ""
                                scope.launch {
                                    snackbarHostState.showSnackbar("Contact has no phone number")
                                }
                            }
                        } else {
                            viewModel.phoneNumber = ""
                            scope.launch {
                                snackbarHostState.showSnackbar("Contact has no phone number")
                            }
                        }
                    } ?: run {
                        viewModel.phoneNumber = ""
                        scope.launch {
                            snackbarHostState.showSnackbar("Error retrieving phone number")
                        }
                    }

                    if (viewModel.contactName.isEmpty()) {
                        viewModel.contactName = name
                    }
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Error: No contact data found")
                    }
                }
            } ?: run {
                scope.launch {
                    snackbarHostState.showSnackbar("Error retrieving contact")
                }
            }
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("Error: ${e.message}")
            }
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickContactLauncher.launch(null)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Contacts permission denied")
            }
        }
    }



    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        )
        {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Add Emergency Contacts", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = viewModel.contactName,
                onValueChange = { viewModel.contactName = it; viewModel.nameError = null },
                label = { Text("Contact Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(nameFocusRequester),
                isError = viewModel.nameError != null,
                supportingText = {
                    viewModel.nameError?.let {
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                trailingIcon = {
                    IconButton(onClick = {
                        permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
                    }) {
                        Icon(
                            Icons.Default.Contacts,
                            contentDescription = "Pick Contact",
                            tint = Color(0xFF6A1B9A)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.phoneNumber,
                onValueChange = {
                    if (it.all(Char::isDigit) && it.length <= 10) {
                        viewModel.phoneNumber = it
                        viewModel.phoneError = null
                    }
                },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = viewModel.phoneError != null,
                supportingText = {
                    viewModel.phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            )


            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    viewModel.addContact(
                        onSuccess = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Contact added successfully!")
                                nameFocusRequester.requestFocus()
                            }
                        },
                        onError = { msg ->
                            scope.launch {
                                snackbarHostState.showSnackbar("Error: $msg")
                            }
                        }
                    )
                },
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF6A1B9A))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Another")
            }


            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.contacts.isNotEmpty()) {
                Text("Added Contacts", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(
                        viewModel.contacts,
                        key = { _, contact -> contact.hashCode() }) { index, contact ->

                        val isDragging = draggedIndex == index

                        ContactItem(
                            contact = contact.copy(priority = index + 1), // Update priority display
                            isDragging = isDragging,
                            onDelete = {
                                viewModel.removeContact(contact) // Remove from local list
                            },
                            dragHandle = Modifier.pointerInput(Unit) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        draggedIndex = index
                                        dragOffset = 0f
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffset += dragAmount.y

                                        val newIndex = if (dragOffset > 0) {
                                            val next = index + 1
                                            if (next < viewModel.contacts.size &&
                                                dragOffset >= (itemHeights[next] ?: 72f)
                                            ) next else index
                                        } else {
                                            val prev = index - 1
                                            if (prev >= 0 &&
                                                dragOffset <= -(itemHeights[prev] ?: 72f)
                                            ) prev else index
                                        }

                                        if (newIndex != index && newIndex != draggedIndex) {
                                            viewModel.reorder(index, newIndex)
                                            draggedIndex = newIndex
                                            dragOffset = 0f
                                        }
                                    },
                                    onDragEnd = {
                                        draggedIndex = null
                                        dragOffset = 0f
                                    },
                                    onDragCancel = {
                                        draggedIndex = null
                                        dragOffset = 0f
                                    }
                                )
                            },
                            modifier = Modifier
                                .zIndex(if (isDragging) 1f else 0f)
                                .offset {
                                    IntOffset(
                                        0,
                                        if (isDragging) animatedDragOffset.roundToInt() else 0
                                    )
                                }
                                .onGloballyPositioned { coordinates ->
                                    itemHeights[index] = coordinates.size.height.toFloat()
                                }
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }


//Done button first checks contacts list is empty or not if it empty then show the EmptyList Dialog box
            Button(
                onClick = {
                    if (viewModel.contacts.isEmpty()) {
                        viewModel.nameError = "Please add at least one contact"
                        scope.launch {
                            snackbarHostState.showSnackbar("No contacts to save")
                        }
                    } else {
                        scope.launch {
                            viewModel.saveAllContactsToDb(
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Emergency contacts saved.")
                                        navController.navigate("dashboard") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                },
                                onError = { error ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Error: $error")
                                    }
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done")
            }



            // Dialog: Empty List
            if (viewModel.showEmptyListDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.showEmptyListDialog=false },
                    confirmButton = {
                        TextButton(onClick = { viewModel.showEmptyListDialog=false }) {
                            Text("OK")
                        }
                    },
                    title = { Text("No Contacts") },
                    text = { Text("Please add at least one emergency contact before proceeding.") }
                )
            }

            //Dialog box for conformation to delete
            if (viewModel.showDeleteDialog && viewModel.contactToDelete != null) {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.showDeleteDialog = false
                        viewModel.contactToDelete = null
                    },
                    title = { Text("Confirm Delete") },
                    text = { Text("Are you sure you want to remove ${viewModel.contactToDelete?.name}?") },
                    confirmButton = {
                        TextButton(
                            onClick = { viewModel.deleteContact() },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF6A1B9A))
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                viewModel.showDeleteDialog = false
                                viewModel.contactToDelete = null
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                        ) {
                            Text("No")
                        }
                    }
                )
            }
        }

    }
}


