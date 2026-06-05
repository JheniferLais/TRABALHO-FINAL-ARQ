# ADR-008 — Especificação OpenAPI como contrato de API

## Status
Aceito (refatoração 2026)

## Contexto
A disciplina exige um design de API formalmente especificado. O projeto roda sobre
Spring Boot 4; a compatibilidade exata da versão do `springdoc-openapi` (que gera
o Swagger em tempo de execução) com essa linha do Boot não pôde ser validada no
ambiente de trabalho atual, e uma dependência incompatível quebraria o build.

## Decisão
Manter um arquivo **`docs/openapi.yaml`** (OpenAPI 3.0.3) versionado como o
**contrato formal** da API — cobrindo rotas, parâmetros, schemas de request/response,
esquema de segurança JWT (`bearerAuth`) e a convenção de erro `ErrorDetails`.
A geração automática via `springdoc` fica registrada como melhoria opcional, a ser
habilitada quando a equipe confirmar a versão compatível com o Spring Boot em uso.

## Consequências
- (+) Contrato formal completo, navegável em qualquer editor Swagger, sem risco
  ao build.
- (+) Serve de fonte para clientes e para validação de contrato.
- (−) O `openapi.yaml` é mantido manualmente; precisa ser atualizado junto com
  mudanças de endpoint (mitigável adotando `springdoc` no futuro).
