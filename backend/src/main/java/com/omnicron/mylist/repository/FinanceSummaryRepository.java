package com.omnicron.mylist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omnicron.mylist.entity.FinanceSummary;

@Repository
public interface FinanceSummaryRepository extends JpaRepository<FinanceSummary, Long> {
}