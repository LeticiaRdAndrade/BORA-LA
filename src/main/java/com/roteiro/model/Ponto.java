package com.roteiro.model;

// Um ponto turístico que já está salvo no roteiro de uma viagem.
// "ordem" é a posição dele na lista e "visitado" é o check que a pessoa marca.
public record Ponto(int id, int viagemId, String nome, double lat, double lng, int ordem, boolean visitado) {}
