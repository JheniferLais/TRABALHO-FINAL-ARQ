# Diagrama 4 — Sequência: previsão de demanda (IA)

Fluxo de `GET /ai/predictions`. O `AIService` agrega dados de demanda da empresa e
delega a previsão à **porta** `DemandForecastPort`, implementada pelo adaptador
que conversa com o serviço Flask.


![Sequência - Previsão de Demanda IA](./imgs/03-sequencia-previsao-ia.png)

<details>
<summary><b>Código-fonte Mermaid (Clique para expandir)</b></summary>

```mermaid
sequenceDiagram
    autonumber
    actor C as Cliente (Frontend)
    participant SC as AIController
    participant AS as AIService
    participant AUP as AuthenticatedUserProvider
    participant AR as AIRepository
    participant PORT as DemandForecastPort
    participant ADP as FlaskDemandForecastClient
    participant FL as Serviço Flask (ai/)

    C->>SC: GET /ai/predictions (Bearer JWT)
    SC->>AS: getPredictions()
    AS->>AUP: getCurrentUser()
    AUP-->>AS: User autenticado
    AS->>AR: getDemandDataByCompany(companyId)
    AR-->>AS: List<AIDemandDataRequest>
    AS->>PORT: forecast(demandData)
    PORT->>ADP: forecast(demandData)
    ADP->>FL: HTTP POST /predict (JSON)
    FL-->>ADP: previsões (JSON)
    ADP-->>AS: List<AIPredictionResponse>
    AS-->>SC: List<AIPredictionResponse>
    SC-->>C: 200 OK
```
