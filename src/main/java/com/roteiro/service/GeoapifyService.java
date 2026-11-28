package com.roteiro.service;

import java.awt.geom.Path2D;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.roteiro.model.GeoJson;

// Tudo que eu peço à Geoapify: achar a cidade, o contorno dela, as sugestões de cidade,
// os lugares por tipo e a busca pelo nome. As respostas já chegam como objetos GeoJson.
@Service
public class GeoapifyService {

    @Value("${chaves.geoapify}")
    private String chave;

    // RestClient é a ferramenta do Spring para chamar outras APIs
    private final RestClient http = RestClient.create();

    // Cada tipo do site vira uma categoria da Geoapify
    private static final Map<String, String> TIPOS = Map.of(
            "parques", "leisure.park",
            "museus", "entertainment.museum",
            "cultura", "entertainment.culture",
            "mirantes", "tourism.attraction.viewpoint",
            "praias", "beach",
            "catedrais", "tourism.sights.place_of_worship.cathedral",
            "shoppings", "commercial.shopping_mall",
            "diversao", "entertainment.theme_park");

    // Acha a cidade pelo nome. O primeiro resultado tem o centro e o "place_id" (que uso para o contorno)
    public GeoJson.Feature cidade(String nome) {
        return http.get()
                .uri("https://api.geoapify.com/v1/geocode/search?text={nome}&limit=1&apiKey={chave}", nome, chave)
                .retrieve().body(GeoJson.class).features().get(0);
    }

    // SUGESTÕES DE CIDADE: enquanto a pessoa digita, devolvo as cidades com aquele nome já com estado e país
    // (ex.: "Bonito, MS, Brasil"), para ela escolher a certa entre as cidades de mesmo nome
    public List<String> sugestoes(String texto) {
        GeoJson r = http.get()
                .uri("https://api.geoapify.com/v1/geocode/autocomplete?text={texto}&type=city&lang=pt&limit=6&apiKey={chave}",
                        texto, chave)
                .retrieve().body(GeoJson.class);
        return r.features().stream().map(f -> f.properties().formatted()).toList();
    }

    // POR TIPO: os lugares da categoria num raio de 15 km da cidade (o mesmo dos mais visitados), os mais
    // perto primeiro. Peço 200 para os famosos (que podem estar longe do centro) virem junto
    public List<GeoJson.Feature> porTipo(String tipo, GeoJson.Feature cidade) {
        return http.get()
                .uri("https://api.geoapify.com/v2/places?categories={categorias}&filter=circle:{lon},{lat},15000"
                        + "&bias=proximity:{lon},{lat}&limit=200&apiKey={chave}",
                        Map.of("categorias", TIPOS.getOrDefault(tipo, "tourism.attraction"),
                                "lon", cidade.geometry().lon(), "lat", cidade.geometry().lat(), "chave", chave))
                .retrieve().body(GeoJson.class).features();
    }

    // BUSCA PELO NOME: até 5 lugares com esse nome a até 30 km da cidade
    public List<GeoJson.Feature> buscar(String texto, GeoJson.Feature cidade) {
        return http.get()
                .uri("https://api.geoapify.com/v1/geocode/search?text={texto}&filter=circle:{lon},{lat},30000"
                        + "&bias=proximity:{lon},{lat}&lang=pt&limit=5&apiKey={chave}",
                        Map.of("texto", texto, "lon", cidade.geometry().lon(), "lat", cidade.geometry().lat(), "chave", chave))
                .retrieve().body(GeoJson.class).features();
    }

    // ENDEREÇO COMPLETO: pela latitude e longitude, a Geoapify diz a rua, o número, o bairro e o CEP
    // ("geocodificação reversa"). Se não achar, devolvo null
    public String endereco(double lat, double lon) {
        try {
            return http.get()
                    .uri("https://api.geoapify.com/v1/geocode/reverse?lat={lat}&lon={lon}&lang=pt&apiKey={chave}", lat, lon, chave)
                    .retrieve().body(GeoJson.class).features().get(0).properties().formatted();
        } catch (Exception e) {
            return null;
        }
    }

    // CONTORNO DA CIDADE: as buscas são num círculo em volta do centro, então às vezes vinham lugares da
    // cidade vizinha (ex.: pesquisar Santos e vir o Guarujá). Por isso peço o desenho do município
    // (um polígono, ou vários se a cidade tiver ilhas) e guardo num Path2D, uma classe do próprio Java
    // que sabe dizer se um ponto está dentro do desenho (contains).
    // Se a cidade não tiver desenho (ex.: um distrito, que vem só como um ponto), devolvo null.
    public Path2D contorno(GeoJson.Feature cidade) {
        try {
            GeoJson.Geometria geo = http.get()
                    .uri("https://api.geoapify.com/v2/place-details?id={id}&features=details&apiKey={chave}",
                            cidade.properties().place_id(), chave)
                    .retrieve().body(GeoJson.class).features().get(0).geometry();
            if (geo.type().equals("Point")) return null;
            Path2D contorno = new Path2D.Double(Path2D.WIND_EVEN_ODD); // "even odd": os buracos do polígono ficam de fora
            desenhar(contorno, (List<?>) geo.coordinates());
            return contorno;
        } catch (Exception e) {
            return null; // sem contorno, a busca só não filtra as cidades vizinhas
        }
    }

    // As coordenadas do desenho vêm em listas dentro de listas (polígonos → anéis → pontos [lon, lat]).
    // Vou descendo (recursão) até achar uma lista de pontos, e aí desenho esse anel ligando um ponto no outro
    private void desenhar(Path2D contorno, List<?> lista) {
        if (((List<?>) lista.get(0)).get(0) instanceof Number) {
            for (int i = 0; i < lista.size(); i++) {
                List<?> p = (List<?>) lista.get(i);
                double lon = ((Number) p.get(0)).doubleValue(), lat = ((Number) p.get(1)).doubleValue();
                if (i == 0) contorno.moveTo(lon, lat);
                else contorno.lineTo(lon, lat);
            }
            contorno.closePath();
        } else {
            for (Object parte : lista) desenhar(contorno, (List<?>) parte);
        }
    }
}
