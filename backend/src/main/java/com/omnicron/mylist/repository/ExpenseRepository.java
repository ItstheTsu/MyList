package com.omnicron.mylist.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omnicron.mylist.model.Expense;
import com.omnicron.mylist.model.ExpenseType;
import com.omnicron.mylist.model.User;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser(User user);

    List<Expense> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);

    List<Expense> findByUserAndType(User user, ExpenseType type);

}
