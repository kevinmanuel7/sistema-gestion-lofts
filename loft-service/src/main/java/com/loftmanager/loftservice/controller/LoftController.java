package com.loftmanager.loftservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.loftmanager.loftservice.dto.InventoryDto;
import com.loftmanager.loftservice.model.LoftModel;
import com.loftmanager.loftservice.repository.LoftRepository;
import com.loftmanager.loftservice.service.LoftService;
import com.loftmanager.loftservice.cliente.InventoryClient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/loft")
@SuppressWarnings("null")
@Tag(name = "Loft Controller", description = "Endpoints para la gestión, administración e integración de las casillas físicas (Lofts)")
public class LoftController {

    @Autowired
    private LoftRepository loftRepository;

    @Autowired
    private InventoryClient InventoryClient;

    @Autowired
    private LoftService loftService;

    @Operation(summary = "Obtener un Loft por ID", description = "Busca en la base de datos la información detallada de un loft específico mediante su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loft encontrado de manera exitosa."),
        @ApiResponse(responseCode = "404", description = "El ID ingresado no corresponde a ningún Loft registrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerLoftPorId(
            @Parameter(description = "ID del loft a consultar", example = "1") @PathVariable("id") Long id) {
        java.util.Optional<LoftModel> loft = loftRepository.findById(id);
        
        if (loft.isPresent()) {
            return ResponseEntity.ok(loft.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loft no encontrado");
        }
    }
    
    @Operation(summary = "Listar muebles asociados a un Loft", description = "Establece comunicación sincrónica vía Feign Client con el microservicio `inventory-service` para traer los muebles.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventario de muebles obtenido correctamente."),
        @ApiResponse(responseCode = "404", description = "El Loft especificado no existe en el sistema.")
    })
    @GetMapping("/{idLoft}/muebles")
    public ResponseEntity<?> listarMueblesPorloft(
            @Parameter(description = "ID del loft para consultar su inventario", example = "2") @PathVariable("idLoft") Long idLoft) { 
        if (!loftRepository.existsById(idLoft)) {
            return new ResponseEntity<>("El loft con ID " + idLoft + " no existe.", HttpStatus.NOT_FOUND);
        }
        InventoryDto inventario = InventoryClient.getItemsByLoftId(idLoft); 
        return ResponseEntity.ok(inventario);
    }

    @Operation(summary = "Listar todos los Lofts", description = "Retorna una lista completa de todos los lofts registrados, sin paginación.")
    @ApiResponse(responseCode = "200", description = "Lista de lofts recuperada con éxito.")
    @GetMapping("/listar")
    public List<LoftModel> listar() {
        return loftRepository.findAll();
    }

    @Operation(summary = "Crear un nuevo Loft", description = "Valida que el ID ingresado no exista previamente y cumpla con la regla de negocio física (casillas del 1 al 9).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Loft creado y registrado correctamente."),
        @ApiResponse(responseCode = "400", description = "Error de validación: ID duplicado o fuera de los límites físicos (1-9).")
    })
    @PostMapping("/crear")
    public ResponseEntity<?> crear(@Valid @RequestBody LoftModel loft) {
        try {
            if (loftService.existePorId(loft.getIdLoft())) {
                return new ResponseEntity<>("Error: El Loft con ID " + loft.getIdLoft() + " ya existe.", HttpStatus.BAD_REQUEST);
            }
            
            // Llamamos al service, que ejecuta la validación del 1 al 9
            LoftModel nuevoLoft = loftService.guardar(loft);
            return new ResponseEntity<>(nuevoLoft, HttpStatus.CREATED);
            
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Actualizar un Loft existente", description = "Modifica los datos mutables (nombreLoft, isOcupado) de un loft determinado mediante su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loft actualizado con éxito."),
        @ApiResponse(responseCode = "404", description = "No se pudo actualizar debido a que el ID no existe.")
    })
    @PutMapping("/actualizar/{idLoft}")
    public ResponseEntity<?> actualizar(
            @Parameter(description = "ID del loft que se desea actualizar", example = "1") @PathVariable("idLoft") Long idLoft, 
            @Valid @RequestBody LoftModel nuevoLoft) {
        java.util.Optional<LoftModel> loftOptional = loftRepository.findById(idLoft);
        
        if (loftOptional.isPresent()) {
            LoftModel loftExistente = loftOptional.get();
            
            loftExistente.setNombreLoft(nuevoLoft.getNombreLoft());
            loftExistente.setIsOcupado(nuevoLoft.getIsOcupado());
            
            LoftModel loftActualizado = loftRepository.save(loftExistente);
            return ResponseEntity.ok(loftActualizado);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: El Loft con ID " + idLoft + " no existe.");
        }
    }

    @Operation(summary = "Eliminar un Loft", description = "Remueve permanentemente un loft del sistema a través de su identificador numérico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Loft eliminado exitosamente. Sin contenido de retorno."),
        @ApiResponse(responseCode = "404", description = "El Loft que intenta eliminar no existe.")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del loft a eliminar", example = "3") @PathVariable("id") Long id) {
        if (loftRepository.existsById(id)) {
            loftRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}