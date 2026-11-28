package com.roteiro.service;

import java.awt.geom.Path2D;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roteiro.model.GeoJson;
import com.roteiro.model.Lugar;

// O LugarService não chama nenhuma API direto: ele COORDENA os services de cada API
// (Geoapify, OpenTripMap e Wikidata) e aplica as regras do site (filtros, ordem, tradução).
// Assim cada classe tem uma responsabilidade só.
@Service
public class LugarService {

    @Autowired
    GeoapifyService geoapify;

    @Autowired
    OpenTripMapService openTripMap;

    @Autowired
    WikidataService wikidata;

    // CACHE: guardo na memória o resultado de cada busca (cidade + tipo). Na segunda vez que alguém abre
    // a mesma cidade com o mesmo filtro, a resposta sai na hora, sem chamar as APIs de novo
    private final Map<String, List<Lugar>> cache = new ConcurrentHashMap<>();

    // Palavras que tiram o lugar dos filtros por tipo: vinham campus de faculdade e quiosques como "parque"
    // e buffet infantil como "diversão"
    private static final List<String> FORA = List.of("universidade", "quiosque", "buffet");

    // EXPLORAR: acho a cidade no mapa (o centro e o contorno dela) e busco os lugares.
    // O tipo "populares" são os mais visitados
    public List<Lugar> explorar(String nomeCidade, String tipo) {
        String chave = nomeCidade + "|" + tipo;
        if (cache.containsKey(chave)) return cache.get(chave);
        try {
            GeoJson.Feature cidade = geoapify.cidade(nomeCidade);
            Path2D contorno = geoapify.contorno(cidade);
            List<Lugar> lugares = tipo.equals("populares")
                    ? maisVisitados(cidade, contorno, nomeCidade) : porTipo(cidade, contorno, tipo, nomeCidade);
            // só guardo se veio pelo menos uma foto: lista vazia ou sem nenhuma foto pode ter sido erro de alguma API
            if (lugares.stream().anyMatch(l -> l.foto() != null)) cache.put(chave, lugares);
            return lugares;
        } catch (Exception e) {
            return new ArrayList<>(); // se a cidade não existir ou a API falhar, a tela avisa
        }
    }

    // Sugestões de cidade enquanto a pessoa digita
    public List<String> cidades(String texto) {
        try {
            return geoapify.sugestoes(texto);
        } catch (Exception e) {
            return new ArrayList<>(); // sem sugestões; a pessoa ainda pode digitar a cidade inteira
        }
    }

    // MAIS VISITADOS (OpenTripMap, que dá uma nota de popularidade para cada lugar):
    // 1º) peço só os de nota máxima (3) num raio de 15 km. Em cidade grande, como Roma, há mais de 500
    //     lugares e a API corta no limite; pedindo só os de nota 3, os famosos (o Coliseu) não ficam de fora.
    //     O raio é grande para pegar os pontos longe do centro (no Rio, Copacabana e o Pão de Açúcar),
    //     e o contorno da cidade ("dentro") tira o que for de cidade vizinha.
    // 2º) se vierem menos de 10 (cidade pequena, como Serra Negra ou Bonito), peço todas as notas
    //     num raio de 20 km, porque lá as atrações (grutas, rios, cachoeiras) ficam longe do centro.
    private List<Lugar> maisVisitados(GeoJson.Feature cidade, Path2D contorno, String nomeCidade) {
        List<GeoJson.Feature> achados = dentro(contorno, openTripMap.emVolta(cidade, 15000, 3));
        if (achados.size() < 10) achados = dentro(contorno, openTripMap.emVolta(cidade, 20000, 1));

        // primeiro a maior nota; no empate, o mais famoso (menor número no Wikidata); depois, o mais perto
        achados.sort(Comparator.comparingInt((GeoJson.Feature f) -> f.properties().popularidade()).reversed()
                .thenComparingLong(f -> f.properties().numeroWikidata())
                .thenComparingDouble(f -> f.properties().distancia()));

        List<GeoJson.Feature> escolhidos = new ArrayList<>();
        Set<String> vistos = new HashSet<>(); // para não repetir o mesmo lugar (mesmo nome ou mesmo código)
        for (GeoJson.Feature f : achados) {
            GeoJson.Propriedades p = f.properties();
            if (p.name() == null || p.name().isBlank() || !vistos.add(p.name())) continue;
            if (p.codigo() != null && !vistos.add(p.codigo())) continue;
            // tiro cemitérios, restaurantes, igrejas comuns (só catedrais), praças (vinham muitas) e
            // "outros prédios" (escolas, casas e prédios comuns que só são antigos)
            if (p.tipo("cemeteries") || p.tipo("restaurants") || p.tipo("squares") || p.tipo("other_buildings_and_structures")) continue;
            if (p.tipo("religion") && !p.tipo("cathedrals")) continue;
            escolhidos.add(f);
            if (escolhidos.size() == 10) break; // mostro só os 10 mais visitados
        }
        return emPortugues(escolhidos, nomeCidade);
    }

    // POR TIPO (Geoapify): os famosos primeiro (os que têm código no Wikidata) e as praças por último.
    // A ordenação do Java é estável, então dentro de cada grupo continua a ordem do mais perto ao mais longe.
    // Mostro no máximo 12: numa cidade grande vinham 50 lugares, e os do fim eram coisas sem graça
    // (galerias de loja, "entrada/saida", nomes em outra língua)
    private List<Lugar> porTipo(GeoJson.Feature cidade, Path2D contorno, String tipo, String nomeCidade) {
        List<GeoJson.Feature> achados = dentro(contorno, geoapify.porTipo(tipo, cidade));
        achados.sort(Comparator.comparing((GeoJson.Feature f) -> f.properties().codigo() == null)
                .thenComparing(f -> praca(f.properties().name())));

        List<GeoJson.Feature> escolhidos = new ArrayList<>();
        Set<String> vistos = new HashSet<>();
        int pracas = 0;
        for (GeoJson.Feature f : achados) {
            String nome = f.properties().name();
            if (nome == null || nome.isBlank() || !vistos.add(nome)) continue; // ignoro sem nome e repetidos
            if (FORA.stream().anyMatch(semAcento(nome)::contains)) continue;
            // nome sem nenhuma letra do nosso alfabeto (ex.: em russo) e sem tradução no Wikidata
            if (f.properties().codigo() == null && !nome.matches(".*\\p{IsLatin}.*")) continue;
            if (praca(nome) && ++pracas > 5) continue; // no máximo 5 praças (vinham dezenas)
            escolhidos.add(f);
            if (escolhidos.size() == 12) break;
        }
        return emPortugues(escolhidos, nomeCidade);
    }

    // É uma praça (ou um largo, que é a mesma coisa)? Comparo sem acento porque às vezes vem escrito "Praca"
    private boolean praca(String nome) {
        return nome != null && (semAcento(nome).startsWith("praca") || semAcento(nome).startsWith("largo"));
    }

    // BUSCAR PELO NOME: procuro o texto até 30 km da cidade em duas fontes, porque cada uma acha coisas
    // que a outra não acha: a Geoapify (museus, lojas, endereços...) e a OpenTripMap (pontos turísticos).
    // Junto as duas listas sem repetir (até 6), passo para português com foto e coloco primeiro
    // os lugares que têm o texto digitado no nome.
    public List<Lugar> buscar(String texto, String nomeCidade) {
        try {
            GeoJson.Feature cidade = geoapify.cidade(nomeCidade);
            List<GeoJson.Feature> achados = new ArrayList<>(geoapify.buscar(texto, cidade));
            achados.addAll(openTripMap.buscar(texto, cidade));

            List<GeoJson.Feature> escolhidos = new ArrayList<>();
            Set<String> vistos = new HashSet<>();
            for (GeoJson.Feature f : achados) {
                String nome = f.properties().name();
                if (nome == null || nome.isBlank() || !vistos.add(nome)) continue;
                escolhidos.add(f);
                if (escolhidos.size() == 6) break;
            }
            List<Lugar> lugares = emPortugues(escolhidos, nomeCidade);
            lugares.sort(Comparator.comparing((Lugar l) -> !semAcento(l.nome()).contains(semAcento(texto))));
            return lugares;
        } catch (Exception e) {
            return new ArrayList<>(); // se der erro, a tela mostra "não achei"
        }
    }

    // Tira os lugares que ficam fora do contorno da cidade (sem contorno, deixa todos).
    // Devolvo uma lista nova, que pode ser ordenada e alterada à vontade
    private List<GeoJson.Feature> dentro(Path2D contorno, List<GeoJson.Feature> lugares) {
        List<GeoJson.Feature> resultado = new ArrayList<>(lugares);
        if (contorno != null) resultado.removeIf(f -> !contorno.contains(f.geometry().lon(), f.geometry().lat()));
        return resultado;
    }

    // Transforma os lugares das APIs no Lugar do meu sistema, com o nome em português e a foto do
    // Wikidata quando tiver (a API manda [longitude, latitude], então pego lat e lon pelo nome)
    private List<Lugar> emPortugues(List<GeoJson.Feature> achados, String nomeCidade) {
        Map<String, WikidataService.Traducao> traducoes;
        try {
            traducoes = wikidata.nomesEFotos(achados.stream().map(f -> f.properties().codigo()).toList());
        } catch (Exception e) {
            traducoes = new HashMap<>(); // se o Wikidata falhar, fico com o nome original e sem foto
        }

        List<Lugar> lugares = new ArrayList<>();
        for (GeoJson.Feature f : achados) {
            GeoJson.Propriedades p = f.properties();
            WikidataService.Traducao t = traducoes.getOrDefault(p.codigo(), new WikidataService.Traducao(null, null));
            String nome = t.nome() != null ? t.nome() : p.name();
            lugares.add(new Lugar(nome, f.geometry().lat(), f.geometry().lon(), p.endereco(nomeCidade), t.foto()));
        }
        return lugares;
    }

    // Deixa o texto em minúsculas e sem acento, para "Café" e "cafe" serem iguais na comparação
    private String semAcento(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase();
    }
}
