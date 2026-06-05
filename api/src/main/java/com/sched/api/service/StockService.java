package com.sched.api.service;

import com.sched.api.domain.Company;
import com.sched.api.domain.Stock;
import com.sched.api.domain.User;
import com.sched.api.dto.request.StockRequest;
import com.sched.api.dto.response.StockBatchResponse;
import com.sched.api.dto.response.StockResponse;
import com.sched.api.exception.AccessDeniedException;
import com.sched.api.exception.ResourceNotFoundException;
import com.sched.api.repository.ProductRepository;
import com.sched.api.repository.StockRepository;
import com.sched.api.security.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Transactional(readOnly = true)
    public List<StockBatchResponse> getAll() {
        User authUser = authenticatedUserProvider.getCurrentUser();
        Long companyId = authUser.getCompany().getId();

        return stockRepository
                .findByProduct_Company_IdAndProduct_DeletedFalse(companyId)
                .stream()
                .map(this::mapToBatchResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StockResponse> getProductStockSummary() {
        User authUser = authenticatedUserProvider.getCurrentUser();
        Long companyId = authUser.getCompany().getId();

        List<Stock> allStocks = stockRepository.findByProduct_Company_IdAndProduct_DeletedFalse(companyId);

        Map<Long, List<Stock>> stocksByProduct = allStocks.stream()
                .collect(Collectors.groupingBy(stock -> stock.getProduct().getId()));

        return stocksByProduct.values().stream()
                .map(this::mapToProductSummary)
                .toList();
    }

    @Transactional
    public StockBatchResponse create(Long id, StockRequest dto) {
        User authUser = authenticatedUserProvider.getCurrentUser();
        Company company = authUser.getCompany();

        if(authUser.getDeleted() || company.getDeleted()){
            throw new AccessDeniedException();
        }

        var product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ResourceNotFoundException::new);

        Stock stock = Stock.builder()
                .quantity(dto.quantity())
                .expirationDate(dto.expirationDate())
                .product(product)
                .createdBy(authUser)
                .build();

        stock = stockRepository.save(stock);

        return mapToBatchResponse(stock);
    }

    @Transactional
    public StockBatchResponse update(Long id, StockRequest dto) {
        Stock stock = validateUserStockAccess(id);

        stock.setQuantity(dto.quantity());
        stock.setExpirationDate(dto.expirationDate());

        stock = stockRepository.save(stock);

        return mapToBatchResponse(stock);
    }

    @Transactional
    public void delete(Long id) {
        Stock stock = validateUserStockAccess(id);

        stockRepository.deleteById(stock.getId());
    }

    private Stock validateUserStockAccess(Long stockId) {
        User authUser = authenticatedUserProvider.getCurrentUser();
        Company company = authUser.getCompany();

        if(authUser.getDeleted() || company.getDeleted()){
            throw new AccessDeniedException();
        }

        Stock stock = stockRepository.findByIdAndProduct_DeletedFalse(stockId)
                .orElseThrow(ResourceNotFoundException::new);

        if(!Objects.equals(stock.getProduct().getCompany().getId(), company.getId())){
            throw new AccessDeniedException();
        }

        return stock;
    }

    private StockBatchResponse mapToBatchResponse(Stock stock) {
        return new StockBatchResponse(
                stock.getId(),
                stock.getProduct().getName(),
                stock.getProduct().getCategory(),
                stock.getProduct().getIsPerishable(),
                stock.getQuantity(),
                stock.getProduct().getUnitOfMeasure(),
                stock.getCreatedAt(),
                stock.getExpirationDate(),
                stock.getCreatedBy().getName()
        );
    }

    private StockResponse mapToProductSummary(List<Stock> productStocks) {
        var product = productStocks.get(0).getProduct();

        Integer totalQuantity = productStocks.stream()
                .mapToInt(Stock::getQuantity)
                .sum();

        LocalDateTime lastEntry = productStocks.stream()
                .map(Stock::getCreatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime nextToExpire = productStocks.stream()
                .map(Stock::getExpirationDate)
                .filter(date -> date != null && !date.isEqual(Stock.NO_EXPIRATION))
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime latestExpiration = productStocks.stream()
                .map(Stock::getExpirationDate)
                .filter(date -> date != null && !date.isEqual(Stock.NO_EXPIRATION))
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return new StockResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getIsPerishable(),
                product.getUnitOfMeasure(),
                totalQuantity,
                lastEntry,
                nextToExpire,
                latestExpiration
        );
    }
}