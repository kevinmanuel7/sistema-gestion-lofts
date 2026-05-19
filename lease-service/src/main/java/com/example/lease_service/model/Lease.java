package com.example.lease_service.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "Lease") 
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lease {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "El ID del arrendatario es obligatorio")
    @Column(name = "tenant_id")
    private Long tenantId;

    @NotNull(message = "El ID del loft es obligatorio")
    @Min(value = 1, message = "El número de loft no puede ser menor a 1")
    @Max(value = 9, message = "El negocio solo cuenta con lofts del 1 al 9")
    @Column(name = "loft_id")
    private Long loftId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de término es obligatoria")
    @Column(name = "fecha_termino")
    private LocalDate fechaTermino;

    @NotNull(message = "El precio mensual es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    @Column(name = "precio_mensual")
    private Double precioMensual;

    @NotNull(message = "El monto de garantía es obligatorio")
    @Column(name = "monto_garantia") 
    private Double montoGarantia;

    @NotBlank(message = "El estado del contrato es obligatorio")
    @Column(name = "estado_contrato")
    private String estadoContrato;

}
