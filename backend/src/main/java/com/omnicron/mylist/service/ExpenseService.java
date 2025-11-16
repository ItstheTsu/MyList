package com.omnicron.mylist.service;

import com.omnicron.mylist.entity.Expense;
import com.omnicron.mylist.entity.User;
import com.omnicron.mylist.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    // Adicionar gasto
    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    // Buscar gastos de um usuário em um mês
    public List<Expense> getExpensesByUserAndMonth(User user, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        return expenseRepository.findByUserAndDateBetween(user, start, end);
    }

    // Marcar gasto como pago
    public void markAsPaid(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Gasto não encontrado"));
        expense.setPaid(true);
        expenseRepository.save(expense);
    }

    // Listar todos os gastos de um usuário
    public List<Expense> getAllExpensesByUser(User user) {
        return expenseRepository.findByUser(user);
    }

    // Aqui você pode adicionar métodos para:
    // - Calcular totais por tipo
    // - Gerar alertas
    // - Comparativo de 12 meses
}
