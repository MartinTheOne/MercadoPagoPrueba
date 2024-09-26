package com.example.pruebaMercadoPago.service;

import com.example.pruebaMercadoPago.entity.PaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MercadoPagoService {

    @Value("${mercado-pago.api-url}")
    private String mercadoPagoApiUrl;

    @Value("${mercado-pago.access-token}")
    private String accessToken;

    private final RestTemplate restTemplate;

    public MercadoPagoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean verifyPayment(String paymentId) {
    String url = UriComponentsBuilder.fromHttpUrl(mercadoPagoApiUrl + "/v1/payments/" + paymentId)
            .queryParam("access_token", accessToken)
            .toUriString();

    try {
        PaymentResponse response = restTemplate.getForObject(url, PaymentResponse.class);
        return response != null && "approved".equals(response.getStatus());
    } catch (RestClientException e) {
        return false;
    }
}

}