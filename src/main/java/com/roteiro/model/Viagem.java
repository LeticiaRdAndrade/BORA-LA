package com.roteiro.model;

// Uma viagem tem só um nome e uma cidade.
// Usei "record" porque é uma classe feita só para guardar dados:
// o Java cria sozinho o construtor e os getters (ex.: viagem.nome()).
public record Viagem(Integer id, String nome, String cidade) {}
