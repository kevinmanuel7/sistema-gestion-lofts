# Sistema de Gestión de Lofts (Loft Manager API)

Este proyecto corresponde a una arquitectura distribuida basada en microservicios para la administración integral de lofts, mobiliario, consumos de servicios y reserva de espacios.

## Integrantes y Roles
* **Kevin Maturana** - Configuración de API Gateway, Auth con JWT, despliegue bases de datos.
* **Bastian Veas** - Microservicios de negocio.
* **Fernanda Peña** - Microservicios de negocio.

---

## 🌐 Arquitectura del Sistema (6 Microservicios)
El sistema utiliza un patrón de diseño **CSR (Controller-Service-Repository)**, bases de datos independientes por servicio y comunicación síncrona mediante **Feign Clients / WebClient**.

1. **`api-gateway` (Puerto 8080)**: Enrutador centralizado que redirige las peticiones y valida los tokens JWT.
2. **`auth-service`**: Gestión de usuarios, roles y generación de tokens JWT.
3. **`loft-service`**: CRUD de propiedades (lofts), capacidades, ubicaciones y estado.
4. **`furniture-service`**: Inventario de mobiliario asociado a cada loft.
5. **`utility-service`**: Registro de consumos (agua, luz, internet) por loft (Protegido con JWT).
6. **`tenant-service`**: Registro de arrendatarios y datos de contacto.
---

## 🛠️ Tecnologías y Entorno
* **Lenguaje**: Java 17 / Spring Boot 3.x
* **Base de Datos**: MySQL alojadas en **AWS** (RDS).
* **Seguridad**: Spring Security + JWT.
* **Entorno de desarrollo local**: Laragon + VS Code.
* **Herramientas**: Postman para pruebas de integración, Git/GitHub y Trello para agilidad.

---

## 🚀 Instalación y Despliegue Local

### Pre requisitos
1. Tener configuradas las variables de entorno de las bases de datos de AWS.
2. Contar con Java 17 instalado.

### Pasos de ejecución (Orden sugerido):
1. Levantar la base de datos local o verificar conexión a AWS RDS.
2. Iniciar el Eureka Server
3. Iniciar el servicio de seguridad: `auth-service`.
4. Iniciar los servicios de negocio (`loft-service`, `furniture-service`, `utility-service`, etc.).
5. Iniciar el `api-gateway` para habilitar el enrutamiento.