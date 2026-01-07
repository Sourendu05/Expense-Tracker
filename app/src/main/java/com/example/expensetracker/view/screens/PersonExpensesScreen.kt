package com.example.expensetracker.view.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.CallMade
import androidx.compose.material.icons.automirrored.rounded.CallReceived
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.expensetracker.model.database.ExpenseModel
import com.example.expensetracker.ui.theme.accentGradientEnd
import com.example.expensetracker.ui.theme.accentGradientStart
import com.example.expensetracker.ui.theme.deleteIcon
import com.example.expensetracker.ui.theme.negativeAmount
import com.example.expensetracker.ui.theme.payCard
import com.example.expensetracker.ui.theme.positiveAmount
import com.example.expensetracker.ui.theme.receiveCard
import com.example.expensetracker.view.ExpenseState
import com.example.expensetracker.viewModel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonExpensesScreen(
    viewModel: ExpenseViewModel,
    state: State<ExpenseState>,
    personId: Int?,
    navController: NavHostController
) {
    val showDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val expenseType = remember { mutableStateOf(true) }
    val selectedExpenses = remember { mutableStateListOf<ExpenseModel>() }
    val allExpensesChecked = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    
    allExpensesChecked.value = selectedExpenses.size == state.value.expenseList.value.size && state.value.expenseList.value.isNotEmpty()
    
    val isSelectionMode = selectedExpenses.size > 0
    
    // Auto-scroll to bottom when list changes
    LaunchedEffect(state.value.expenseList.value.size) {
        if (state.value.expenseList.value.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Animated crossfade between selection and normal top bar
            AnimatedContent(
                targetState = isSelectionMode,
                transitionSpec = {
                    fadeIn(tween(200)) + slideInVertically { -it / 2 } togetherWith 
                    fadeOut(tween(150)) + slideOutVertically { -it / 2 }
                },
                label = "topbar_transition"
            ) { selectionMode ->
                if (selectionMode) {
                    // Selection Mode Top Bar
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.statusBarsPadding()
                    ) {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                titleContentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            navigationIcon = {
                                IconButton(onClick = { selectedExpenses.clear() }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Clear selection",
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            },
                            title = {
                                Text(
                                    text = "${selectedExpenses.size} selected",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            actions = {
                                IconButton(onClick = {
                                    if (allExpensesChecked.value) {
                                        selectedExpenses.clear()
                                    } else {
                                        selectedExpenses.clear()
                                        selectedExpenses.addAll(state.value.expenseList.value)
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Rounded.SelectAll,
                                        contentDescription = "Select all",
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                IconButton(onClick = {
                                    showDeleteDialog.value = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.deleteIcon
                                    )
                                }
                            }
                        )
                    }
                } else {
                    // Normal Top Bar with summary
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.statusBarsPadding()
                    ) {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent
                            ),
                            navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            title = {
                                Column {
                                    Text(
                                        text = state.value.personName.value,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = when {
                                            state.value.amountReceivable.value > 0 -> "You'll receive"
                                            state.value.amountReceivable.value < 0 -> "You owe"
                                            else -> "All settled"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            actions = {
                                // Animated amount badge
                                val badgeColor by animateColorAsState(
                                    targetValue = when {
                                        state.value.amountReceivable.value > 0 -> MaterialTheme.colorScheme.secondaryContainer
                                        state.value.amountReceivable.value < 0 -> MaterialTheme.colorScheme.tertiaryContainer
                                        else -> MaterialTheme.colorScheme.surfaceContainerHigh
                                    },
                                    animationSpec = tween(300),
                                    label = "badge_color"
                                )
                                
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = badgeColor,
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = "₹${String.format("%,.0f", abs(state.value.amountReceivable.value))}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            state.value.amountReceivable.value > 0 -> MaterialTheme.colorScheme.positiveAmount
                                            state.value.amountReceivable.value < 0 -> MaterialTheme.colorScheme.negativeAmount
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Action buttons - premium style with gradient hint
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shadowElevation = 12.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Receive button
                    FilledTonalButton(
                        onClick = {
                            expenseType.value = true
                            showDialog.value = true
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.CallReceived,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Receive",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    // Pay button
                    FilledTonalButton(
                        onClick = {
                            expenseType.value = false
                            showDialog.value = true
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.CallMade,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pay",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .then(
                    if (isSelectionMode) {
                        Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { selectedExpenses.clear() }
                    } else Modifier
                )
        ) {
            if (state.value.expenseList.value.isEmpty()) {
                // Empty state
                EmptyTransactionsState()
            } else {
                // Transaction list with animations
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    state = listState,
                    reverseLayout = true
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    
                    itemsIndexed(
                        items = state.value.expenseList.value.reversed(),
                        key = { index, expense -> "${expense.expenseDate}_${expense.expenseName}_$index" }
                    ) { index, expense ->
                        ExpenseCard(
                            expense = expense,
                            selectedExpenses = selectedExpenses,
                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(durationMillis = 250, delayMillis = index * 20),
                                fadeOutSpec = tween(durationMillis = 150),
                                placementSpec = spring(stiffness = Spring.StiffnessMediumLow)
                            )
                        )
                    }
                    
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }

            // Add expense dialog
            if (showDialog.value) {
                PremiumExpenseDialog(
                    expenseType = expenseType.value,
                    onDismiss = { showDialog.value = false },
                    onConfirm = { name, amount, date ->
                        val newExpense = ExpenseModel(
                            expenseName = name,
                            expenseAmount = amount,
                            expenseType = expenseType.value,
                            expenseDate = date
                        )
                        
                        // Add and immediately sort by date (oldest first)
                        val updatedExpenses = (state.value.expenseList.value + newExpense)
                            .sortedBy { it.expenseDate }
                        state.value.expenseList.value = updatedExpenses
                        
                        val amountChange = if (expenseType.value) amount else -amount
                        state.value.amountReceivable.value += amountChange
                        state.value.personId.value = personId
                        viewModel.upsertPerson()
                        showDialog.value = false
                    }
                )
            }

            // Delete Confirmation Dialog
            if (showDeleteDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog.value = false },
                    shape = RoundedCornerShape(28.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.errorContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Delete ${selectedExpenses.size} ${if (selectedExpenses.size == 1) "Transaction" else "Transactions"}?",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            text = "This will permanently delete the selected transactions and update the balance accordingly.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Reverse the amounts for deleted expenses
                                selectedExpenses.forEach { expense ->
                                    val amountChange = if (expense.expenseType) {
                                        -expense.expenseAmount
                                    } else {
                                        expense.expenseAmount
                                    }
                                    state.value.amountReceivable.value += amountChange
                                }
                                
                                val updatedExpenses = state.value.expenseList.value.toMutableList()
                                updatedExpenses.removeAll(selectedExpenses)
                                state.value.expenseList.value = updatedExpenses
                                state.value.personId.value = personId
                                viewModel.upsertPerson()
                                selectedExpenses.clear()
                                showDeleteDialog.value = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete", fontWeight = FontWeight.SemiBold)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDeleteDialog.value = false },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyTransactionsState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Receipt,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "No transactions yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Add your first transaction below",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumExpenseDialog(
    expenseType: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (name: String, amount: Double, date: Long) -> Unit
) {
    val expenseName = remember { mutableStateOf("") }
    val expenseAmount = remember { mutableStateOf("") }
    val selectedDate = remember { mutableStateOf(System.currentTimeMillis()) }
    val showDatePicker = remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    
    val isFormValid by remember {
        derivedStateOf {
            expenseName.value.isNotBlank() && expenseAmount.value.isNotBlank()
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.value,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    val accentColor by animateColorAsState(
        targetValue = if (expenseType) 
            MaterialTheme.colorScheme.secondary 
        else 
            MaterialTheme.colorScheme.tertiary,
        animationSpec = tween(300),
        label = "accent_color"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = if (expenseType) listOf(
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                            ) else listOf(
                                MaterialTheme.colorScheme.tertiary,
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (expenseType) 
                        Icons.AutoMirrored.Rounded.CallReceived 
                    else 
                        Icons.AutoMirrored.Rounded.CallMade,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = if (expenseType) "Record Money Received" else "Record Payment Made",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = expenseName.value,
                    onValueChange = { expenseName.value = it },
                    label = { Text("Description") },
                    placeholder = { Text("What's this for?") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = expenseAmount.value,
                    onValueChange = { expenseAmount.value = it },
                    label = { Text("Amount") },
                    placeholder = { Text("0") },
                    leadingIcon = {
                        Text(
                            text = "₹",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Date selector
                Surface(
                    onClick = { showDatePicker.value = true },
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerLow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarMonth,
                                contentDescription = null,
                                tint = accentColor
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Date",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = dateFormatter.format(Date(selectedDate.value)),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        expenseName.value,
                        expenseAmount.value.toDoubleOrNull() ?: 0.0,
                        selectedDate.value
                    )
                },
                enabled = isFormValid,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }
        }
    )

    // Date picker dialog
    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            if (millis <= System.currentTimeMillis()) {
                                selectedDate.value = millis
                            }
                        }
                        showDatePicker.value = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker.value = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpenseCard(
    expense: ExpenseModel,
    selectedExpenses: MutableList<ExpenseModel>,
    modifier: Modifier = Modifier
) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val isSelected = selectedExpenses.contains(expense)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Bouncy scale animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bubble_scale"
    )
    
    // Animated elevation
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 2.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "card_elevation"
    )

    fun toggleExpenseSelection(expense: ExpenseModel) {
        if (selectedExpenses.contains(expense)) {
            selectedExpenses.remove(expense)
        } else {
            selectedExpenses.add(expense)
        }
    }
    
    val bubbleShape = RoundedCornerShape(
        topStart = if (expense.expenseType) 6.dp else 20.dp,
        topEnd = if (expense.expenseType) 20.dp else 6.dp,
        bottomStart = 20.dp,
        bottomEnd = 20.dp
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = if (expense.expenseType) Arrangement.Start else Arrangement.End
    ) {
        Card(
            shape = bubbleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
            colors = CardDefaults.cardColors(
                containerColor = if (expense.expenseType)
                    MaterialTheme.colorScheme.receiveCard
                else
                    MaterialTheme.colorScheme.payCard
            ),
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .then(
                    if (isSelected) Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = bubbleShape
                    ) else Modifier
                )
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        if (selectedExpenses.size > 0) {
                            toggleExpenseSelection(expense)
                        }
                    },
                    onLongClick = {
                        toggleExpenseSelection(expense)
                    }
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = if (expense.expenseType) Arrangement.Start else Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Selection indicator (receive side)
                AnimatedVisibility(
                    visible = isSelected && expense.expenseType,
                    enter = scaleIn(spring(stiffness = Spring.StiffnessHigh)) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Row {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = if (expense.expenseType) Alignment.Start else Alignment.End
                ) {
                    Text(
                        text = expense.expenseName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "₹${String.format("%,.2f", expense.expenseAmount)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (expense.expenseType) 
                            MaterialTheme.colorScheme.positiveAmount 
                        else 
                            MaterialTheme.colorScheme.negativeAmount
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = dateFormatter.format(Date(expense.expenseDate)),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Selection indicator (pay side)
                AnimatedVisibility(
                    visible = isSelected && !expense.expenseType,
                    enter = scaleIn(spring(stiffness = Spring.StiffnessHigh)) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Row {
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}