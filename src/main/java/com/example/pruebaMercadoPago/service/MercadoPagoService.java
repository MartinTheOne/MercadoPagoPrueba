package com.example.pruebaMercadoPago.service;

import com.example.pruebaMercadoPago.entity.PaymentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

    public boolean verifyPayment(String paymentId) {
        String url = UriComponentsBuilder.fromHttpUrl(mercadoPagoApiUrl + "/v1/payments/" + paymentId)
                .queryParam("access_token", accessToken)
                .toUriString();

        try {
            PaymentResponse response = restTemplate.getForObject(url, PaymentResponse.class);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
            System.out.println("Respuesta para ver si trae el PedidoID: " + jsonResponse); // Imprime el objeto como JSON
            return "approved".equals(response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Object> getMerchantOrderDetails(String resourceUrl) {
        // Usar la URL completa directamente
        String url = resourceUrl + "?access_token=" + accessToken;

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody(); // Devuelve el cuerpo de la respuesta
        } catch (RestClientException e) {
            System.out.println("Error obteniendo detalles del merchant order: " + e.getMessage());
            return null;
        }
    }


}