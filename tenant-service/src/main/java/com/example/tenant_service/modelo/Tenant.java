package com.example.tenant_service.modelo;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Setter;

@Entity
@Data
@Setter
@Table(name = "tenants")

public class Tenant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre", nullable = false)
    private String nombre;
 
    @NotBlank(message = "El apellido es obligatorio")
    @Column(name = "apellido", nullable = false)
    private String apellido;

    @NotBlank(message = "El RUT es obligatorio")
    @Pattern(regexp = "^[0-9]{7,8}-[0-9Kk]{1}$", message = "Formato de RUT inválido (ej: 12345678-9)")
    @Column(unique = true)
    private String rut; 

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Column(name = "email", unique = true, nullable = false)
    private String email;  
    
    @Column(name = "telefono")
    private String telefono;
  
    //@NotNull(message = "La fecha de registro no puede ser nula")
    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    //Puente entre tenant y auth para lectura de utility-service
    @Column(name = "id_usuario")
    private Long idUsuario;

    @PrePersist
    protected void onCreate() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDate.now();
        }
    }
}
