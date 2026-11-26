package com.roteiro.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.roteiro.model.Lugar;

// Aqui eu descubro lugares usando duas APIs gratuitas:
// OpenTripMap (pontos turísticos de uma cidade) e OpenRouteService (achar um lugar pelo nome).
@Service
@SuppressWarnings("unchecked")
public class LugarService {

    @Value("${chaves.opentripmap}")
    private String chaveOtm;

    @Value("${chaves.ors}")
    private String chaveOrs;

    // RestClient é a ferramenta do Spring para chamar outras APIs
    private final RestClient http = RestClient.create();

    // EXPLORAR: primeiro pego as coordenadas da cidade, depois os pontos turísticos ao redor dela
    public List<Lugar> explorar(String cidade) {
        List<Lugar> lugares = new ArrayList<>();
        try {
            Map<String, Object> c = http.get()
                    .uri("https://api.opentripmap.com/0.1/en/places/geoname?name={nome}&apikey={chave}", cidade, chaveOtm)
                    .retrieve().body(Map.class);

            // radius = 8 km, rate=1 tira os lugares sem nenhuma relevância
            List<Map<String, Object>> achados = http.get()
                    .uri("https://api.opentripmap.com/0.1/en/places/radius?radius=8000&lon={lon}&lat={lat}"
                            + "&kinds=interesting_places&rate=1&format=json&limit=30&apikey={chave}",
                            c.get("lon"), c.get("lat"), chaveOtm)
                    .retrieve().body(List.class);

            for (Map<String, Object> a : achados) {
                String nome = (String) a.get("name");
                if (nome == null || nome.isBlank()) continue; // ignoro lugares sem nome
                Map<String, Object> ponto = (Map<String, Object>) a.get("point");
                lugares.add(new Lugar(nome, ((Number) ponto.get("lat")).doubleValue(), ((Number) ponto.get("lon")).doubleValue()));
            }
        } catch (Exception e) {
            // se a cidade não existir ou a API falhar, devolvo a lista vazia e a tela avisa
        }
        return lugares;
    }

    // DIGITAR: transformo o texto digitado ("Museu do Café, Santos") em nome + coordenadas
    public Lugar buscar(String texto) {
        try {
            Map<String, Object> r = http.get()
                    .uri("https://api.openrouteservice.org/geocode/search?api_key={chave}&text={texto}&size=1", chaveOrs, texto)
                    .retrieve().body(Map.class);

            Map<String, Object> primeiro = ((List<Map<String, Object>>) r.get("features")).get(0);
            List<Number> coord = (List<Number>) ((Map<String, Object>) primeiro.get("geometry")).get("coordinates");
            String nome = (String) ((Map<String, Object>) primeiro.get("properties")).get("name");

            // a API manda [longitude, latitude], por isso inverto aqui
            return new Lugar(nome, coord.get(1).doubleValue(), coord.get(0).doubleValue());
        } catch (Exception e) {
            return null; // não achei o lugar
        }
    }
}
