package com.example.tenant_service.modelo;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Data
@Table(name = "tenants")

public class Tenant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "el nombre es obligatorio")
    private String nombre;
 
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El RUT es obligatorio")
    @Pattern(regexp = "^[0-9]{7,8}-[0-9Kk]{1}$", message = "Formato de RUT inválido (ej: 12345678-9)")
    @Column(unique = true)
    private String rut; 

    @Email(message = "Debe ingresar un formato de email válido")
    private String email;  

    private String telefono;

    private LocalDate fechaRegistro;
}
