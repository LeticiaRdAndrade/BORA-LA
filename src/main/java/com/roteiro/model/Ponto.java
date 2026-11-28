package com.roteiro.model;

// Um ponto turístico que já está salvo no roteiro de uma viagem.
// "ordem" é a posição dele na lista, "visitado" é o check que a pessoa marca,
// "anotacao" é um texto livre (ex.: "chegar às 9h") e "endereco" é onde o lugar fica.
public record Ponto(int id, int viagemId, String nome, double lat, double lng, int ordem, boolean visitado, String anotacao, String endereco) {

    // Distância em linha reta até outro ponto (em graus, serve só para comparar quem está mais perto).
    // Multiplico a diferença de longitude pelo cosseno da latitude porque, longe do Equador,
    // um grau de longitude vale menos quilômetros que um grau de latitude
    public double distanciaAte(Ponto outro) {
        double dLat = outro.lat - lat;
        double dLng = (outro.lng - lng) * Math.cos(Math.toRadians(lat));
        return Math.hypot(dLat, dLng);
    }
}
