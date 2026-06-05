package com.sched.api.service;

import com.sched.api.domain.Company;
import com.sched.api.domain.Product;
import com.sched.api.domain.Stock;
import com.sched.api.domain.User;
import com.sched.api.dto.request.ProductRequest;
import com.sched.api.dto.request.StockRequest;
import com.sched.api.dto.response.ProductResponse;
import com.sched.api.exception.AccessDeniedException;
import com.sched.api.exception.ProductHasStockException;
import com.sched.api.exception.ResourceNotFoundException;
import com.sched.api.repository.ProductRepository;
import com.sched.api.repository.StockRepository;
import com.sched.api.security.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final StockService stockService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        User authUser = authenticatedUserProvider.getCurrentUser();
        Company company = authUser.getCompany();

        if(authUser.getDeleted() || company.getDeleted()){
            throw new AccessDeniedException();
        }

        return productRepository.findAllByCompanyIdAndDeletedFalse(authUser.getCompany().getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = validateUserCompanyAccess(id);

        return new ProductResponse(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest dto) {
        User authUser = authenticatedUserProvider.getCurrentUser();
        Company company = authUser.getCompany();

        if(authUser.getDeleted() || company.getDeleted()){
            throw new AccessDeniedException();
        }

        Product product = Product.builder()
                .name(dto.name())
                .category(dto.category())
                .price(dto.price())
                .unitOfMeasure(dto.unitOfMeasure())
                .isPerishable(dto.isPerishable())
                .deleted(false)
                .company(company)
                .build();

        Product savedProduct = productRepository.save(product);

        StockRequest initialEmptyStock = new StockRequest(0, Stock.NO_EXPIRATION);

        stockService.create(savedProduct.getId(), initialEmptyStock);

        return new ProductResponse(product);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest updatedProduct) {
        Product product = validateUserCompanyAccess(id);

        product.setName(updatedProduct.name());
        product.setCategory(updatedProduct.category());
        product.setPrice(updatedProduct.price());
        product.setIsPerishable(updatedProduct.isPerishable());

        productRepository.save(product);

        return new ProductResponse(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = validateUserCompanyAccess(id);

        boolean hasActiveStock = stockRepository
                .existsByProductIdAndQuantityGreaterThanAndProduct_DeletedFalse(product.getId(), 0);

        if (hasActiveStock) {
            throw new ProductHasStockException();
        }

        product.setDeleted(true);

        productRepository.save(product);
    }

    private Product validateUserCompanyAccess(Long productId) {
        User authUser = authenticatedUserProvider.getCurrentUser();
        Company company = authUser.getCompany();

        if(authUser.getDeleted() || company.getDeleted()){
            throw new AccessDeniedException();
        }

        Product product = productRepository.findByIdAndDeletedFalse(productId)
                .orElseThrow(ResourceNotFoundException::new);

        if(!Objects.equals(product.getCompany().getId(), company.getId())){
            throw new AccessDeniedException();
        }

        return product;
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(product);
    }
}