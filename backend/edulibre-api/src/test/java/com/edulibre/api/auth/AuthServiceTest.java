package com.edulibre.api.auth;

import com.edulibre.api.auth.dto.AuthResponse;
import com.edulibre.api.auth.dto.LoginRequest;
import com.edulibre.api.auth.dto.RegisterRequest;
import com.edulibre.api.auth.service.AuthService;
import com.edulibre.api.security.jwt.JwtService;
import com.edulibre.api.shared.exception.ConflictException;
import com.edulibre.api.shared.util.Rol;
import com.edulibre.api.usuario.entity.Usuario;
import com.edulibre.api.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerCreaEstudianteYDevuelveToken() {
        when(usuarioRepository.existsByEmail("nuevo@edulibre.com")).thenReturn(false);
        when(passwordEncoder.encode("secreto123")).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId("u-1");
            usuario.setCreadoEn(LocalDateTime.now());
            return usuario;
        });
        when(jwtService.generateToken(any(), eq("ESTUDIANTE"), eq("Nuevo"))).thenReturn("jwt");

        AuthService.AuthResultado resultado = authService.register(
                new RegisterRequest("Nuevo", "nuevo@edulibre.com", "secreto123"));

        assertEquals("jwt", resultado.token());
        assertEquals("jwt", resultado.response().token());
        assertEquals("nuevo@edulibre.com", resultado.response().email());
        assertEquals("ESTUDIANTE", resultado.response().rol());
        verify(usuarioRepository).save(argThat(usuario ->
                usuario.getRol() == Rol.ESTUDIANTE && "hash".equals(usuario.getPassword())));
    }

    @Test
    void registerRechazaEmailDuplicado() {
        when(usuarioRepository.existsByEmail("dup@edulibre.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(
                new RegisterRequest("Duplicado", "dup@edulibre.com", "secreto123")));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void loginAutenticaYDevuelveToken() {
        Usuario usuario = Usuario.builder()
                .id("u-2")
                .nombre("Ana")
                .email("ana@edulibre.com")
                .password("hash")
                .rol(Rol.ESTUDIANTE)
                .build();

        when(usuarioRepository.findByEmail("ana@edulibre.com")).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(any(), eq("ESTUDIANTE"), eq("Ana"))).thenReturn("jwt-ana");

        AuthService.AuthResultado resultado = authService.login(new LoginRequest("ana@edulibre.com", "secreto123"));

        assertEquals("jwt-ana", resultado.token());
        assertEquals("Ana", resultado.response().nombre());
        verify(authenticationManager).authenticate(any());
    }
}
