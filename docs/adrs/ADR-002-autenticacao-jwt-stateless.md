# ADR-002 — Autenticação stateless via JWT

## Status
Aceito

## Contexto
O frontend é uma aplicação web separada que consome a API por HTTP. Não há estado
de sessão a manter no servidor e queremos que a API seja horizontalmente escalável
sem replicação de sessão.

## Decisão
Usar autenticação **stateless com JWT (Bearer)**. O `TokenService` (biblioteca
`java-jwt`) emite tokens assinados em `HMAC256` no login; o `SecurityFilter`
(`OncePerRequestFilter`) valida o token a cada requisição e popula o
`SecurityContext`. O Spring Security é configurado com
`SessionCreationPolicy.STATELESS`, CSRF desabilitado e senhas em `BCrypt`.
O segredo e a expiração vêm de variáveis de ambiente
(`application.security.jwt.secret` / `.expiration`); **não há segredo padrão** —
um segredo ausente faz a aplicação falhar na inicialização (fail-fast).

## Consequências
- (+) Sem estado de sessão; qualquer instância valida qualquer token.
- (+) Segredo externalizado e expiração configurável.
- (−) Revogação de token antes da expiração exige mecanismo adicional
  (blocklist), não implementado por ora.
- Decisão reforçada nesta refatoração: removido o fallback inseguro de segredo
  (`my-secret`) e alinhadas as chaves de configuração com `application.properties`.
