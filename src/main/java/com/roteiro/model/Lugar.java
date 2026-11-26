package com.roteiro.model;

// Um lugar que ainda NÃO está no roteiro (vem da busca por cidade ou do formulário).
public record Lugar(String nome, double lat, double lng) {}
