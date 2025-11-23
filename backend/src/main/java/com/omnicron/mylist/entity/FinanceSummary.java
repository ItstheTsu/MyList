package com.omnicron.mylist.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "finance_summary")
public class FinanceSummary {

    @Id
    private Long id;

    private Double totalInvestimentos = 0.0;
    private Double totalGasto = 0.0;

    public FinanceSummary() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTotalInvestimentos() {
        return totalInvestimentos;
    }

    public void setTotalInvestimentos(Double totalInvestimentos) {
        this.totalInvestimentos = totalInvestimentos;
    }

    public Double getTotalGasto() {
        return totalGasto;
    }

    public void setTotalGasto(Double totalGasto) {
        this.totalGasto = totalGasto;
    }
}