# Diagrama 2 — Diagrama de classes

Destaca o domínio (entidades e relações) e os dois padrões estruturais/de
comportamento centrais: **Strategy** (alertas) e **Adapter** (IA),
além do **Builder**.

## Domínio

## Padrão Strategy (alertas)
![Diagrama de Classes - Strategy](./imgs/02-diagrama-de-classe(STRATEGY).png)

## Padrão Adapter (IA)
![Diagrama de Classes - Adapter](./imgs/02-diagrama-de-classe(ADAPTER).png)

## Padrão Builder
![Diagrama de Classes - Builder](./imgs/02-diagrama-de-classe(BUILDER).png)

<details>
<summary><b>Código-fonte Mermaid (Clique para expandir)</b></summary>

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
    class AIService {
        -AIRepository aiRepository
        -DemandForecastPort demandForecastPort
        +getPredictions() List~AIPredictionResponse~
        +getDemandData() List~AIDemandDataRequest~
    }

    class DemandForecastPort {
        <<interface>>
        +forecast(List~AIDemandDataRequest~) List~AIPredictionResponse~
    }

    class FlaskDemandForecastClient {
        -RestTemplate restTemplate
        -String forecastUrl
        +forecast(List~AIDemandDataRequest~) List~AIPredictionResponse~
    }

    class RestTemplate {
        +postForObject(url, body, type)
    }

    AIService --> DemandForecastPort : 
    DemandForecastPort <|.. FlaskDemandForecastClient
    FlaskDemandForecastClient --> RestTemplate : adapta HTTP -> Flask /predict
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