package com.roteiro.model;

// Os dados que chegam do site quando a pessoa cria ou edita uma viagem: só nome e cidade.
// Separei da Viagem porque o id ainda não existe (quem gera é o banco).
public record NovaViagem(String nome, String cidade) {}
