package com.omnicron.mylist.controller;

import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omnicron.mylist.entity.Expense;
import com.omnicron.mylist.entity.User;
import com.omnicron.mylist.service.ExpenseService;
import com.omnicron.mylist.service.UserService;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    // Adicionar gasto
    @PostMapping
    public Expense addExpense(@RequestBody Expense expense) {
        User user = userService.getUserById(expense.getUser().getId());
        expense.setUser(user);
        return expenseService.addExpense(expense);
    }

    @GetMapping("/user/{userId}")
    public List<Expense> getAllExpenses(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return expenseService.getAllExpensesByUser(user);
    }

    @GetMapping("/user/{userId}/{year}/{month}")
    public List<Expense> getExpensesByMonth(@PathVariable Long userId,
            @PathVariable int year,
            @PathVariable int month) {
        User user = userService.getUserById(userId);
        YearMonth ym = YearMonth.of(year, month);
        return expenseService.getExpensesByUserAndMonth(user, ym);
    }

    // Marcar gasto como pago
    @PutMapping("/paid/{id}")
    public Expense updatePaidStatus(@PathVariable Long id, @RequestBody PaidStatusRequest request) {
        Expense expense = expenseService.getExpenseById(id);
        expense.setPaid(request.isPaid());
        return expenseService.updateExpense(expense);
    }

    public static class PaidStatusRequest {
        private boolean paid;

        public boolean isPaid() {
            return paid;
        }

        public void setPaid(boolean paid) {
            this.paid = paid;
        }
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
    }

}
