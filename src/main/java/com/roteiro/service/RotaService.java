package com.roteiro.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.roteiro.model.GeoJson;
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
                opcoes.add(new Trecho.Opcao(m.getNome(), calcularMinutos(de, para, m)));
            }
            resultado.add(new Trecho(de.nome(), para.nome(), opcoes));
        }
        return resultado;
    }

    // Pergunta ao OpenRouteService quantos segundos leva e converto para minutos.
    // A resposta é GeoJSON: a rota é a primeira "feature" e a duração fica em properties.summary.duration.
    // "radiuses: -1" manda ele achar a rua mais próxima a qualquer distância: sem isso, lugares dentro
    // de parques ou lagos (ex.: Lago Negro) não tinham rua de carro por perto e davam "sem dado".
    private int calcularMinutos(Ponto origem, Ponto destino, MeioTransporte meio) {
        try {
            Map<String, Object> pedido = Map.of(
                    "coordinates", List.of(List.of(origem.lng(), origem.lat()), List.of(destino.lng(), destino.lat())),
                    "radiuses", List.of(-1, -1));
            GeoJson r = http.post()
                    .uri("https://api.heigit.org/openrouteservice/v2/directions/{perfil}/geojson", meio.getPerfil())
                    .header("Authorization", chave)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(pedido)
                    .retrieve().body(GeoJson.class);

            double segundos = r.features().get(0).properties().summary().duration();
            return (int) Math.round(segundos / 60.0);
        } catch (Exception e) {
            return -1; // não consegui calcular; a tela mostra "sem dado"
        }
    }
}
