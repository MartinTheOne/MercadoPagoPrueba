package com.example.pruebaMercadoPago.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
 private String id;
 private String status;
 private String statusDetail;
 private double transactionAmount;


}
