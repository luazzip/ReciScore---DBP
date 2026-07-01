from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

KEYWORDS = {
    "PLASTICO": ["pet", "plastico", "plastic", "botella", "bottle"],
    "VIDRIO":   ["vidrio", "glass", "botella-vidrio"],
    "METAL":    ["metal", "lata", "aluminio", "can"],
    "PAPEL":    ["papel", "carton", "paper", "periodico"],
}

class ClassifyRequest(BaseModel):
    imageUrl: str

class ClassifyResponse(BaseModel):
    label: str
    tipoMaterial: str | None
    confidence: float
    recyclable: bool

@app.post("/classify", response_model=ClassifyResponse)
def classify(request: ClassifyRequest):
    url = request.imageUrl.lower()
    for categoria, keywords in KEYWORDS.items():
        for kw in keywords:
            if kw in url:
                return ClassifyResponse(label=kw, tipoMaterial=categoria, confidence=0.95, recyclable=True)
    return ClassifyResponse(label="plastico", tipoMaterial="PLASTICO", confidence=0.95, recyclable=True)