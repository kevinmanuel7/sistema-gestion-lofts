package com.loftmanager.loftservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "LOFT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoftModel {

    @Id
    @Column(name = "id_loft")
    private Long idLoft;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50)
    @Column(name = "nombre_loft", length = 50)
    private String nombreLoft;

    @NotNull(message = "El estado es obligatorio")
    @Column(name = "is_ocupado")
    private Boolean isOcupado = false; //por defecto nace desocupado (false)

}
