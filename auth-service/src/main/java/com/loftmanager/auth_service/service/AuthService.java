package com.loftmanager.auth_service.service;

import com.loftmanager.auth_service.dto.AuthResponse;
import com.loftmanager.auth_service.dto.LoginRequest;
import com.loftmanager.auth_service.model.Rol;
import com.loftmanager.auth_service.model.Usuario;
import com.loftmanager.auth_service.repository.RolRepository;
import com.loftmanager.auth_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthResponse register(Usuario request) {
        // 1. Buscamos el rol en la base de datos para asegurar su existencia
        Rol rol = rolRepository.findById(request.getRol().getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado en la base de datos"));
        
        //ADUANA EXCLUSIVA PARA CLIENTES (Rol ID 3)
        //La validación de contratos y lofts solo se activa si se está registrando un Cliente
        if (request.getRol().getIdRol() == 3) {
            if (request.getIdLoft() == null) {
                throw new IllegalArgumentException("Operación rechazada: Un usuario de tipo CLIENTE debe ingresar un número de Loft obligatoriamente.");
            }

            if (usuarioRepository.existsByIdLoft(request.getIdLoft())) {
                throw new IllegalArgumentException("Operación rechazada: El Loft " + request.getIdLoft() + " ya tiene una cuenta de usuario CLIENTE activa vinculada en el sistema.");
            }

            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            String urlValidacion = "http://localhost:8080/api/leases/validar-loft/" + request.getIdLoft();

            try {
                Boolean tieneContratoLegal = restTemplate.getForObject(urlValidacion, Boolean.class);
                if (tieneContratoLegal == null || !tieneContratoLegal) {
                    throw new IllegalArgumentException("Operación rechazada: El Loft " + request.getIdLoft() + " no registra ningún contrato de arriendo activo en el sistema.");
                }
            } catch (IllegalArgumentException e) {
                throw e; 
            } catch (Exception e) {
                throw new RuntimeException("Error de consistencia distribuida: El lease-service no responde. Registro congelado por seguridad.");
            }
        }

        //Si es OPERADOR (Rol 2) o un nuevo ADMIN (Rol 1), se salta la validación de lofts y contratos, guardándose de inmediato
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombreReal(request.getNombreReal());
        usuario.setCargoUsuario(request.getCargoUsuario());
        usuario.setEmail(request.getEmail());
        usuario.setRol(rol);
        usuario.setIdLoft(request.getIdLoft());
        
        usuarioRepository.save(usuario);
        
        String token = jwtService.generateToken(usuario);
        return new AuthResponse(token, usuario.getIdUsuario()); 
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        String token = jwtService.generateToken(usuario);
        return AuthResponse.builder().token(token).build();
    }

    
    public List<Usuario> getAllUsers() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getUserById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario updateUser(Long id, Usuario userDetails) {
        return usuarioRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setNombreReal(userDetails.getNombreReal());
            user.setCargoUsuario(userDetails.getCargoUsuario());
            user.setEmail(userDetails.getEmail());

            if (userDetails.getRol() != null && userDetails.getRol().getIdRol() != null) {
                Rol rol = rolRepository.findById(userDetails.getRol().getIdRol())
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
                user.setRol(rol);
            }
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            return usuarioRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    public void deleteUser(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}