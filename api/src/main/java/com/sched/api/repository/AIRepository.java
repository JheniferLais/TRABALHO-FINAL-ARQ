package com.sched.api.repository;

import com.sched.api.domain.Sale;
import com.sched.api.dto.request.AIDemandDataRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AIRepository extends JpaRepository<Sale, Long> {

    @Query("""
        SELECT new com.sched.api.dto.request.AIDemandDataRequest(
            p.id,
            p.name,
            p.category,
            p.price,
            CAST(SUM(s.totalSold) AS integer),
            CAST(EXTRACT(MONTH FROM MAX(s.saleDate)) AS integer),
            CAST(COALESCE(SUM(st.quantity), 0) AS long)
        )
        FROM Sale s
        JOIN s.product p
        LEFT JOIN Stock st ON st.product.id = p.id AND st.product.company.id = :companyId
        WHERE p.company.id = :companyId
        GROUP BY
            p.id,
            p.name,
            p.category,
            p.price
    """)
    List<AIDemandDataRequest> getDemandDataByCompany(
            @Param("companyId") Long companyId
    );
}
