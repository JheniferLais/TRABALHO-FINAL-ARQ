package com.sched.api.service;

import com.sched.api.domain.Product;
import com.sched.api.domain.Sale;
import com.sched.api.domain.Stock;
import com.sched.api.domain.User;
import com.sched.api.dto.request.SaleRequest;
import com.sched.api.dto.response.SaleResponse;
import com.sched.api.exception.InsufficientStockException;
import com.sched.api.exception.ResourceNotFoundException;
import com.sched.api.repository.ProductRepository;
import com.sched.api.repository.SaleRepository;
import com.sched.api.repository.StockRepository;
import com.sched.api.security.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public List<SaleResponse> getAll() {
        User authUser = authenticatedUserProvider.getCurrentUser();

        Long companyId = authUser.getCompany().getId();

        List<Sale> sales = saleRepository.findByProduct_Company_Id(companyId);

        return sales.stream()
                .map(SaleResponse::new)
                .toList();
    }

    @Transactional
    public SaleResponse create(Long id, SaleRequest dto) {
        User authUser = authenticatedUserProvider.getCurrentUser();

        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ResourceNotFoundException::new);

        List<Stock> stocks = stockRepository
                .findByProductIdAndProduct_DeletedFalseOrderByExpirationDateAsc(product.getId());

        int quantityToSell = dto.totalSold();

        int totalAvailable = stocks.stream()
                .mapToInt(Stock::getQuantity)
                .sum();

        if (totalAvailable < quantityToSell) {
            throw new InsufficientStockException();
        }

        for (Stock stock : stocks) {

            if (quantityToSell == 0) break;

            int available = stock.getQuantity();

            if (available <= quantityToSell) {
                quantityToSell -= available;
                stock.setQuantity(0);
            } else {
                stock.setQuantity(available - quantityToSell);
                quantityToSell = 0;
            }

            stockRepository.save(stock);
        }

        double totalPrice = product.getPrice() * dto.totalSold();

        Sale sale = Sale.builder()
                .totalSold(dto.totalSold())
                .totalPrice(totalPrice)
                .product(product)
                .soldBy(authUser)
                .build();

        return new SaleResponse(saleRepository.save(sale));
    }
}
