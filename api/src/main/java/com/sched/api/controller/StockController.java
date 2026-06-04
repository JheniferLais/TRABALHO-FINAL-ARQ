package com.sched.api.controller;

import com.sched.api.dto.request.StockRequest;
import com.sched.api.dto.response.StockBatchResponse;
import com.sched.api.dto.response.StockResponse;
import com.sched.api.service.StockService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock")
public class StockController {

    private StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<StockBatchResponse> getAll() {
        return stockService.getAll();
    }

    @GetMapping("/filterProduct")
    public List<StockResponse> findById() {
        return stockService.getProductStockSummary();
    }

    @PostMapping("/{id}")
    public StockBatchResponse create(@PathVariable Long id, @Valid @RequestBody StockRequest dto) {
        return stockService.create(id, dto);
    }

    @PutMapping("/{id}")
    public StockBatchResponse update(@PathVariable Long id, @Valid @RequestBody StockRequest dto) {
        return stockService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        stockService.delete(id);
    }

}
