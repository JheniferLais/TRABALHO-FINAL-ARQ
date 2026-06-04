package com.sched.api;

import com.sched.api.domain.Company;
import com.sched.api.domain.Product;
import com.sched.api.domain.Stock;
import com.sched.api.dto.response.AlertResponse;
import com.sched.api.repository.AlertRepository;
import com.sched.api.service.alert.AlertType;
import com.sched.api.service.alert.LowStockRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LowStockRuleTest {

    private static final Long COMPANY_ID = 1L;
    private static final Integer LOW_STOCK_THRESHOLD = 20;

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private LowStockRule rule;

    private Stock stock;

    @BeforeEach
    void setUp() {
        stock = criarEstoque();
    }

    @Test
    void type_RetornaLowStock() {
        assertEquals(AlertType.LOW_STOCK, rule.type());
    }

    @Test
    void evaluate_QuandoExistemProdutosComEstoqueBaixo_RetornaAlertas() {
        when(alertRepository.findByQuantityLessThanEqualAndProduct_Company_IdAndProduct_DeletedFalse(
                LOW_STOCK_THRESHOLD, COMPANY_ID))
                .thenReturn(List.of(stock));

        final List<AlertResponse> response = rule.evaluate(COMPANY_ID);

        assertEquals(1, response.size());
        verify(alertRepository).findByQuantityLessThanEqualAndProduct_Company_IdAndProduct_DeletedFalse(
                LOW_STOCK_THRESHOLD, COMPANY_ID);
    }

    @Test
    void evaluate_QuandoNaoExistemProdutosComEstoqueBaixo_RetornaListaVazia() {
        when(alertRepository.findByQuantityLessThanEqualAndProduct_Company_IdAndProduct_DeletedFalse(
                LOW_STOCK_THRESHOLD, COMPANY_ID))
                .thenReturn(List.of());

        assertTrue(rule.evaluate(COMPANY_ID).isEmpty());
    }

    private Stock criarEstoque() {
        final Company company = Company.builder()
                .id(COMPANY_ID)
                .name("Empresa Teste")
                .deleted(false)
                .build();

        final Product product = Product.builder()
                .id(1L)
                .name("Produto A")
                .category("Categoria X")
                .unitOfMeasure("UN")
                .isPerishable(true)
                .deleted(false)
                .company(company)
                .build();

        return Stock.builder()
                .id(1L)
                .quantity(10)
                .expirationDate(LocalDateTime.of(2026, 2, 1, 10, 0))
                .product(product)
                .build();
    }
}
