package com.SafeCryptoStocks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SafeCryptoStocks.model.Budget;
import com.SafeCryptoStocks.model.Expense;
import com.SafeCryptoStocks.services.BudgetService;
import com.SafeCryptoStocks.services.ExpenseService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private ExpenseService expenseService;

    // Create Budget
    @PostMapping
    public ResponseEntity<Budget> createBudget(@RequestBody Budget budget) {
        Budget createdBudget = budgetService.createBudget(budget);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBudget);
    }

//    // Get All Budgets
//    @GetMapping
//    public ResponseEntity<List<Budget>> getAllBudgets() {
//        List<Budget> budgets = budgetService.getAllBudgets();
//        return ResponseEntity.ok(budgets);
//    }

    @GetMapping
    public ResponseEntity<List<Budget>> getUserBudgets(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Handle case when userId is not in session
        }
        List<Budget> budgets = budgetService.getBudgetByUser(userId);
        return ResponseEntity.ok(budgets);
    }

    
    
    // Add a new expense to a budget
    @PostMapping("/{budgetId}/expenses")
    public ResponseEntity<?> addExpenseToBudget(@PathVariable Long budgetId, @RequestBody Expense expense) {
        try {
            Budget budget = budgetService.getBudgetById(budgetId);

            double totalExpenses = budget.getExpenses() + expense.getAmount();
            if (totalExpenses > budget.getAmount()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Out of Budget: Total expenses exceed the budget limit of ₹" + budget.getAmount());
            }

            expense.setBudget(budget);
            expenseService.createExpense(expense);

            budget.setExpenses(totalExpenses);
            budgetService.updateBudget(budget);

            return ResponseEntity.status(HttpStatus.CREATED).body(expense);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    
//    @PostMapping("bulk/{budgetId}/expenses")
//    public ResponseEntity<?> addBulkExpensesToBudget(@PathVariable Long budgetId, @RequestBody List<Expense> expenses) {
//        try {
//            Budget budget = budgetService.getBudgetById(budgetId);
//
//            double totalNewExpenses = expenses.stream().mapToDouble(Expense::getAmount).sum();
//            double totalExpenses = budget.getExpenses() + totalNewExpenses;
//            
//            if (totalExpenses > budget.getAmount()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body("Out of Budget: Total expenses exceed the budget limit of ₹" + budget.getAmount());
//            }
//
//            for (Expense expense : expenses) {
//                expense.setBudget(budget);
//                expenseService.createExpense(expense);
//            }
//
//            budget.setExpenses(totalExpenses);
//            budgetService.updateBudget(budget);
//
//            return ResponseEntity.status(HttpStatus.CREATED).body(expenses);
//        } catch (RuntimeException ex) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
//        }
//    }
    
    // Get Expenses for a Budget
    @GetMapping("/{budgetId}/expenses")
    public ResponseEntity<List<Expense>> getExpenses(@PathVariable Long budgetId) {
        try {
            List<Expense> expenses = budgetService.getExpensesByBudgetId(budgetId);
            return ResponseEntity.ok(expenses);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Delete a Budget
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long budgetId) {
        try {
            budgetService.deleteBudget(budgetId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    // Get a specific budget by ID
    @GetMapping("/{budgetId}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long budgetId) {
        try {
            Budget budget = budgetService.getBudgetById(budgetId);
            return ResponseEntity.ok(budget);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
