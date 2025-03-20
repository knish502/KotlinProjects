package com.example.budgetapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.*

// Data classes
data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val description: String,
    val isIncome: Boolean,
    val date: Date = Date()
)

// ViewModel
class BudgetViewModel : ViewModel() {
    var transactions by mutableStateOf<List<Transaction>>(emptyList())
        private set

    var description by mutableStateOf("")
    var amount by mutableStateOf("")

    val balance: Double
        get() = transactions.sumOf { if (it.isIncome) it.amount else -it.amount }

    val income: Double
        get() = transactions.filter { it.isIncome }.sumOf { it.amount }

    val expenses: Double
        get() = transactions.filter { !it.isIncome }.sumOf { it.amount }

    fun addTransaction(isIncome: Boolean) {
        val amountValue = amount.toDoubleOrNull() ?: return
        if (description.isBlank() || amountValue <= 0) return

        val newTransaction = Transaction(
            amount = amountValue,
            description = description,
            isIncome = isIncome
        )

        transactions = transactions + newTransaction
        clearInputs()
    }

    fun deleteTransaction(id: String) {
        transactions = transactions.filter { it.id != id }
    }

    private fun clearInputs() {
        description = ""
        amount = ""
    }
}

// Main Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BudgetApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetApp() {
    val navController = rememberNavController()
    val viewModel: BudgetViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simple Budget") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { navController.navigate("home") },
                    icon = { Text("📊") },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("add") },
                    icon = { Text("➕") },
                    label = { Text("Add") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("history") },
                    icon = { Text("📜") },
                    label = { Text("History") }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                DashboardScreen(viewModel)
            }
            composable("add") {
                AddTransactionScreen(viewModel, navController)
            }
            composable("history") {
                TransactionHistoryScreen(viewModel)
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: BudgetViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Current Balance",
                    fontSize = 18.sp
                )
                Text(
                    text = "$${String.format("%.2f", viewModel.balance)}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (viewModel.balance >= 0) Color.Green else Color.Red
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Income")
                    Text(
                        text = "$${String.format("%.2f", viewModel.income)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Green
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Expenses")
                    Text(
                        text = "$${String.format("%.2f", viewModel.expenses)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
            }
        }

        Text(
            text = "Recent Transactions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(vertical = 16.dp)
        )

        if (viewModel.transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No transactions yet",
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn {
                items(viewModel.transactions.take(5)) { transaction ->
                    TransactionItem(transaction, viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(viewModel: BudgetViewModel, navController: androidx.navigation.NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Transaction",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = viewModel.description,
            onValueChange = { viewModel.description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.amount,
            onValueChange = { viewModel.amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    viewModel.addTransaction(true)
                    navController.navigate("home")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green
                )
            ) {
                Text("Add Income")
            }

            Button(
                onClick = {
                    viewModel.addTransaction(false)
                    navController.navigate("home")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text("Add Expense")
            }
        }
    }
}

@Composable
fun TransactionHistoryScreen(viewModel: BudgetViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Transaction History",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (viewModel.transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No transactions yet",
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn {
                items(viewModel.transactions) { transaction ->
                    TransactionItem(transaction, viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItem(transaction: Transaction, viewModel: BudgetViewModel) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (transaction.isIncome)
                Color(0xFFE0F2E0) else Color(0xFFF2E0E0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = transaction.description,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateFormat.format(transaction.date),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${if (transaction.isIncome) "+" else "-"}$${String.format("%.2f", transaction.amount)}",
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.isIncome) Color.Green else Color.Red
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { viewModel.deleteTransaction(transaction.id) }) {
                    Text("🗑️")
                }
            }
        }
    }
}