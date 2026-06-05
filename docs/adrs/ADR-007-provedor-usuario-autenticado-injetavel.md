# ADR-007 — Provedor de usuário autenticado injetável (DIP)

## Status
Aceito (refatoração 2026)

## Contexto
O acesso ao usuário autenticado era feito por um utilitário **estático**
(`SecurityUtils.getAuthenticatedUser()`) chamado diretamente em todos os services.
Isso acoplava cada service ao `SecurityContextHolder` do Spring Security e exigia
**mock estático** nos testes (`mockStatic`), além de violar a Inversão de
Dependência (dependência de um detalhe concreto/estático, não de uma abstração).

## Decisão
Substituir o util estático por um colaborador **injetável**
`AuthenticatedUserProvider` (`@Component`) com o método `getCurrentUser()`. Os
services passam a recebê-lo por injeção de construtor (via `@RequiredArgsConstructor`)
e dependem dessa abstração. O `SecurityUtils` foi removido.

## Consequências
- (+) Services desacoplados do acesso estático ao contexto de segurança (DIP).
- (+) Testes usam um mock simples do provedor (`@Mock`), sem `mockStatic`.
- (+) Ponto único para evoluir a resolução do usuário (ex.: cache, claims).
- (−) Uma dependência a mais no construtor de cada service; custo trivial.
- A lógica de negócio (checagem de `deleted`, posse por empresa) permaneceu nos
  services para preservar o comportamento e a suíte de testes existente.
