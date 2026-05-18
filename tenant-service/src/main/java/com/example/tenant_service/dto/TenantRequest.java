package com.example.tenant_service.dto;

import com.example.tenant_service.modelo.Tenant;
import lombok.Data;

@Data
public class TenantRequest {
    private Tenant tenant;    // Aquí vienen nombre, rut, email, etc.
    private String password;  // La clave para crear su cuenta en el Auth
}