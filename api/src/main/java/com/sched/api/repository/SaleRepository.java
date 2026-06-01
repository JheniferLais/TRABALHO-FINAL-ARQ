package com.sched.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sched.api.domain.Sale;
import com.sched.api.dto.request.DemandDataRequest;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByProduct_Company_Id(Long companyId);
}