import numpy as np
import requests
from fastapi import FastAPI
from pydantic import BaseModel
from PIL import Image
from io import BytesIO
import tensorflow as tf

app = FastAPI()

# Carga MobileNetV2 preentrenado con ImageNet
model = tf.keras.applications.MobileNetV2(weights="imagenet")
decode_predictions = tf.keras.applications.mobilenet_v2.decode_predictions
preprocess_input = tf.keras.applications.mobilenet_v2.preprocess_input

# Palabras clave de ImageNet que mapean a cada categoría
CATEGORIA_KEYWORDS = {
    "PLASTICO": ["bottle", "plastic", "water_bottle", "pop_bottle", "pill_bottle",
                 "lotion", "shampoo", "container", "jug", "bucket"],
    "VIDRIO":   ["wine_bottle", "beer_bottle", "glass", "jar", "goblet",
                 "perfume", "vase", "whiskey_jug"],
    "METAL":    ["can", "tin", "aluminum", "steel", "iron", "nail", "screw",
                 "wrench", "padlock", "chain", "mailbox"],
    "PAPEL":    ["newspaper", "paper", "book", "envelope", "cardboard",
                 "comic_book", "menu", "packet", "carton"],
}

def detectar_categoria(predictions):
    """Busca en las top-5 predicciones si alguna coincide con nuestras categorías."""
    top5 = decode_predictions(predictions, top=5)[0]
    for _, label, confidence in top5:
        label_lower = label.lower()
        for categoria, keywords in CATEGORIA_KEYWORDS.items():
            for keyword in keywords:
                if keyword in label_lower:
                    return categoria, float(confidence)
    # Si no coincide con nada conocido
    best_label = top5[0][1]
    best_conf = float(top5[0][2])
    return None, best_conf

class ClassifyRequest(BaseModel):
    imageUrl: str

class ClassifyResponse(BaseModel):
    label: str
    tipoMaterial: str | None
    confidence: float
    recyclable: bool

@app.post("/classify", response_model=ClassifyResponse)
def classify(request: ClassifyRequest):
    response = requests.get(request.imageUrl, timeout=10,
                           headers={"User-Agent": "Mozilla/5.0"})
    image = Image.open(BytesIO(response.content)).convert("RGB")
    image = image.resize((224, 224))

    img_array = np.array(image, dtype=np.float32)
    img_array = np.expand_dims(img_array, axis=0)
    img_array = preprocess_input(img_array)

    predictions = model.predict(img_array)
    top5 = decode_predictions(predictions, top=5)[0]

    # Log para ver qué detecta
    print("=== TOP 5 PREDICCIONES ===")
    for _, label, conf in top5:
        print(f"  {label}: {conf:.2%}")

    categoria, confidence = detectar_categoria(predictions)

    return ClassifyResponse(
        label=top5[0][1],
        tipoMaterial=categoria,
        confidence=confidence,
        recyclable=categoria is not None
    )