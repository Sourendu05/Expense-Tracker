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
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.expensetracker.model.database.PersonDbTable
import com.example.expensetracker.ui.theme.accentGradientEnd
import com.example.expensetracker.ui.theme.accentGradientStart
import com.example.expensetracker.ui.theme.cardSurface
import com.example.expensetracker.ui.theme.deleteIcon
import com.example.expensetracker.ui.theme.negativeAmount
import com.example.expensetracker.ui.theme.positiveAmount
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
    
    allPersonsChecked.value = selectedPersons.size == state.value.personList.size && state.value.personList.isNotEmpty()
    
    val isSelectionMode = selectedPersons.size > 0

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
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.statusBarsPadding()
                    ) {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            navigationIcon = {
                                IconButton(onClick = { selectedPersons.clear() }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Clear selection",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            },
                            title = {
                                Text(
                                    text = "${selectedPersons.size} selected",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            actions = {
                                IconButton(onClick = {
                                    if (allPersonsChecked.value) {
                                        selectedPersons.clear()
                                    } else {
                                        selectedPersons.clear()
                                        selectedPersons.addAll(state.value.personList)
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Rounded.SelectAll,
                                        contentDescription = "Select all",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                IconButton(onClick = {
                                    selectedPersons.forEach { person ->
                                        state.value.personId.value = person.id
                                        state.value.personName.value = person.personName
                                        state.value.amountReceivable.value = person.amountReceivable
                                        state.value.expenseList.value = person.expenseList
                                        viewModel.deletePerson()
                                    }
                                    selectedPersons.clear()
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
                    // Normal Top Bar - Large expressive header
                    LargeTopAppBar(
                        colors = TopAppBarDefaults.largeTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        title = {
                            Column {
                                Text(
                                    text = "Expense",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Tracker",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Light,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !isSelectionMode,
                enter = scaleIn(spring(stiffness = Spring.StiffnessMedium)) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        state.value.personId.value = null
                        state.value.personName.value = ""
                        state.value.amountReceivable.value = 0.0
                        state.value.expenseList.value = emptyList()
                        showDialog.value = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 12.dp
                    ),
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(
                            text = "Add Person",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                )
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
                        ) { selectedPersons.clear() }
                    } else Modifier
                )
        ) {
            // Add Person Dialog
            if (showDialog.value) {
                PremiumAddPersonDialog(
                    personName = state.value.personName.value,
                    onPersonNameChange = { state.value.personName.value = it },
                    onDismiss = { showDialog.value = false },
                    onConfirm = {
                        viewModel.upsertPerson()
                        state.value.personId.value = null
                        state.value.personName.value = ""
                        state.value.amountReceivable.value = 0.0
                        state.value.expenseList.value = emptyList()
                        showDialog.value = false
                    }
                )
            }

            if (state.value.personList.isEmpty()) {
                // Empty State with premium feel
                EmptyStateContent()
            } else {
                // Person List with animations
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    
                    itemsIndexed(
                        items = state.value.personList,
                        key = { _, person -> person.id ?: person.personName }
                    ) { index, person ->
                        PersonCard(
                            person = person,
                            state = state,
                            navController = navController,
                            selectedPersons = selectedPersons,
                            index = index,
                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(durationMillis = 300, delayMillis = index * 30),
                                fadeOutSpec = tween(durationMillis = 200),
                                placementSpec = spring(stiffness = Spring.StiffnessMediumLow)
                            )
                        )
                    }
                    
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated gradient background circle
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "No people yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Tap the button below to add someone",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PremiumAddPersonDialog(
    personName: String,
    onPersonNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
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
                            colors = listOf(
                                MaterialTheme.colorScheme.accentGradientStart,
                                MaterialTheme.colorScheme.accentGradientEnd
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = "Add New Person",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = personName,
                    onValueChange = onPersonNameChange,
                    label = { Text("Name") },
                    placeholder = { Text("Enter person's name") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = personName.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
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
}

@Composable
fun PersonCard(
    person: PersonDbTable,
    state: State<ExpenseState>,
    navController: NavHostController,
    selectedPersons: MutableList<PersonDbTable>,
    index: Int = 0,
    modifier: Modifier = Modifier
) {
    val isSelected = selectedPersons.contains(person)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Smooth scale animation on press
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )
    
    // Animate elevation on selection
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 1.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "card_elevation"
    )
    
    // Animate background color
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.cardSurface,
        animationSpec = tween(200),
        label = "card_bg"
    )

    fun togglePersonSelection(person: PersonDbTable) {
        if (selectedPersons.contains(person)) {
            selectedPersons.remove(person)
        } else {
            selectedPersons.add(person)
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                if (selectedPersons.size > 0) {
                    togglePersonSelection(person)
                } else {
                    state.value.personId.value = person.id
                    state.value.personName.value = person.personName
                    state.value.amountReceivable.value = person.amountReceivable
                    state.value.expenseList.value = person.expenseList
                    navController.navigate(PersonExpenseScreenUI(person.id))
                }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Animated Avatar
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected)
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                    )
                                )
                            else
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        MaterialTheme.colorScheme.secondaryContainer
                                    )
                                )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = isSelected,
                        transitionSpec = {
                            scaleIn(spring(stiffness = Spring.StiffnessHigh)) + fadeIn() togetherWith
                            scaleOut() + fadeOut()
                        },
                        label = "avatar_content"
                    ) { selected ->
                        if (selected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        } else {
                            Text(
                                text = person.personName.take(1).uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = person.personName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (person.amountReceivable > 0) "You'll receive" 
                               else if (person.amountReceivable < 0) "You owe"
                               else "Settled up",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Amount with animated color
            val amountColor by animateColorAsState(
                targetValue = when {
                    person.amountReceivable > 0.0 -> MaterialTheme.colorScheme.positiveAmount
                    person.amountReceivable < 0.0 -> MaterialTheme.colorScheme.negativeAmount
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                animationSpec = tween(300),
                label = "amount_color"
            )
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${String.format("%,.0f", abs(person.amountReceivable))}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
            }
        }
    }
}