package com.example.expensetracker.view.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.expensetracker.model.database.ExpenseModel
import com.example.expensetracker.ui.theme.deleteIcon
import com.example.expensetracker.ui.theme.dialogButtonPay
import com.example.expensetracker.ui.theme.dialogButtonReceive
import com.example.expensetracker.ui.theme.negativeAmount
import com.example.expensetracker.ui.theme.payCard
import com.example.expensetracker.ui.theme.personTopBarBackground
import com.example.expensetracker.ui.theme.personTopBarText
import com.example.expensetracker.ui.theme.positiveAmount
import com.example.expensetracker.ui.theme.receiveCard
import com.example.expensetracker.view.ExpenseState
import com.example.expensetracker.viewModel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PersonExpensesScreen(
    viewModel: ExpenseViewModel,
    state: State<ExpenseState>,
    personId: Int?,
    navController: NavHostController
) {
    val showDialog = remember { mutableStateOf(false) }
    val expenseType = remember { mutableStateOf(true) }
    val selectedExpenses = remember { mutableStateListOf<ExpenseModel>() }
    val allExpensesChecked = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    
    // Update allExpensesChecked based on selection
    allExpensesChecked.value = selectedExpenses.size == state.value.expenseList.value.size && state.value.expenseList.value.isNotEmpty()
    
    // Auto-scroll to bottom (latest expense) when list changes - WhatsApp-like behavior
    LaunchedEffect(state.value.expenseList.value.size) {
        if (state.value.expenseList.value.isNotEmpty()) {
            listState.animateScrollToItem(0) // In reverse layout, 0 is the bottom (latest)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .then(
                if (selectedExpenses.size > 0) {
                    Modifier.clickable { selectedExpenses.clear() }
                } else {
                    Modifier
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // top bar - shows selection mode when expenses are selected
        if (selectedExpenses.size > 0) {
            // Selection Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.personTopBarBackground,
                        shape = RoundedCornerShape(20.dp)
                    ).weight(0.1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(0.8f)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Checkbox(
                            checked = allExpensesChecked.value,
                            onCheckedChange = {
                                if (it) {
                                    selectedExpenses.clear()
                                    selectedExpenses.addAll(state.value.expenseList.value)
                                } else {
                                    selectedExpenses.clear()
                                }
                            }
                        )
                        Text(
                            text = "All",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.personTopBarText
                        )
                    }
                    Text(
                        text = "${selectedExpenses.size} Selected",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.personTopBarText,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.deleteIcon,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(32.dp)
                        .clickable {
                            // Delete selected expenses and update amounts
                            selectedExpenses.forEach { expense ->
                                // Reverse the amount change for each deleted expense
                                val amountChange = if (expense.expenseType) {
                                    -expense.expenseAmount // Was receiving, now subtract
                                } else {
                                    expense.expenseAmount // Was paying, now add back
                                }
                                state.value.amountReceivable.value += amountChange
                            }
                            
                            // Remove selected expenses from the list
                            val updatedExpenses = state.value.expenseList.value.toMutableList()
                            updatedExpenses.removeAll(selectedExpenses)
                            state.value.expenseList.value = updatedExpenses
                            
                            state.value.personId.value = personId
                            
                            // Update in database
                            viewModel.upsertPerson()
                            
                            // Clear selection
                            selectedExpenses.clear()
                        }
                )
            }
        } else {
            // Normal Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.personTopBarBackground,
                        shape = RoundedCornerShape(20.dp)
                    ).weight(0.1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.value.personName.value,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.personTopBarText,
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = "Rs. ${abs(state.value.amountReceivable.value)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.SansSerif,
                    color = if(state.value.amountReceivable.value > 0.0){
                        MaterialTheme.colorScheme.positiveAmount
                    } else if(state.value.amountReceivable.value == 0.0) {
                        MaterialTheme.colorScheme.personTopBarText
                    } else {
                        MaterialTheme.colorScheme.negativeAmount
                    },
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
        
        // all transactions - WhatsApp-like: latest at bottom, scrolls from bottom
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(10.dp).weight(0.8f),
            state = listState,
            reverseLayout = true // Makes list scroll from bottom, showing latest items first
        ) {
            items(state.value.expenseList.value.reversed()) { expense ->
                ExpenseCard(
                    expense = expense,
                    selectedExpenses = selectedExpenses
                )
            }
        }
        
        // Lower 2 buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .weight(0.1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    expenseType.value = true // Receive money
                    showDialog.value = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.dialogButtonReceive
                ),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp).weight(0.5f)
            ) {
                Text(
                    text = "Receive",
                    fontSize = 20.sp
                )
            }
            Button(
                onClick = {
                    expenseType.value = false // Pay money
                    showDialog.value = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.dialogButtonPay
                ),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp).weight(0.5f)
            ) {
                Text(
                    text = "Pay",
                    fontSize = 20.sp
                )
            }
        }
        
        // dialog box
        if (showDialog.value) {
            val expenseName = remember { mutableStateOf("")  }
            val expenseAmount = remember { mutableStateOf("") }
            val selectedDate = remember { mutableStateOf(System.currentTimeMillis()) }
            val showDatePicker = remember { mutableStateOf(false) }
            val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
            
            // Validate form: Enable "Add" button only when both fields are filled
            val isAddButtonEnabled by remember {
                derivedStateOf {
                    expenseName.value.isNotBlank() && expenseAmount.value.isNotBlank()
                }
            }
            
            // Create date picker state once and keep it in memory
            // This prevents crashes and freezing
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate.value,
                selectableDates = object : SelectableDates {
                    // Only allow dates up to today (disable future dates)
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis <= System.currentTimeMillis()
                    }
                }
            )

            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Add Expense") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = expenseName.value,
                            onValueChange = { expenseName.value = it },
                            label = { Text("Expense Detail") },
                            singleLine = true,
                            maxLines = 1
                        )
                        OutlinedTextField(
                            value = expenseAmount.value,
                            onValueChange = { expenseAmount.value = it },
                            label = { Text("Amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = dateFormatter.format(Date(selectedDate.value)),
                            onValueChange = { },
                            label = { Text("Date") },
                            readOnly = true,
                            modifier = Modifier.padding(top = 8.dp),
                            trailingIcon = {
                                TextButton(onClick = { showDatePicker.value = true }) {
                                    Text("Select")
                                }
                            }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Create new expense
                            val newExpense = ExpenseModel(
                                expenseName = expenseName.value,
                                expenseAmount = expenseAmount.value.toDouble(),
                                expenseType = expenseType.value,
                                expenseDate = selectedDate.value
                            )
                            
                            // Add to existing list (create new list since List is immutable)
                            val updatedExpenses = state.value.expenseList.value + newExpense
                            state.value.expenseList.value = updatedExpenses
                            
                            // Update the total amount
                            val amountChange = if (expenseType.value) {
                                expenseAmount.value.toDouble() // Receiving money (positive)
                            } else {
                                -expenseAmount.value.toDouble() // Paying money (negative)
                            }
                            state.value.amountReceivable.value += amountChange

                            state.value.personId.value = personId
                            
                            // Update in database
                            viewModel.upsertPerson()
                            
                            // Clear dialog
                            expenseName.value = ""
                            expenseAmount.value = ""
                            showDialog.value = false
                        },
                        enabled = isAddButtonEnabled, // Button disabled when fields are empty
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (expenseType.value) {
                                MaterialTheme.colorScheme.dialogButtonReceive
                            } else {
                                MaterialTheme.colorScheme.dialogButtonPay
                            }
                        )
                    ) {
                        Text("Add")
                    }
                }
            )
            
            // DatePicker Dialog - Simplified and optimized
            if (showDatePicker.value) {
                DatePickerDialog(
                    onDismissRequest = { 
                        showDatePicker.value = false 
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                // Only accept dates that are today or in the past
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
                        TextButton(
                            onClick = { 
                                showDatePicker.value = false 
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpenseCard(
    expense: ExpenseModel,
    selectedExpenses: MutableList<ExpenseModel>
) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val isSelected = selectedExpenses.contains(expense)
    
    fun toggleExpenseSelection(expense: ExpenseModel) {
        if (selectedExpenses.contains(expense)) {
            selectedExpenses.remove(expense)
        } else {
            selectedExpenses.add(expense)
        }
    }
    
    // Chat-like layout: Received money on left, Paid money on right
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = {
                    if (selectedExpenses.size > 0) {
                        toggleExpenseSelection(expense)
                    }
                },
                onLongClick = { toggleExpenseSelection(expense) }
            ),
        horizontalArrangement = if (expense.expenseType) {
            Arrangement.Start // Received money - left side like incoming messages
        } else {
            Arrangement.End   // Paid money - right side like sent messages
        }
    ) {
        OutlinedCard(
            shape = RoundedCornerShape(
                topStart = if (expense.expenseType) 4.dp else 15.dp,
                topEnd = if (expense.expenseType) 15.dp else 4.dp,
                bottomStart = 15.dp,
                bottomEnd = 15.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (expense.expenseType) {
                    MaterialTheme.colorScheme.receiveCard // Keep green for receiving
                } else {
                    MaterialTheme.colorScheme.payCard // Keep red for paying
                }
            ),
            border = BorderStroke(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth(0.65f) // Take only 75% width like chat bubbles
                .padding(
                    start = if (expense.expenseType) 8.dp else 32.dp,
                    end = if (expense.expenseType) 32.dp else 8.dp
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (expense.expenseType) {
                    Arrangement.Start
                } else {
                    Arrangement.End
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelected && expense.expenseType) {
                    Checkbox(
                        checked = true,
                        onCheckedChange = { toggleExpenseSelection(expense) },
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp),
                    horizontalAlignment = if (expense.expenseType) {
                        Alignment.Start // Align text to start for received money
                    } else {
                        Alignment.End   // Align text to end for paid money
                    }
                ) {
                    Text(
                        text = expense.expenseName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "Rs. ${expense.expenseAmount}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (expense.expenseType) MaterialTheme.colorScheme.positiveAmount else MaterialTheme.colorScheme.negativeAmount
                    )
                    
                    Text(
                        text = dateFormatter.format(Date(expense.expenseDate)),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                if (isSelected && !expense.expenseType) {
                    Checkbox(
                        checked = true,
                        onCheckedChange = { toggleExpenseSelection(expense) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }
        }
    }
}