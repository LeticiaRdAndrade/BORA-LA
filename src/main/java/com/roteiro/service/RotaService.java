package com.roteiro.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.roteiro.model.Ponto;
import com.roteiro.model.Trecho;
import com.roteiro.transporte.APe;
import com.roteiro.transporte.Carro;
import com.roteiro.transporte.MeioTransporte;

// Calcula quanto tempo leva de um ponto até o próximo, usando o OpenRouteService
@Service
public class RotaService {

    @Value("${chaves.ors}")
    private String chave;

    private final RestClient http = RestClient.create();

    // POLIMORFISMO: a lista é do tipo do pai (MeioTransporte), mas guarda objetos dos filhos (APe e Carro)
    private final List<MeioTransporte> meios = List.of(new APe(), new Carro());

    // SOBRECARGA (1/2): sem dizer os meios, uso todos
    public List<Trecho> trechos(List<Ponto> pontos) {
        return trechos(pontos, meios);
    }

    // SOBRECARGA (2/2): mesmo nome, mas agora recebe também quais meios calcular
    public List<Trecho> trechos(List<Ponto> pontos, List<MeioTransporte> escolhidos) {
        List<Trecho> resultado = new ArrayList<>();

        for (int i = 0; i < pontos.size() - 1; i++) {
            Ponto de = pontos.get(i);
            Ponto para = pontos.get(i + 1);
            List<Trecho.Opcao> opcoes = new ArrayList<>();

            // POLIMORFISMO: chamo os mesmos métodos sem saber se é APe ou Carro;
            // cada um responde do seu jeito (SOBRESCRITA)
            for (MeioTransporte m : escolhidos) {
                opcoes.add(new Trecho.Opcao(m.getNome(), calcularMinutos(de, para, m), m.getLinkApp(de, para)));
            }
            resultado.add(new Trecho(de.nome(), para.nome(), opcoes));
        }
        return resultado;
    }

    // Pergunta ao OpenRouteService quantos segundos leva e converto para minutos
    @SuppressWarnings("unchecked")
    private int calcularMinutos(Ponto origem, Ponto destino, MeioTransporte meio) {
        try {
            Map<String, Object> r = http.get()
                    .uri("https://api.openrouteservice.org/v2/directions/{perfil}?api_key={chave}"
                            + "&start={lng1},{lat1}&end={lng2},{lat2}",
                            meio.getPerfil(), chave, origem.lng(), origem.lat(), destino.lng(), destino.lat())
                    .retrieve().body(Map.class);

            Map<String, Object> primeiro = ((List<Map<String, Object>>) r.get("features")).get(0);
            Map<String, Object> props = (Map<String, Object>) primeiro.get("properties");
            Map<String, Object> resumo = (Map<String, Object>) props.get("summary");
            double segundos = ((Number) resumo.get("duration")).doubleValue();
            return (int) Math.round(segundos / 60.0);
        } catch (Exception e) {
            return -1; // não consegui calcular; a tela mostra "sem dado"
        }
    }
}
