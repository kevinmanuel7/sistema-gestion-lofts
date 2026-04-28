package com.loftmanager.utility_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecturas_energia")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Lectura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLectura;

    @Column(nullable = false)
    private Double valorKwh;

    @Column(nullable = false)
    private LocalDateTime fechaLectura;

    @Column(nullable = false)
    private Long idLoftFk; // Referencia lógica al Loft (que estará en otro microservicio)
}