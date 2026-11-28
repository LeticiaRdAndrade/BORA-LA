package com.roteiro.model;

// Um lugar que ainda NÃO está no roteiro (vem da busca por cidade ou do formulário).
// "foto" é o endereço de uma foto do lugar (do Wikimedia Commons); fica null quando não tem.
public record Lugar(String nome, double lat, double lng, String endereco, String foto) {}
