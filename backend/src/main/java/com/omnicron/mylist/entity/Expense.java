package com.omnicron.mylist.entity;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description; // descrição do gasto
    private Double amount; // valor
    private LocalDate date; // data do gasto
    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ExpenseType type; // tipo do gasto (enum)

    private Boolean paid = false; // se o gasto foi pago ou não

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference // aqui para evitar o loop infinito
    private User user; // dono do gasto

    // ----- Construtores -----
    public Expense() {
    }

    public Expense(String description, Double amount, LocalDate date, ExpenseType type, User user) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.user = user;
        this.paid = false;
    }

    // ----- Getters e Setters -----
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ExpenseType getType() {
        return type;
    }

    public void setType(ExpenseType type) {
        this.type = type;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    // ----- equals e hashCode -----
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Expense))
            return false;
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ----- toString -----
    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", type=" + type +
                ", paid=" + paid +
                '}';
    }
}
