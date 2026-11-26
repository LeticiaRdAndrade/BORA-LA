package com.roteiro.model;

import java.util.List;

// O caminho entre dois pontos seguidos do roteiro, com uma opção para cada meio de transporte.
public record Trecho(String de, String para, List<Opcao> opcoes) {

    // minutos = -1 quando não consegui calcular; link = botão do app (só o carro tem)
    public record Opcao(String meio, int minutos, String link) {}
}
