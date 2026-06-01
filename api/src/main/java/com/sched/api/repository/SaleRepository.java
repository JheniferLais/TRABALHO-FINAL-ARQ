package com.sched.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sched.api.domain.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByProduct_Company_Id(Long companyId);
}