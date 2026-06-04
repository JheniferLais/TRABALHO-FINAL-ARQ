# Sched

O **Sched** é uma plataforma voltada a minimercados, mini padarias e pequenos comércios alimentares. O sistema atua diretamente no apoio à tomada de decisão, proteção do fluxo de caixa, otimização de estoque e redução drástica do desperdício de alimentos através de previsões de demanda baseadas em Inteligência Artificial (Machine Learning).

## 👥 Equipe

* **Eduarda Luiza Pinheiro Nepomuceno - 24002529**
* **Jean Yuki Kimura - 24008214** 
* **Jhenifer Laís Barbosa - 24014979** 
* **João Pedro Duarte Giatti - 24019083** 

- O projeto foi desenvolvido como parte do componente curricular **Projeto Integrador V - Engenharia de Software** da **PUC-Campinas**.
- E foi refatorado como parte do componente curricular **Padrões e Arquitetura de Software** da **PUC-Campinas**.

## Como Rodar?

### 1. Configure suas variáveis de ambiente

Exemplo:

```env
DB_URL=jdbc:postgresql://localhost:5432/####
DB_USER=####
DB_PASSWORD=####
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=864000
```

`JWT_SECRET` e `JWT_EXPIRATION` podem permanecer iguais.  
Altere apenas `DB_URL`, `DB_USER` e `DB_PASSWORD` conforme suas credenciais do PostgreSQL.

---

### 2. Rode a IA

```bash
TRABALHO-FINAL-ARQ/ai/app.py
```

---

### 3. Rode o backend

```bash
TRABALHO-FINAL-ARQ/api/src/main/java/com/sched/api/ApiApplication.java
```

---

### 4. Rode o frontend

```bash
TRABALHO-FINAL-ARQ/frontend/pages/signup.html
```

---

E sipimpa ✨ tudo funciona.
