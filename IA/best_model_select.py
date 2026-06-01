import utils
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_absolute_error


def descobrir_melhor_estrategia(df):
    """Avalia qual algoritmo performa melhor via split de teste e retorna o nome dele."""
    if len(df) > 100:
        df = df.sample(100, random_state=42)

    X = df[["month", "price", "stockQuantity"]]
    y = df["totalSold"]

    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)

    X_train, X_test, y_train, y_test = train_test_split(
        X_scaled, y, test_size=0.2, random_state=42
    )

    # Pega as instâncias de teste usando as mesmas configs globais
    modelos = utils.obter_config_modelos(len(X_train))
    maes = {}

    for nome, model in modelos.items():
        model.fit(X_train, y_train)
        predicoes = model.predict(X_test)
        maes[nome] = mean_absolute_error(y_test, predicoes)

    print(f"[Seleção] MAEs calculados -> { {k: round(v, 2) for k, v in maes.items()} }")

    melhor_modelo = min(maes, key=maes.get)
    print(f"[Seleção] Vencedor do teste: {melhor_modelo}")

    return melhor_modelo, maes[melhor_modelo]