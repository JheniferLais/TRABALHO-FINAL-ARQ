# ADR-006 — Exclusão lógica (soft delete)

## Status
Aceito

## Contexto
Produtos, usuários e empresas podem ser "removidos", mas registros históricos
(vendas, lotes) referenciam essas entidades. A exclusão física quebraria
integridade referencial e perderia histórico necessário para os relatórios e para
a previsão de demanda.

## Decisão
Adotar **soft delete** com a flag booleana `deleted`. As consultas usam variações
`...AndDeletedFalse`; a "remoção" apenas marca `deleted = true`. Regras de negócio
protegem operações inválidas (ex.: `ProductHasStockException` impede excluir
produto com estoque ativo).

## Consequências
- (+) Histórico preservado e integridade referencial mantida.
- (+) Possibilidade de auditoria e de "reativação".
- (−) Todas as queries precisam filtrar `deleted = false` (risco de esquecimento);
  mitigado por nomes de método padronizados nos repositórios.
- (−) Dados crescem sem expurgo; aceitável no volume atual.
