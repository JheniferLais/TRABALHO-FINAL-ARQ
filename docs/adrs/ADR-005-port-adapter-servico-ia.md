# ADR-005 — Port/Adapter para o serviço de IA (anticorrupção)

## Status
Aceito

## Contexto
A previsão de demanda é fornecida por um serviço Python/Flask externo
(pasta `ai/`), que **não pode ser alterado** e pode ser substituído no futuro.
O backend não deve acoplar sua regra de negócio ao protocolo HTTP nem ao formato
desse serviço.

## Decisão
Definir uma **porta** `DemandForecastPort` (interface) no pacote `client` e um
**adaptador** `FlaskDemandForecastClient` que implementa a porta usando
`RestTemplate`. O `AIService` depende apenas da porta (Inversão de Dependência);
a URL do serviço é externalizada por `@Value` com default local.

## Consequências
- (+) Troca do provedor de IA = nova implementação da porta, sem tocar o domínio.
- (+) Testes do `AIService` usam um mock da porta, sem rede.
- (+) Camada anticorrupção isola o backend do contrato do Flask.
- (−) Uma indireção a mais; justificada pela fronteira externa.
