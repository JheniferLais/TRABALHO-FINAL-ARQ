# ADR-004 — Strategy + Registry para regras de alerta

## Status
Aceito

## Contexto
O sistema gera alertas de estoque de naturezas diferentes (vencimento próximo,
estoque baixo) e a tendência é surgirem novos tipos (ex.: ruptura, excesso). Um
bloco `if/else`/`switch` por tipo no service violaria o princípio Aberto-Fechado.

## Decisão
Modelar cada regra como uma **Strategy** implementando a interface
`StockAlertRule` (`type()` + `evaluate(companyId)`). O `AlertService` recebe por
injeção a `List<StockAlertRule>` e monta um **Registry**
(`Map<AlertType, StockAlertRule>`) indexado por `type()`. A seleção da regra é uma
busca no mapa, sem condicionais.

## Consequências
- (+) Adicionar um novo alerta = criar uma nova `@Component` que implementa
  `StockAlertRule`; o `AlertService` não muda (OCP).
- (+) Cada regra é testável isoladamente.
- (−) Mais classes pequenas — custo desprezível frente ao ganho de extensão.
