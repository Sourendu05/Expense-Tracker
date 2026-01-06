package com.example.expensetracker.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.expensetracker.view.screens.AllPersonsScreen
import com.example.expensetracker.view.screens.PersonExpensesScreen
import com.example.expensetracker.viewModel.ExpenseViewModel

@Composable
fun AppNavigation(viewModel: ExpenseViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val state = viewModel.state.collectAsState()
    NavHost(navController = navController, startDestination = AllPersonsScreenUI) {
        composable<AllPersonsScreenUI> {
            AllPersonsScreen(viewModel, state, navController)
        }
        composable<PersonExpenseScreenUI> {
            val data = it.toRoute<PersonExpenseScreenUI>()
            PersonExpensesScreen(viewModel, state, data.personId, navController)
        }
    }
}