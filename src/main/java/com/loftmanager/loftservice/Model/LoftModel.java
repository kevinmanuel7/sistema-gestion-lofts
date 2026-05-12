package com.loftmanager.loftservice.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_loft")
    private Long idLoft;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50)
    @Column(name = "nombre_loft", length = 50)
    private String nombreLoft;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 1)
    @Column(name = "estado", length = 1)
    private String estado;



}
