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
import org.springframework.http.HttpStatus;
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
                    .metadata(Map.of("pedidoId", "1221"))
                    .notificationUrl("https://mercadopagoprueba-production.up.railway.app/api/mp/webhook")
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            return preference.getId();
        } catch (MPException | MPApiException e) {
            throw new RuntimeException(e);
        }
    }



    @PostMapping("/api/mp/webhook")
    public ResponseEntity<String> handleWebhookNotification(@RequestBody Map<String, Object> webhookData) {
        System.out.print(webhookData.toString());
        String type = (String) webhookData.get("type");
        String action = (String) webhookData.get("action");
        Map<String, String> data = (Map<String, String>) webhookData.get("data");
        String paymentId = data.get("id");

        if ("payment".equals(type) && "payment.created".equals(action) && paymentId != null) {
            boolean paymentConfirmed = mercadoPagoService.verifyPayment(paymentId);

            if (paymentConfirmed) {
                System.out.print("pago recibido");
                return ResponseEntity.status(HttpStatus.CREATED).body("Pago recibido");
            } else {
                System.out.print("pago no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pago no ecnontrado");
            }
        }

        return ResponseEntity.badRequest().body("Invalid webhook data");
    }
}

