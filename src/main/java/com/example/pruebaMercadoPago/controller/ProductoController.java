package com.example.pruebaMercadoPago.controller;

import com.example.pruebaMercadoPago.entity.Producto;
import com.example.pruebaMercadoPago.service.MercadoPagoService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @Value("${mercado-pago.access-token}")
    private String accessToken;

    // Endpoint para crear la preferencia de pago
    @PostMapping("/api/mp")
    public String createTransaction(@RequestBody Producto producto) {
        if (producto == null) {
            return "error";
        }

        String titulo = producto.getTitulo();
        int cantidad = producto.getCantidad();
        BigDecimal precio = producto.getPrecio();

        try {
            MercadoPagoConfig.setAccessToken(accessToken);

            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .title(titulo)
                    .unitPrice(precio)
                    .quantity(cantidad)
                    .currencyId("ARS")
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            PreferenceBackUrlsRequest backUrlsRequest = PreferenceBackUrlsRequest.builder()
                    .pending("https://www.youtube.com")
                    .failure("https://www.youtube.com")
                    .success("https://www.youtube.com")
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrlsRequest)
                    .metadata(Map.of("pedidoId", "11111111111"))
                    .notificationUrl("https://mercadopagoprueba-production.up.railway.app/api/mp/webhook")
                    .additionalInfo("1111111111")
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            return preference.getId();
        } catch (MPException | MPApiException e) {
            throw new RuntimeException(e);
        }
    }



    @PostMapping("/api/mp/webhook")
    public ResponseEntity<?> handleWebhookNotification(@RequestBody Map<String, Object> webhookData) {


        String type = (String) webhookData.get("type");
        String action = (String) webhookData.get("action");

        if ("merchant_order".equals(webhookData.get("topic"))) {
            Object resourceObj = webhookData.get("resource");
            if (resourceObj instanceof String) {
                String resourceUrl = (String) resourceObj;
                Map<String, Object> merchantOrderDetails = mercadoPagoService.getMerchantOrderDetails(resourceUrl);
                if (merchantOrderDetails.containsKey("additional_info")) {
                    String additionalInfo = (String) merchantOrderDetails.get("additional_info");
                    try {
                        long additionalInfoLong = Long.parseLong(additionalInfo);
                        System.out.println("Additional Info (long): " + additionalInfoLong);
                        return ResponseEntity.ok("creado");
                    } catch (NumberFormatException e) {
                        System.out.println("Error al convertir 'additional_info' a long: " + e.getMessage());
                    }

                } else {
                    System.out.println("El campo 'additional_info' no está presente en los detalles de la orden");
                }
            } else {
                System.out.println("El campo 'resource' no es una cadena");
            }
        }

        return ResponseEntity.badRequest().body("Invalid webhook data");
    }



}

