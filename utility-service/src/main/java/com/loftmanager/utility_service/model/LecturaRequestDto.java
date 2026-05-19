package com.loftmanager.utility_service.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class LecturaRequestDto {
    private Double lecturaMedidor; // Ejemplo: 1340
    private LocalDateTime fechaLectura; // Ejemplo: "2026-05-05T10:00:00"
    private Long idLoft; // Ejemplo: 1
}