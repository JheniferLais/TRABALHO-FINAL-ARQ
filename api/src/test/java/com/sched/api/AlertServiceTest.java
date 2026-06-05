package com.sched.api;

import com.sched.api.domain.Company;
import com.sched.api.domain.User;
import com.sched.api.dto.response.AlertResponse;
import com.sched.api.security.AuthenticatedUserProvider;
import com.sched.api.service.AlertService;
import com.sched.api.service.alert.AlertType;
import com.sched.api.service.alert.StockAlertRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    private static final Long COMPANY_ID = 1L;

    @Mock
    private StockAlertRule expiringRule;

    @Mock
    private StockAlertRule lowStockRule;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    private AlertService alertService;
    private User authenticatedUser;

    @BeforeEach
    void setUp() {
        when(expiringRule.type()).thenReturn(AlertType.EXPIRING);
        when(lowStockRule.type()).thenReturn(AlertType.LOW_STOCK);

        alertService = new AlertService(List.of(expiringRule, lowStockRule), authenticatedUserProvider);
        authenticatedUser = criarUsuarioAutenticado();
    }

    @Test
    void getProductsExpiringInNext30Days_DelegaParaRegraDeVencimento() {
        // Arrange
        final List<AlertResponse> expected = List.of(criarAlerta());
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(authenticatedUser);
        when(expiringRule.evaluate(COMPANY_ID)).thenReturn(expected);

        // Act
        final List<AlertResponse> response = alertService.getProductsExpiringInNext30Days();

        // Assert
        assertEquals(expected, response);
        verify(expiringRule).evaluate(COMPANY_ID);
        verify(lowStockRule, never()).evaluate(COMPANY_ID);
    }

    @Test
    void getProductsWithLowStock_DelegaParaRegraDeEstoqueBaixo() {
        // Arrange
        final List<AlertResponse> expected = List.of(criarAlerta());
        when(authenticatedUserProvider.getCurrentUser()).thenReturn(authenticatedUser);
        when(lowStockRule.evaluate(COMPANY_ID)).thenReturn(expected);

        // Act
        final List<AlertResponse> response = alertService.getProductsWithLowStock();

        // Assert
        assertEquals(expected, response);
        verify(lowStockRule).evaluate(COMPANY_ID);
        verify(expiringRule, never()).evaluate(COMPANY_ID);
    }

    private User criarUsuarioAutenticado() {
        final Company company = Company.builder()
                .id(COMPANY_ID)
                .name("Empresa Teste")
                .deleted(false)
                .build();

        return User.builder()
                .company(company)
                .deleted(false)
                .build();
    }

    private AlertResponse criarAlerta() {
        return new AlertResponse(
                1L,
                LocalDateTime.of(2026, 2, 1, 10, 0),
                10,
                "Produto A",
                "UN"
        );
    }
}
