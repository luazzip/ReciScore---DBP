package utec.reciscore.ia;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class IaClient {

    private final RestTemplate restTemplate;

    @Value("${ia.service.url:http://localhost:8000/classify}")
    private String iaUrl;

    public IaResponse classify(String imageUrl) {
        IaRequest request = new IaRequest(imageUrl);
        return restTemplate.postForObject(iaUrl, request, IaResponse.class);
    }
}