package com.omnicron.mylist.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.omnicron.mylist.entity.FinanceSummary;
import com.omnicron.mylist.repository.FinanceSummaryRepository;

@Service
public class FinanceSummaryService {

    @Autowired
    private FinanceSummaryRepository repo;

    public FinanceSummary getSummary() {
        return repo.findById(1L).orElseGet(() -> {
            FinanceSummary fs = new FinanceSummary();
            fs.setId(1L);
            return repo.save(fs);
        });
    }
}