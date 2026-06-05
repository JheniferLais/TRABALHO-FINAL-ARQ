# ADR-003 — Isolamento multi-tenant por `company_id`

## Status
Aceito

## Contexto
Cada empresa cadastrada enxerga apenas seus próprios produtos, estoques, vendas e
usuários. Vazamento de dados entre empresas é um risco de segurança e de
confiabilidade inaceitável.

## Decisão
Adotar **multi-tenancy lógico por discriminador** (`company_id`). O usuário
autenticado carrega sua `Company`; todas as consultas de leitura filtram por
`product.company.id`/`company.id` (ex.:
`findByProduct_Company_IdAndProduct_DeletedFalse`), e as operações de escrita
validam, no service, se o recurso pertence à empresa do usuário, lançando
`AccessDeniedException` caso contrário.

## Consequências
- (+) Isolamento de dados garantido na camada de aplicação e de consulta.
- (+) Modelo simples, sem custo de bancos/schemas separados por tenant.
- (−) A correção do isolamento depende de disciplina nos services; mitigado por
  testes unitários que cobrem os cenários de acesso cruzado.
- (−) Não há isolamento físico; um bug de query poderia, em tese, vazar dados —
  por isso os filtros por empresa são padronizados nos repositórios.
