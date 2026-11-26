package com.roteiro.transporte;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.roteiro.model.Ponto;

// HERANÇA: Carro também é filho de MeioTransporte
public class Carro extends MeioTransporte {

    @Override
    public String getNome() {
        return "Carro";
    }

    @Override
    public String getPerfil() {
        return "driving-car";
    }

    // SOBRESCRITA: o pai devolvia null, mas o carro tem app (Uber).
    // Monto o link que abre o Uber já com origem e destino preenchidos.
    @Override
    public String getLinkApp(Ponto origem, Ponto destino) {
        String apelido = URLEncoder.encode(destino.nome(), StandardCharsets.UTF_8);
        return "https://m.uber.com/?action=setPickup"
                + "&pickup[latitude]=" + origem.lat() + "&pickup[longitude]=" + origem.lng()
                + "&dropoff[latitude]=" + destino.lat() + "&dropoff[longitude]=" + destino.lng()
                + "&dropoff[nickname]=" + apelido;
    }
}
