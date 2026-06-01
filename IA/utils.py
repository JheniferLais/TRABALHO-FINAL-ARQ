from sklearn.ensemble import RandomForestRegressor
from sklearn.neighbors import KNeighborsRegressor
from sklearn.preprocessing import StandardScaler
from sklearn.svm import SVR

def obter_config_modelos(len_data):
    """Centraliza a definição e os hiperparâmetros dos modelos."""
    k = max(1, min(3, len_data - 1))
    return {
        "RandomForest": RandomForestRegressor(n_estimators=50, random_state=42),
        "KNN": KNeighborsRegressor(
            n_neighbors=k, weights="distance", metric="euclidean"
        ),
        "SVM": SVR(kernel="rbf", C=100, epsilon=0.1, gamma="scale"),
    }


def preparar_e_treinar(X, y, modelo_nome):
    """Cria o scaler, normaliza os dados, printa o progresso e treina o modelo."""
    # Print do início do treinamento definitivo
    print(
        f"[IA - TREINO] Iniciando treinamento definitivo com algoritmo: {modelo_nome.upper()}...",
        flush=True,
    )

    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)

    modelos = obter_config_modelos(len(X))
    model = modelos[modelo_nome]
    model.fit(X_scaled, y)

    # Print de sucesso do treinamento
    print(
        f"[IA - TREINO] Modelo treinado com sucesso utilizando {len(X)} registros!",
        flush=True,
    )

    return model, scaler


def processar_previsoes(df, model, scaler):
    """Aplica o scaler na matriz inteira e gera a estrutura de alertas."""
    X = df[["month", "price", "stockQuantity"]]
    X_scaled = scaler.transform(X)

    predictions = model.predict(X_scaled)
    alerts = []

    for idx, row in df.iterrows():
        predicted_sales = max(predictions[idx], 0)

        prediction7 = round(predicted_sales * 0.25)
        prediction15 = round(predicted_sales * 0.50)
        prediction30 = round(predicted_sales)
        stock = row["stockQuantity"]

        alert = "OK"
        if stock < prediction7:
            alert = "URGENTE"
        elif stock < prediction15:
            alert = "ATENCAO"

        alerts.append(
            {
                "productName": row["productName"],
                "stockQuantity": int(stock),
                "prediction7Days": int(prediction7),
                "prediction15Days": int(prediction15),
                "prediction30Days": int(prediction30),
                "recommendedRestock": int(max(prediction30 - stock, 0)),
                "alert": alert,
            }
        )
    return alerts