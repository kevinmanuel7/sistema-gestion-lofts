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
@SuppressWarnings("null") // Elimina las 6 advertencias de seguridad de nulos de VS Code
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // --- FLUJO DE AUTENTICACIÓN ---

    public AuthResponse register(Usuario request) {
        // Buscamos el Rol existente en la BD para no crear duplicados
        Rol rol = rolRepository.findById(request.getRol().getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado en la base de datos"));
        
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombreReal(request.getNombreReal());
        usuario.setCargoUsuario(request.getCargoUsuario());
        usuario.setEmail(request.getEmail());
        usuario.setRol(rol);
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        String token = jwtService.generateToken(usuarioGuardado);
        return AuthResponse.builder().token(token).build();
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

    // --- CRUD DE USUARIOS ---
    
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

            // Si se envió un nuevo rol, actualizamos la relación JPA de manera segura
            if (userDetails.getRol() != null && userDetails.getRol().getIdRol() != null) {
                Rol rol = rolRepository.findById(userDetails.getRol().getIdRol())
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
                user.setRol(rol);
            }

            // Solo encriptamos y modificamos la contraseña si viene en el cuerpo
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