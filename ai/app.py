import pandas as pd
from best_model_select import descobrir_melhor_estrategia
from flask import Flask, jsonify, request
import utils

app = Flask(__name__)

# >>> VARIÁVEIS GLOBAIS DE CACHE <<<
# Elas vão guardar a decisão do primeiro torneio na memória do servidor
MELHOR_MODELO_CACHE = None
MAE_MODELO_CACHE = 0.0


@app.route("/predict", methods=["POST"])
def predict():
    # Permite alterar as variáveis globais dentro da função
    global MELHOR_MODELO_CACHE, MAE_MODELO_CACHE

    payload = request.get_json()

    if isinstance(payload, dict) and "data" in payload:
        df = pd.DataFrame(payload["data"])
    else:
        df = pd.DataFrame(payload)

    print(
        f"\n[SINAL] Requisição recebida! Itens para análise: {len(df)}",
        flush=True,
    )

    if len(df) < 3:
        return (
            jsonify(
                {
                    "status": "INSUFFICIENT_DATA",
                    "message": "São necessários pelo menos 3 registros para avaliar e treinar os modelos de IA.",
                }
            ),
            400,
        )

    # ============================================================
    # ETAPA 1: O TORNEIO (SÓ RODA SE FOR A PRIMEIRA VEZ)
    # ============================================================
    if MELHOR_MODELO_CACHE is None:
        print(
            "[SISTEMA] Cache vazio! Iniciando torneio de modelos pela primeira e única vez...",
            flush=True,
        )
        # Roda o teste nos 3, calcula o MAE e printa aquele painel lindo
        modelo_escolhido, mae_calculado = descobrir_melhor_estrategia(df)

        # Salva o resultado no cache global
        MELHOR_MODELO_CACHE = modelo_escolhido
        MAE_MODELO_CACHE = mae_calculado
        print(
            f"[SISTEMA] Decisão salva no Cache: O algoritmo escolhido para sempre será o {MELHOR_MODELO_CACHE}.\n",
            flush=True,
        )
    else:
        # Nas próximas vezes, ele entra direto aqui e reaproveita a decisão
        print(
            f"[SISTEMA] Reaproveitando algoritmo do Cache: {MELHOR_MODELO_CACHE} (Pulando torneio MAE)",
            flush=True,
        )

    # Usa o modelo que está guardado no Cache
    modelo_alvo = MELHOR_MODELO_CACHE
    mae = MAE_MODELO_CACHE

    # ============================================================
    # ETAPA 2: A PRODUÇÃO (Sempre roda com 100% dos dados enviados)
    # ============================================================
    X = df[["month", "price", "stockQuantity"]]
    y = df["totalSold"]

    model, scaler = utils.preparar_e_treinar(X, y, modelo_alvo)
    alerts = utils.processar_previsoes(df, model, scaler)

    for entry in alerts:
        entry.update({"modelUsed": modelo_alvo, "modelMAE": round(mae, 2)})

    alertas_criticos = [item for item in alerts if item["alert"] != "OK"]

    print(
        f"[FLUXO] Processamento concluído. Modelo: {modelo_alvo}. Retornando {len(alertas_criticos)} alertas.\n",
        flush=True,
    )
    return jsonify(alertas_criticos)


if __name__ == "__main__":
    app.run(port=5000, debug=False)