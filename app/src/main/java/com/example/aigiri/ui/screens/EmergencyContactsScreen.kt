//package com.example.aigiri.ui.screens
//import android.app.Activity
//import android.content.Intent
//import android.provider.ContactsContract
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.example.aigiri.model.EmergencyContact
//import com.example.aigiri.ui.components.ContactItem
//import com.example.aigiri.viewmodel.EmergencyContactsViewModel
//import kotlinx.coroutines.launch
//import org.burnoutcrew.reorderable.ReorderableItem
//import org.burnoutcrew.reorderable.detectReorder
//import org.burnoutcrew.reorderable.detectReorderAfterLongPress
//import org.burnoutcrew.reorderable.reorderable
//import org.burnoutcrew.reorderable.rememberReorderableLazyListState
//
//// Compose
//import androidx.compose.runtime.getValue
//// Layout
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.width
//
//// UI
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.Icon
//import androidx.compose.material3.SnackbarHost
//import androidx.compose.material3.SnackbarHostState
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.ui.graphics.Color
//
////
////@Composable
////fun EmergencyContactsScreen(
////    navController: NavController,
////    viewModel: EmergencyContactsViewModel
////) {
////    val context = LocalContext.current
////    val scope = rememberCoroutineScope()
////    val snackbarHostState = remember { SnackbarHostState() }
////
////    val contactList = viewModel.contactList
////    var showEmptyListDialog by remember { mutableStateOf(false) }
////    var showDeleteDialog by remember { mutableStateOf(false) }
////    var contactToDelete by remember { mutableStateOf<EmergencyContact?>(null) }
////
////    val pickContactLauncher = rememberLauncherForActivityResult(
////        contract = ActivityResultContracts.StartActivityForResult()
////    ) { result ->
////        if (result.resultCode == Activity.RESULT_OK) {
////            val contactUri = result.data?.data ?: return@rememberLauncherForActivityResult
////            val cursor = context.contentResolver.query(
////                contactUri,
////                arrayOf(
////                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
////                    ContactsContract.CommonDataKinds.Phone.NUMBER
////                ),
////                null,
////                null,
////                null
////            )
////            cursor?.use {
////                if (it.moveToFirst()) {
////                    val name = it.getString(0)
////                    val number = it.getString(1).filter { ch -> ch.isDigit() }
////                    viewModel.addContactFromPicker(name, number)
////                }
////            }
////        }
////    }
////
////    val permissionLauncher = rememberLauncherForActivityResult(
////        contract = ActivityResultContracts.RequestPermission()
////    ) { isGranted ->
////        if (isGranted) {
////            pickContactLauncher.launch(Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI))
////        } else {
////            scope.launch {
////                snackbarHostState.showSnackbar("Contacts permission denied. Cannot access contacts.")
////            }
////        }
////    }
////
////
////    val reorderState = rememberReorderableLazyListState(
////        onMove = { from, to -> viewModel.reorderContacts(from.index, to.index) }
////    )
////
////    LaunchedEffect(Unit) {
////        viewModel.snackbarMessage.collect { message ->
////            snackbarHostState.showSnackbar(message)
////        }
////    }
////
////    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
////        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
////            Button(
////                onClick = {
////                    val permission = android.Manifest.permission.READ_CONTACTS
////                    val hasPermission = android.content.pm.PackageManager.PERMISSION_GRANTED ==
////                            context.checkSelfPermission(permission)
////
////                    if (hasPermission) {
////                        val pickIntent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
////                        pickContactLauncher.launch(pickIntent)
////                    } else {
////                        permissionLauncher.launch(permission)
////                    }
////                },
////                modifier = Modifier.fillMaxWidth()
////            ) {
////                Icon(Icons.Default.Add, contentDescription = "Add Contact")
////                Spacer(Modifier.width(8.dp))
////                Text("Add Contact")
////            }
////
////            Spacer(Modifier.height(16.dp))
////
////            LazyColumn(
////                state = reorderState.listState,
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .reorderable(reorderState)
////                    .detectReorderAfterLongPress(reorderState)
////            ) {
////                itemsIndexed(contactList, key = { _, contact -> contact.name }) { _, contact ->
////                    val isDragging = reorderState.draggingItem?.key == contact.name
////
////                    ReorderableItem(reorderState, key = contact.name) { dragging ->
////                        ContactItem(
////                            contact = contact,
////                            onDelete = {
////                                contactToDelete = contact
////                                showDeleteDialog = true
////                            },
////                            isDragging = dragging,
////                            dragHandle = Modifier.detectReorder(reorderState),
////                            modifier = Modifier.fillMaxWidth()
////                        )
////                    }
////                }
////            }
////
////            Spacer(modifier = Modifier.height(16.dp))
////
//
////        }
////
//
////
////
////    }
////}
//@Composable
//fun EmergencyContactsScreen(
//    navController: NavController,
//    viewModel: EmergencyContactsViewModel
//) {
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    val contactList by remember { derivedStateOf { viewModel.contacts } }
//
//    var draggedIndex by remember { mutableStateOf<Int?>(null) }
//    var dragOffset by remember { mutableFloatStateOf(0f) }
//    val animatedDragOffset by animateFloatAsState(
//        targetValue = dragOffset,
//        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessVeryLow),
//        label = "dragOffsetAnimation"
//    )
//    val itemHeights = remember { mutableStateMapOf<Int, Float>() }
//
//    val nameFocusRequester = remember { FocusRequester() }
//
//    // Contact picker launcher
//    val pickContactLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.PickContact()
//    ) { uri ->
//        // Same contact picker logic...
//        // After extracting, set:
//        // viewModel.contactName = extractedName
//        // viewModel.phoneNumber = formattedPhoneNumber
//    }
//
//    // Permission launcher
//    val permissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (isGranted) {
//            pickContactLauncher.launch(null)
//        } else {
//            scope.launch {
//                snackbarHostState.showSnackbar("Contacts permission denied. Cannot access contacts.")
//            }
//        }
//    }
//
//    Scaffold(
//        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp)
//        ) {
//            IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.Start)) {
//                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF6A1B9A))
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//            Text("Add Emergency Contacts", fontSize = 24.sp, fontWeight = FontWeight.Bold)
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            OutlinedTextField(
//                value = viewModel.contactName,
//                onValueChange = { viewModel.contactName = it; viewModel.nameError = null },
//                label = { Text("Contact Name") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .focusRequester(nameFocusRequester),
//                isError = viewModel.nameError != null,
//                supportingText = { viewModel.nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
//                trailingIcon = {
//                    IconButton(onClick = {
//                        permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
//                    }) {
//                        Icon(Icons.Default.AccountBox, contentDescription = "Pick Contact", tint = Color(0xFF6A1B9A))
//                    }
//                }
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = viewModel.phoneNumber,
//                onValueChange = {
//                    if (it.all(Char::isDigit) && it.length <= 10) {
//                        viewModel.phoneNumber = it
//                        viewModel.phoneError = null
//                    }
//                },
//                label = { Text("Phone Number") },
//                modifier = Modifier.fillMaxWidth(),
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                isError = viewModel.phoneError != null,
//                supportingText = { viewModel.phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            TextButton(
//                onClick = {
//                    viewModel.addContact(
//                        onSuccess = {
//                            scope.launch {
//                                snackbarHostState.showSnackbar("Contact added successfully!")
//                                nameFocusRequester.requestFocus()
//                            }
//                        },
//                        onError = {
//                            scope.launch {
//                                snackbarHostState.showSnackbar("Error: $it")
//                            }
//                        }
//                    )
//                },
//                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF6A1B9A))
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Add")
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("Add Another")
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            if (contactList.isNotEmpty()) {
//                Text("Added Contacts", fontSize = 16.sp, fontWeight = FontWeight.Bold)
//                Spacer(modifier = Modifier.height(8.dp))
//
//                LazyColumn(modifier = Modifier.weight(1f)) {
//                    itemsIndexed(contactList, key = { _, contact -> contact.hashCode() }) { index, contact ->
//                        val isDragging = draggedIndex == index
//                        ContactItem(
//                            contact = contact,
//                            isDragging = isDragging,
//                            onDelete = {
//                                viewModel.contactToDelete = contact
//                                viewModel.showDeleteDialog = true
//                            },
//                            onHeightMeasured = { height -> itemHeights[index] = height },
//                            triggerShake = contactList,
//                            modifier = Modifier
//                                .zIndex(if (isDragging) 1f else 0f)
//                                .offset { IntOffset(0, if (isDragging) animatedDragOffset.roundToInt() else 0) }
//                                .pointerInput(Unit) {
//                                    detectDragGesturesAfterLongPress(
//                                        onDragStart = {
//                                            draggedIndex = index
//                                            dragOffset = 0f
//                                        },
//                                        onDrag = { change, dragAmount ->
//                                            change.consume()
//                                            dragOffset += dragAmount.y
//                                            val offset = dragOffset
//                                            val newIndex = if (offset > 0) {
//                                                val next = index + 1
//                                                if (next < contactList.size && offset >= (itemHeights[next] ?: 72f)) next else index
//                                            } else {
//                                                val prev = index - 1
//                                                if (prev >= 0 && offset <= -(itemHeights[prev] ?: 72f)) prev else index
//                                            }
//                                            if (newIndex != index && newIndex != draggedIndex) {
//                                                viewModel.reorder(index, newIndex)
//                                                draggedIndex = newIndex
//                                                dragOffset = 0f
//                                            }
//                                        },
//                                        onDragEnd = {
//                                            draggedIndex = null
//                                            dragOffset = 0f
//                                        },
//                                        onDragCancel = {
//                                            draggedIndex = null
//                                            dragOffset = 0f
//                                        }
//                                    )
//                                }
//                        )
//                    }
//                }
//            } else {
//                Spacer(modifier = Modifier.weight(1f))
//            }
//
//            //Done button first checks contacts list is empty or not if it empty then show the EmptyList Dialog box
//            Button(
//                onClick = {
//                    if (viewModel.contacts.isEmpty()) {
//                        viewModel.setShowEmptyListDialog(true)
//                    } else {
//                        viewModel.saveContactList(viewModel.contacts)
//
//                        scope.launch {
//                            snackbarHostState.showSnackbar("Emergency contacts saved.")
//                            navController.navigate("dashboard") {
//                                popUpTo(0) { inclusive = false }
//                            }
//                        }
//                    }
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Done")
//            }
//
//
//
//
//            // Dialog: Empty List
//            if (viewModel.showEmptyListDialog) {
//                AlertDialog(
//                    onDismissRequest = { viewModel.setShowEmptyListDialog(false) },
//                    confirmButton = {
//                        TextButton(onClick = { viewModel.setShowEmptyListDialog(false) }) {
//                            Text("OK")
//                        }
//                    },
//                    title = { Text("No Contacts") },
//                    text = { Text("Please add at least one emergency contact before proceeding.") }
//                )
//            }
//
//        //Dialog box for conformation to delete
//        if (viewModel.showDeleteDialog && viewModel.contactToDelete != null) {
//            AlertDialog(
//                onDismissRequest = {
//                    viewModel.showDeleteDialog = false
//                    viewModel.contactToDelete = null
//                },
//                title = { Text("Confirm Delete") },
//                text = { Text("Are you sure you want to remove ${viewModel.contactToDelete?.name}?") },
//                confirmButton = {
//                    TextButton(
//                        onClick = { viewModel.deleteContact() },
//                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF6A1B9A))
//                    ) {
//                        Text("Yes")
//                    }
//                },
//                dismissButton = {
//                    TextButton(
//                        onClick = {
//                            viewModel.showDeleteDialog = false
//                            viewModel.contactToDelete = null
//                        },
//                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
//                    ) {
//                        Text("No")
//                    }
//                }
//            )
//        }
//    }
//}
//
//}