package com.example.pruebaMercadoPago.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

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


    public Map<String, Object> getMerchantOrderDetails(String resourceUrl) {
        String url = resourceUrl + "?access_token=" + accessToken;

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody();
        } catch (RestClientException e) {
            System.out.println("Error obteniendo detalles del merchant order: " + e.getMessage());
            return null;
        }
    }


}