package com.example.auth.service;

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.entity.Usuario;
import com.example.auth.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
    }

    public AuthResponse login(LoginRequest request) {
        validarUsuarioAtivo(request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        return buildResponse(authentication, "Autenticação realizada com sucesso. Use Basic Auth nas próximas requisições.");
    }

    public AuthResponse me(Authentication authentication) {
        return buildResponse(authentication, "Usuário autenticado.");
    }

    private AuthResponse buildResponse(Authentication authentication, String mensagem) {
        return usuarioRepository.findByUsername(authentication.getName())
                .map(usuario -> fromUsuario(usuario, mensagem))
                .orElseGet(() -> fromAuthentication(authentication, mensagem));
    }

    private AuthResponse fromUsuario(Usuario usuario, String mensagem) {
        // nomeProfessor agora vem do próprio campo local (sem join JPA com Professor)
        return new AuthResponse(
                usuario.getUsername(),
                usuario.getRole().name(),
                usuario.getNomeProfessor(),
                mensagem
        );
    }

    private AuthResponse fromAuthentication(Authentication authentication, String mensagem) {
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_PROFESSOR");

        return new AuthResponse(authentication.getName(), role, null, mensagem);
    }

    private void validarUsuarioAtivo(String username) {
        usuarioRepository.findByUsername(username)
                .filter(usuario -> Boolean.FALSE.equals(usuario.getEnabled()))
                .ifPresent(usuario -> {
                    throw new DisabledException("Este usuário está inativo. Procure o administrador.");
                });
    }
}
