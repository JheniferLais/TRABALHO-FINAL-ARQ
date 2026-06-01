from flask import Flask, request, jsonify
import pandas as pd
import utils
from best_model_select import descobrir_melhor_estrategia

app = Flask(__name__)

# Dicionário de mapeamento simples para validar o tamanho mínimo de amostras por estratégia
LIMITES_MINIMOS = {"rf": 3, "knn": 3, "svm": 5, "auto": 2}
MAPA_NOMES = {"rf": "RandomForest", "knn": "KNN", "svm": "SVM"}

@app.route("/predict", methods=["POST"])
def predict():
    payload = request.get_json()

    if isinstance(payload, dict) and "data" in payload:
        df = pd.DataFrame(payload["data"])
        strategy = payload.get("strategy", "rf").lower()
    else:
        df = pd.DataFrame(payload)
        strategy = "rf"

    if strategy not in LIMITES_MINIMOS:
        return jsonify({"status": "INVALID_STRATEGY", "message": "Estratégia não reconhecida"}), 400

    # Valida limite da estratégia escolhida
    limite = LIMITES_MINIMOS[strategy]
    if len(df) < limite:
        return jsonify({
            "status": "INSUFFICIENT_DATA",
            "message": f"A estratégia '{strategy}' requer ao menos {limite} registros."
        }), 400

    X = df[["month", "price", "stockQuantity"]]
    y = df["totalSold"]

    # Define qual algoritmo rodar
    if strategy == "auto":
        modelo_alvo, mae = descobrir_melhor_estrategia(df)
    else:
        modelo_alvo = MAPA_NOMES[strategy]
        mae = 0.0

    # Treina o modelo definitivo (com 100% dos dados recebidos) e aplica as previsões
    model, scaler = utils.preparar_e_treinar(X, y, modelo_alvo)
    alerts = utils.processar_previsoes(df, model, scaler)

    # Adiciona metadados na resposta de forma limpa
    for entry in alerts:
        entry.update({"modelUsed": modelo_alvo, "modelMAE": round(mae, 2)})

    return jsonify(alerts)

if __name__ == "__main__":
    app.run(port=5000, debug=True)