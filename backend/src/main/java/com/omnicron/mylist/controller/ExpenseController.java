package com.omnicron.mylist.controller;

import java.time.YearMonth;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.omnicron.mylist.dto.PaidStatusRequest;
import com.omnicron.mylist.entity.Expense;
import com.omnicron.mylist.entity.User;
import com.omnicron.mylist.service.ExpenseService;
import com.omnicron.mylist.service.UserService;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserService userService;

    public ExpenseController(ExpenseService expenseService, UserService userService) {
        this.expenseService = expenseService;
        this.userService = userService;
    }

    // Criar gasto
    @PostMapping
    public Expense addExpense(@RequestBody Expense expense) {
        User user = userService.getUserById(expense.getUser().getId());
        expense.setUser(user);
        return expenseService.addExpense(expense);
    }

    // Listar todos por usuário
    @GetMapping("/user/{userId}")
    public List<Expense> getAllExpenses(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return expenseService.getAllExpensesByUser(user);
    }

    // Listar por mês específico
    @GetMapping("/user/{userId}/{year}/{month}")
    public List<Expense> getExpensesByMonth(
            @PathVariable Long userId,
            @PathVariable int year,
            @PathVariable int month) {
        User user = userService.getUserById(userId);
        YearMonth ym = YearMonth.of(year, month);
        return expenseService.getExpensesByUserAndMonth(user, ym);
    }

    // Marcar como pago / não pago
    @PutMapping("/paid/{id}")
    public Expense updatePaidStatus(@PathVariable Long id, @RequestBody PaidStatusRequest request) {
        Expense expense = expenseService.getExpenseById(id);
        expense.setPaid(request.isPaid());
        System.out.println("Atualizado gasto: " + expense.getDescription() + " Do usuário: " + expense.getUser().getName() + " para pago=" + request.isPaid());

        return expenseService.updateExpense(expense);
    }

    // Deletar gasto
    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
    }
}