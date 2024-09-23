package com.example.pruebaMercadoPago.entity;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Producto {

    private String titulo;
    private int cantidad;
    private BigDecimal precio;
}
