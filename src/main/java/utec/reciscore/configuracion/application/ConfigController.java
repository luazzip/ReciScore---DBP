package utec.reciscore.configuracion.application;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @GetMapping("/pesos-por-tamano")
    public ResponseEntity<Map<String, Double>> getPesosPorTamano() {
        Map<String, Double> pesos = Map.of(
            "PEQUENO", 0.5,
            "MEDIANO", 1.0,
            "GRANDE", 2.5
        );
        return ResponseEntity.ok(pesos);
    }
}
