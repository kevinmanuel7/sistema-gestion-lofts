package com.loftmanager.auth_service.service;

import com.loftmanager.auth_service.dto.AuthResponse;
import com.loftmanager.auth_service.dto.LoginRequest;
import com.loftmanager.auth_service.model.Rol;
import com.loftmanager.auth_service.model.Usuario;
import com.loftmanager.auth_service.repository.RolRepository;
import com.loftmanager.auth_service.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.ArgumentMatchers.contains;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    // =========================================================================
    // PRUEBAS PARA EL MÉTODO REGISTER
    // =========================================================================

@Test
    void debeRegistrarAdminUOperador_SinValidarLoft_CuandoRolNoEsCliente() {
        // GIVEN
        Rol rolAdmin = new Rol();
        rolAdmin.setIdRol(1);

        Usuario request = new Usuario();
        request.setUsername("admin_kevin");
        request.setPassword("pass123");
        request.setNombreReal("Kevin Manuel");
        request.setCargoUsuario("Administrador");
        request.setEmail("kevin@duocuc.cl");
        request.setRol(rolAdmin);

        when(rolRepository.findById(1)).thenReturn(Optional.of(rolAdmin));
        when(passwordEncoder.encode("pass123")).thenReturn("encoded_pass");
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("mocked_jwt_token");

        // WHEN
        AuthResponse response = authService.register(request);

        // THEN
        assertNotNull(response);
        assertEquals("mocked_jwt_token", response.getToken());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        // Verificamos que jamás entró a las validaciones de cliente
        verify(usuarioRepository, never()).existsByIdLoft(anyLong());
    }

@Test
    void debeRegistrarCliente_CuandoTieneContratoActivoYLoftDisponible() {
        // GIVEN
        Rol rolCliente = new Rol();
        rolCliente.setIdRol(3);

        Usuario request = new Usuario();
        request.setUsername("inquilino1");
        request.setPassword("pass123");
        request.setNombreReal("Juan Perez");
        request.setCargoUsuario("Cliente");
        request.setEmail("juan@duocuc.cl");
        request.setRol(rolCliente);
        request.setIdLoft(101L);

        when(rolRepository.findById(3)).thenReturn(Optional.of(rolCliente));
        when(usuarioRepository.existsByIdLoft(101L)).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded_pass");
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("mocked_jwt_token");

        // Usamos MockedConstruction para atrapar el "new RestTemplate()" interno
        try (MockedConstruction<RestTemplate> mockedConstruction = mockConstruction(RestTemplate.class,
                (mock, context) -> {
                    // Simulamos que el lease-service responde TRUE (tiene contrato legal)
                    when(mock.getForObject(contains("/api/leases/validar-loft/101"), eq(Boolean.class)))
                            .thenReturn(true);
                })) {

            // WHEN
            AuthResponse response = authService.register(request);

            // THEN
            assertNotNull(response);
            assertEquals("mocked_jwt_token", response.getToken());
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
        }
    }

    // =========================================================================
    // PRUEBAS PARA EL MÉTODO LOGIN
    // =========================================================================

    @Test
    void debeAutenticarYRetornarToken_CuandoLoginEsExitoso() {
        // GIVEN
        LoginRequest request = new LoginRequest("admin_kevin", "password123");
        Usuario usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(5L);
        usuarioMock.setUsername("admin_kevin");

        when(usuarioRepository.findByUsername("admin_kevin")).thenReturn(Optional.of(usuarioMock));
        when(jwtService.generateToken(usuarioMock)).thenReturn("jwt_token_login");

        // WHEN
        AuthResponse response = authService.login(request);

        // THEN
        assertNotNull(response);
        assertEquals("jwt_token_login", response.getToken());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void debeLanzarExcepcionEnLogin_CuandoUsuarioNoExiste() {
        // GIVEN
        LoginRequest request = new LoginRequest("usuario_fantasma", "pass");
        when(usuarioRepository.findByUsername("usuario_fantasma")).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(UsernameNotFoundException.class, () -> {
            authService.login(request);
        });
    }

    // =========================================================================
    // PRUEBAS DEL CRUD (DELETE)
    // =========================================================================

    @Test
    void debeEliminarUsuario_CuandoIdExiste() {
        // GIVEN
        Long idExistente = 1L;
        when(usuarioRepository.existsById(idExistente)).thenReturn(true);

        // WHEN
        authService.deleteUser(idExistente);

        // THEN
        verify(usuarioRepository, times(1)).deleteById(idExistente);
    }
}