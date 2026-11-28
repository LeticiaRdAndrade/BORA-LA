package com.roteiro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.roteiro.model.GeoJson;

// Tudo que eu peço à OpenTripMap: os pontos turísticos em volta da cidade (com a nota de popularidade)
// e a busca pelo nome. As respostas já chegam como objetos GeoJson.
@Service
public class OpenTripMapService {

    @Value("${chaves.opentripmap}")
    private String chave;

    private final RestClient http = RestClient.create();

    // Os lugares em volta da cidade, com nota mínima "nota", só de categorias de turismo
    // (cultural, historic, architecture, natural e amusements): assim ficam de fora UBS, ONGs etc.
    public List<GeoJson.Feature> emVolta(GeoJson.Feature cidade, int raio, int nota) {
        return http.get()
                .uri("https://api.opentripmap.com/0.1/en/places/radius?radius={raio}&lon={lon}&lat={lat}"
                        + "&kinds=cultural,historic,architecture,natural,amusements&rate={nota}&limit=500&apikey={chave}",
                        raio, cidade.geometry().lon(), cidade.geometry().lat(), nota, chave)
                .retrieve().body(GeoJson.class).features();
    }

    // BUSCA PELO NOME: até 5 pontos turísticos com esse nome a até 30 km da cidade
    public List<GeoJson.Feature> buscar(String texto, GeoJson.Feature cidade) {
        return http.get()
                .uri("https://api.opentripmap.com/0.1/en/places/autosuggest?name={texto}&radius=30000"
                        + "&lon={lon}&lat={lat}&limit=5&apikey={chave}",
                        texto, cidade.geometry().lon(), cidade.geometry().lat(), chave)
                .retrieve().body(GeoJson.class).features();
    }
}
