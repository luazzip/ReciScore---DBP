package utec.reciscore.ia;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class IaClient {

    private final RestTemplate restTemplate;
    private static final String IA_URL = "http://localhost:8000/classify";

    public IaResponse classify(String imageUrl) {
        IaRequest request = new IaRequest(imageUrl);
        return restTemplate.postForObject(IA_URL, request, IaResponse.class);
    }
}