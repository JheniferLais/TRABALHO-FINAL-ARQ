# Diagrama 2 — Diagrama de classes

Destaca o domínio (entidades e relações) e os dois padrões estruturais/de
comportamento centrais: **Strategy** (alertas) e **Adapter** (IA),
além do **Builder**.

## Domínio

## Geral
![Diagrama de Classes - Geral](./imgs/02-diagrama-de-classe(GERAL).png)

## Padrão Strategy (alertas)
![Diagrama de Classes - Strategy](./imgs/02-diagrama-de-classe(STRATEGY).png)

## Padrão Adapter (IA)
![Diagrama de Classes - Adapter](./imgs/02-diagrama-de-classe(ADAPTER).png)

## Padrão Builder
![Diagrama de Classes - Builder](./imgs/02-diagrama-de-classe(BUILDER).png)

<details>
<summary><b>Código-fonte Mermaid (Clique para expandir)</b></summary>

## Geral

```mermaid
classDiagram
    class Company {
        +Long id
        +String name
        +String cnpj
        +Boolean deleted
        +LocalDateTime createdAt
    }
    class User {
        +Long id
        +String name
        +String email
        +String password
        +Role role
        +Boolean deleted
    }
    class Product {
        +Long id
        +String name
        +String category
        +Double price
        +String unitOfMeasure
        +Boolean isPerishable
        +Boolean deleted
    }
    class Stock {
        +Long id
        +Integer quantity
        +LocalDateTime expirationDate
        +LocalDateTime createdAt
        +NO_EXPIRATION$ LocalDateTime
    }
    class Sale {
        +Long id
        +Integer totalSold
        +Double totalPrice
        +LocalDateTime saleDate
    }
    class Role {
        <<enumeration>>
        ADMIN
        USER
    }

    Company "1" o-- "*" User : possui
    Company "1" o-- "*" Product : possui
    Product "1" o-- "*" Stock : tem lotes
    Product "1" o-- "*" Sale : vendido em
    User "1" o-- "*" Stock : createdBy
    User "1" o-- "*" Sale : soldBy
    User --> Role
```

## Padrão Strategy (alertas)

```mermaid
classDiagram
    class StockAlertRule {
        <<interface>>
        +AlertType type()
        +List~AlertResponse~ evaluate(Long companyId)
    }
    class LowStockRule {
        +AlertType type()
        +evaluate(companyId)
    }
    class ExpiringStockRule {
        +AlertType type()
        +evaluate(companyId)
    }
    class AlertService {
        -Map~AlertType,StockAlertRule~ rulesByType
        +getProductsWithLowStock()
        +getProductsExpiringInNext30Days()
    }
    class AlertType {
        <<enumeration>>
        EXPIRING
        LOW_STOCK
    }
    StockAlertRule <|.. LowStockRule
    StockAlertRule <|.. ExpiringStockRule
    AlertService o-- "*" StockAlertRule : Registry (injeta List)
    StockAlertRule --> AlertType
```

## Padrão Adapter (IA)

```mermaid
classDiagram
    class DemandForecastPort {
        <<interface>>
        +List~AIPredictionResponse~ forecast(List~AIDemandDataRequest~)
    }
    class FlaskDemandForecastClient {
        -RestTemplate restTemplate
        -String forecastUrl
        +forecast(demandData)
    }
    class AIService {
        +getPredictions()
    }
    DemandForecastPort <|.. FlaskDemandForecastClient
    AIService --> DemandForecastPort : depende da abstração
```

## Padrão Builder

```mermaid
classDiagram
    class ProductService {
        +create(ProductRequest dto) ProductResponse
    }
    class ProductBuilder {
        +name(String name) ProductBuilder
        +category(String category) ProductBuilder
        +price(Double price) ProductBuilder
        +unitOfMeasure(String unitOfMeasure) ProductBuilder
        +isPerishable(Boolean isPerishable) ProductBuilder
        +deleted(Boolean deleted) ProductBuilder
        +company(Company company) ProductBuilder
        +build() Product
    }
    class Product {
        -Long id
        -String name
        -String category
        -Double price
        -String unitOfMeasure
        -Boolean isPerishable
        -LocalDateTime createdAt
        -Boolean deleted
        -Company company
    }

    ProductService ..> ProductBuilder : usa para construir
    ProductBuilder --> Product : instancia
```