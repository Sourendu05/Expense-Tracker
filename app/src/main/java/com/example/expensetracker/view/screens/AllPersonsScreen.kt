package com.example.expensetracker.view.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.example.expensetracker.ui.theme.positiveAmount
import com.example.expensetracker.ui.theme.negativeAmount
import com.example.expensetracker.ui.theme.topBarColor
import com.example.expensetracker.ui.theme.deleteIcon
import com.example.expensetracker.ui.theme.topBarText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.expensetracker.model.database.PersonDbTable
import com.example.expensetracker.view.ExpenseState
import com.example.expensetracker.view.navigation.PersonExpenseScreenUI
import com.example.expensetracker.viewModel.ExpenseViewModel
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllPersonsScreen(
    viewModel: ExpenseViewModel,
    state: State<ExpenseState>,
    navController: NavHostController
) {
    val showDialog = remember { mutableStateOf(false) }
    val selectedPersons = remember { mutableStateListOf<PersonDbTable>() }
    val allPersonsChecked = remember { mutableStateOf(false) }
    
    // Update allPersonsChecked based on selection
    allPersonsChecked.value = selectedPersons.size == state.value.personList.size && state.value.personList.isNotEmpty()
    
    Scaffold(
        topBar = {
            if (selectedPersons.size > 0) {
                // Selection Top Bar
                Row(
                    modifier = Modifier
                        .safeDrawingPadding()
                        .padding(top = 8.dp, start = 12.dp, end = 12.dp)
                        .height(65.dp)
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.topBarColor,
                            RoundedCornerShape(20.dp)
                        ),
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
                                checked = allPersonsChecked.value,
                                onCheckedChange = {
                                    if (it) {
                                        selectedPersons.clear()
                                        selectedPersons.addAll(state.value.personList)
                                    } else {
                                        selectedPersons.clear()
                                    }
                                }
                            )
                            Text(
                                text = "All",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.topBarText
                            )
                        }
                        Text(
                            text = "${selectedPersons.size} Selected",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.topBarText,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.deleteIcon,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(36.dp)
                            .clickable {
                                selectedPersons.forEach { person ->
                                    state.value.personId.value = person.id
                                    state.value.personName.value = person.personName
                                    state.value.amountReceivable.value = person.amountReceivable
                                    state.value.expenseList.value = person.expenseList
                                    viewModel.deletePerson()
                                }
                                selectedPersons.clear()
                            }
                    )
                }
            } else {
                // Normal Top Bar
                Row(
                    modifier = Modifier
                        .safeDrawingPadding()
                        .padding(top = 8.dp, start = 12.dp, end = 12.dp)
                        .height(65.dp)
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.topBarColor,
                            RoundedCornerShape(20.dp)
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Expense Tracker",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = MaterialTheme.colorScheme.topBarText,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            // Add Person Button
            FloatingActionButton(
                onClick = {
                    // Clear state to ensure we're adding a new person, not updating existing one
                    state.value.personId.value = null
                    state.value.personName.value = ""
                    state.value.amountReceivable.value = 0.0
                    state.value.expenseList.value = emptyList()
                    showDialog.value = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add Person",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .then(
                    if (selectedPersons.size > 0) {
                        Modifier.clickable {
                            selectedPersons.clear()
                        }
                    } else {
                        Modifier
                    }
                )
        ) {
            if(showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text("Add Person") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = state.value.personName.value,
                                onValueChange = { state.value.personName.value = it },
                                label = { Text("Person Name") }
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.upsertPerson()
                                // Clear state after adding new person
                                state.value.personId.value = null
                                state.value.personName.value = ""
                                state.value.amountReceivable.value = 0.0
                                state.value.expenseList.value = emptyList()
                                showDialog.value = false
                            }
                        ) {
                            Text("Add")
                        }
                    }
                )
            }
            if (state.value.personList.isEmpty()) {
                // Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                        //.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No persons added yet",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Tap + to add your first person",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else {
                // Person List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.value.personList) {
                        PersonCard(
                            person = it,
                            state = state,
                            navController = navController,
                            selectedPersons = selectedPersons
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PersonCard(
    person: PersonDbTable,
    state: State<ExpenseState>,
    navController: NavHostController,
    selectedPersons: MutableList<PersonDbTable>
) {
    val isSelected = selectedPersons.contains(person)
    val cardColor = if (isSelected) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }

    fun togglePersonSelection(person: PersonDbTable) {
        if (selectedPersons.contains(person)) {
            selectedPersons.remove(person)
        } else {
            selectedPersons.add(person)
        }
    }

    OutlinedCard(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier
            .padding(vertical = 6.dp, horizontal = 8.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (selectedPersons.size > 0) {
                        togglePersonSelection(person)
                    } else {
                        state.value.personId.value = person.id
                        state.value.personName.value = person.personName
                        state.value.amountReceivable.value = person.amountReceivable
                        state.value.expenseList.value = person.expenseList
                        navController.navigate(PersonExpenseScreenUI(person.id))
                    }
                },
                onLongClick = { togglePersonSelection(person) }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (isSelected) {
                    Checkbox(
                        checked = true,
                        onCheckedChange = { togglePersonSelection(person) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                
                Text(
                    text = person.personName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Text(
                text = "Rs. ${abs(person.amountReceivable)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif,
                color = if(person.amountReceivable > 0.0){
                    MaterialTheme.colorScheme.positiveAmount
                } else if(person.amountReceivable == 0.0) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.negativeAmount
                },
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}