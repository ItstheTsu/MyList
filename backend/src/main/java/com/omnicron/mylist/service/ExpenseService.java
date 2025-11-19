package com.omnicron.mylist.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.omnicron.mylist.entity.Expense;
import com.omnicron.mylist.entity.User;
import com.omnicron.mylist.repository.ExpenseRepository;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
        } else {
            throw new RuntimeException("Gasto não encontrado para exclusão");
        }
    }

    public List<Expense> getAllExpensesByUser(User user) {
        return expenseRepository.findByUser(user);
    }

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

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despesa não encontrada"));
    }

    public Expense updateExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public Map<String, Double> getTotalsByType(User user) {
        List<Expense> expenses = getAllExpensesByUser(user);
        Map<String, Double> totals = new HashMap<>();
        for (Expense e : expenses) {
            String type = e.getType() != null ? e.getType().toString() : "OUTROS";
            totals.put(type, totals.getOrDefault(type, 0.0) + e.getAmount());
        }
        return totals;
    }

    public List<String> getAlerts(User user, double limite) {
        List<Expense> expenses = getAllExpensesByUser(user);
        List<String> alerts = new ArrayList<>();
        for (Expense e : expenses) {
            if (!e.getPaid() && e.getAmount() > limite) {
                alerts.add("Despesa \"" + e.getDescription() + "\" acima do limite de R$ " + limite);
            }
        }
        return alerts;
    }

    // Comparativo dos últimos 12 meses
    public Map<YearMonth, Double> getLast12Months(User user) {
        Map<YearMonth, Double> monthTotals = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();

        // Inicializa os últimos 12 meses com zero
        for (int i = 11; i >= 0; i--) {
            YearMonth ym = YearMonth.from(now.minusMonths(i));
            monthTotals.put(ym, 0.0);
        }

        // Soma despesas por mês
        List<Expense> expenses = getAllExpensesByUser(user);
        for (Expense e : expenses) {
            if (e.getDate() != null) {
                YearMonth ym = YearMonth.from(e.getDate());
                if (monthTotals.containsKey(ym)) {
                    monthTotals.put(ym, monthTotals.get(ym) + e.getAmount());
                }
            }
        }

        return monthTotals;
    }

    public Expense save(Expense expense) {
        Expense newExp = new Expense();

        newExp.setDescription(expense.getDescription());
        newExp.setAmount(expense.getAmount());
        newExp.setDate(expense.getDate());
        newExp.setType(expense.getType());
        newExp.setPaid(expense.getPaid());
        newExp.setUser(expense.getUser());
        newExp.setEndDate(expense.getEndDate());

        return expenseRepository.save(newExp);
    }

}
