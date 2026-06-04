package com.sched.api;

import com.sched.api.domain.Company;
import com.sched.api.domain.Product;
import com.sched.api.domain.Stock;
import com.sched.api.dto.response.AlertResponse;
import com.sched.api.repository.AlertRepository;
import com.sched.api.service.alert.AlertType;
import com.sched.api.service.alert.ExpiringStockRule;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpiringStockRuleTest {

    private static final Long COMPANY_ID = 1L;

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private ExpiringStockRule rule;

    private Stock stock;

    @BeforeEach
    void setUp() {
        stock = criarEstoque();
    }

    @Test
    void type_RetornaExpiring() {
        assertEquals(AlertType.EXPIRING, rule.type());
    }

    @Test
    void evaluate_QuandoExistemProdutosAVencer_RetornaAlertas() {
        when(alertRepository.findByExpirationDateBetweenAndProduct_Company_IdAndProduct_DeletedFalse(
                any(LocalDateTime.class), any(LocalDateTime.class), eq(COMPANY_ID)))
                .thenReturn(List.of(stock));

        final List<AlertResponse> response = rule.evaluate(COMPANY_ID);

        assertEquals(1, response.size());
        verify(alertRepository).findByExpirationDateBetweenAndProduct_Company_IdAndProduct_DeletedFalse(
                any(LocalDateTime.class), any(LocalDateTime.class), eq(COMPANY_ID));
    }

    @Test
    void evaluate_QuandoNaoExistemProdutosAVencer_RetornaListaVazia() {
        when(alertRepository.findByExpirationDateBetweenAndProduct_Company_IdAndProduct_DeletedFalse(
                any(LocalDateTime.class), any(LocalDateTime.class), eq(COMPANY_ID)))
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
