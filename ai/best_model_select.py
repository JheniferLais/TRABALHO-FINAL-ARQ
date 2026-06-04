from sklearn.metrics import mean_absolute_error
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
import utils


def descobrir_melhor_estrategia(df):
    """Avalia qual algoritmo performa melhor via split de teste e exibe o painel no console."""
    if len(df) > 100:
        df = df.sample(100, random_state=42)

    X = df[["month", "price", "stockQuantity"]]
    y = df["totalSold"]

    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)

    X_train, X_test, y_train, y_test = train_test_split(
        X_scaled, y, test_size=0.2, random_state=42
    )

    modelos = utils.obter_config_modelos(len(X_train))
    maes = {}

    for nome, model in modelos.items():
        model.fit(X_train, y_train)
        predicoes = model.predict(X_test)
        maes[nome] = mean_absolute_error(y_test, predicoes)

    melhor_modelo = min(maes, key=maes.get)

    # Painel visual bonito
    print("\n" + "=" * 60, flush=True)
    print("               RESULTADO DO TESTE DE IA (AUTO)             ", flush=True)
    print("=" * 60, flush=True)
    print(f"  Amostras avaliadas no teste: {len(df)} produtos", flush=True)
    print("  Erros Médios Calculados (MAE):", flush=True)
    for nome, valor in maes.items():
        marcador = " -> [VENCEDOR]" if nome == melhor_modelo else ""
        print(f"    - {nome.ljust(15)}: {valor:.2f} {marcador}", flush=True)
    print("-" * 60, flush=True)
    print(
        f"  ESTRATÉGIA SELECIONADA PARA PREVISÃO: {melhor_modelo.upper()}",
        flush=True,
    )
    print("=" * 60 + "\n", flush=True)

    return melhor_modelo, maes[melhor_modelo]