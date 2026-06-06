package com.example.auth.dto;

public class AuthResponse {

    private String username;
    private String role;
    private String nomeProfessor;
    private String mensagem;

    public AuthResponse(String username, String role, String nomeProfessor, String mensagem) {
        this.username = username;
        this.role = role;
        this.nomeProfessor = nomeProfessor;
        this.mensagem = mensagem;
    }

    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getNomeProfessor() { return nomeProfessor; }
    public String getMensagem() { return mensagem; }
}
