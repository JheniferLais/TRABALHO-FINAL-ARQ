# Sched

O **Sched** é uma plataforma voltada a minimercados, mini padarias e pequenos comércios alimentares. O sistema atua diretamente no apoio à tomada de decisão, proteção do fluxo de caixa, otimização de estoque e redução drástica do desperdício de alimentos através de previsões de demanda baseadas em Inteligência Artificial (Machine Learning).

## 👥 Equipe

* **Eduarda Luiza Pinheiro Nepomuceno - 24002529**
* **Jean Yuki Kimura - 24008214** 
* **Jhenifer Laís Barbosa - 24014979** 
* **João Pedro Duarte Giatti - 24019083** 

- O projeto foi desenvolvido como parte do componente curricular **Projeto Integrador V - Engenharia de Software** da **PUC-Campinas**.
- E foi refatorado como parte do componente curricular **Padrões e Arquitetura de Software** da **PUC-Campinas**.

## 📚 Documentação de Arquitetura

- **[docs/adrs](docs/adrs/)** — decisões arquiteturais (ADRs).
- **[docs/diagrams](docs/diagrams/)** — diagramas de arquitetura, classes e sequência (Mermaid).
- **[docs/openapi.yaml](docs/openapi.yaml)** — especificação OpenAPI 3.0 da API.
- **[docs/DOCUMENTACAO-FINAL.md](docs/DOCUMENTACAO-FINAL.md)** — documento consolidado (arquitetura, atributos de qualidade, SOLID, Clean Code, GoF, API).

## Como Rodar?

### Pré-requisitos
- Java 17+ e PostgreSQL (o backend usa o Maven Wrapper incluso, `./mvnw`).
- Python 3 com as dependências do serviço de IA (pasta `ai/`).

### 1. Configure as variáveis de ambiente do backend

```env
DB_URL=jdbc:postgresql://localhost:5432/seu_banco
DB_USER=seu_user
DB_PASSWORD=sua_senha
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=864000
```

`JWT_SECRET` e `JWT_EXPIRATION` (segundos) podem permanecer iguais.
Ajuste `DB_URL`, `DB_USER` e `DB_PASSWORD` conforme seu PostgreSQL.
As variáveis precisam estar exportadas no ambiente que inicia o backend.

### 2. Rode o serviço de IA (Flask)

```bash
cd ai
python app.py        # sobe em http://127.0.0.1:5000
```

### 3. Rode o backend (Spring Boot)

```bash
cd api
./mvnw spring-boot:run        # sobe em http://localhost:8080
```

Para rodar os testes automatizados:

```bash
cd api
./mvnw test
```

### 4. Abra o frontend

Abra `frontend/pages/signup.html` no navegador (ou sirva a pasta `frontend/` com
qualquer servidor estático). O fluxo começa pelo cadastro da empresa.
