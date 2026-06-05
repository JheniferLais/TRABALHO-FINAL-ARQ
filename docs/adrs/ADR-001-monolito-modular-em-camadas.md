# ADR-001 — Monolito modular organizado em camadas

## Status
Aceito

## Contexto
O Sched é mantido por uma equipe pequena (4 pessoas) e tem um domínio coeso
(produtos, estoque, vendas, alertas e previsão de demanda) de uma mesma empresa.
Não há requisito de escala independente por subsistema nem times separados que
justifiquem fronteiras de implantação distintas. Precisamos de uma arquitetura
que favoreça manutenibilidade e velocidade de evolução sem custo operacional alto.

## Decisão
Adotar um **monolito modular** com **arquitetura em camadas**:
`controller → service → repository → domain`, complementada por `dto`,
`exception`, `security` e `client`. Cada camada só conhece a camada imediatamente
inferior; entidades JPA não cruzam a fronteira do controller (são convertidas em
DTOs do tipo `record`). A borda de integração externa (IA) usa porta/adaptador.

## Consequências
- (+) Baixa complexidade operacional: um único artefato implantável.
- (+) Limites de responsabilidade claros e testáveis por camada.
- (+) Refatoração interna barata (sem contratos de rede entre módulos).
- (−) Escala apenas vertical/horizontal do monolito inteiro; subsistemas não
  escalam isoladamente. Aceitável para o volume atual.
- Microsserviços foram explicitamente descartados por overengineering frente ao
  tamanho do time e do domínio.
