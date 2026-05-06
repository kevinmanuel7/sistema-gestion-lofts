package com.equipamiento.equipamiento.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table ( name= "Loft")
@AllArgsConstructor
@NoArgsConstructor
public class Loft {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)

    private Long Id;

    @Column(name = "Nombre_Loft", nullable = false)
    private String Nombre_Loft;

    @Column( name = "Descripcion", nullable = false)
    private String Descripcion;

    @Column (name = "Estado",nullable = false)
    private Boolean Estado;
    
}
