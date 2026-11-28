package com.roteiro.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.roteiro.model.Wikidata;
import com.roteiro.model.Wikipedia;

// Nome em português e foto dos lugares, usando o Wikidata e a Wikipédia (não precisam de chave).
// As outras APIs mandam o nome em inglês ou na língua do país, e não mandam foto.
@Service
public class WikidataService {

    private final RestClient http = RestClient.create();

    // A Wikimedia pede que todo programa diga o nome dele e um contato em cada pedido
    private static final String PROGRAMA = "BoraLa/1.0 (https://github.com/LeticiaRdAndrade/BORA-LA)";

    // O que descobri de um lugar: o nome em português e a foto (os dois podem ser null)
    public record Traducao(String nome, String foto) {}

    // CACHE: guardo na memória o que já descobri de cada lugar. Abrir a mesma cidade de novo não pergunta
    // nada à Wikimedia: fica mais rápido e não estoura o limite de pedidos dela.
    // ConcurrentHashMap é um HashMap que aguenta várias pessoas usando o site ao mesmo tempo.
    private final Map<String, Traducao> cache = new ConcurrentHashMap<>();

    // Para cada código do Wikidata (ex.: "Q10285"), descubro o nome em português e uma foto:
    // 1) no Wikidata: o nome em português e o título do artigo na Wikipédia em português e em inglês
    // 2) na Wikipédia: a foto principal de cada artigo (primeiro a do artigo em português; se não tiver, a do inglês)
    // São no máximo 4 pedidos para a lista inteira, e só para os lugares que ainda não estão no cache.
    public Map<String, Traducao> nomesEFotos(List<String> codigos) {
        List<String> faltam = codigos.stream().filter(c -> c != null && !cache.containsKey(c)).distinct().limit(50).toList();
        if (!faltam.isEmpty()) {
            Wikidata r = pedir(Wikidata.class, "https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&ids={ids}"
                    + "&props=labels|sitelinks&languages=pt&sitefilter=ptwiki|enwiki", String.join("|", faltam));

            Map<String, String> fotosPt = fotos("pt", faltam.stream().map(c -> artigo(r, c, "ptwiki")).toList());
            // só pergunto à Wikipédia em inglês pelos lugares que ficaram sem foto em português
            Map<String, String> fotosEn = fotos("en", faltam.stream()
                    .filter(c -> !fotosPt.containsKey(artigo(r, c, "ptwiki"))).map(c -> artigo(r, c, "enwiki")).toList());

            Map<String, String> fotos = new HashMap<>();
            for (String c : faltam) {
                String foto = fotosPt.getOrDefault(artigo(r, c, "ptwiki"), fotosEn.get(artigo(r, c, "enwiki")));
                if (foto != null) fotos.put(c, foto);
            }
            // 3) quem ainda ficou sem foto (lugar sem artigo na Wikipédia) pode ter a foto oficial cadastrada
            // no Wikidata. Só peço as informações ("claims") desses, porque elas são grandes
            fotos.putAll(fotosOficiais(faltam.stream().filter(c -> !fotos.containsKey(c)).toList()));

            for (String c : faltam) {
                Wikidata.Entidade e = r.entities().get(c);
                cache.put(c, new Traducao(e == null ? null : e.nomePt(), fotos.get(c)));
            }
        }
        Map<String, Traducao> resultado = new HashMap<>();
        for (String c : codigos) if (c != null && cache.containsKey(c)) resultado.put(c, cache.get(c));
        return resultado;
    }

    // A foto oficial (P18) de cada lugar da lista, direto do Wikidata: código → endereço da foto
    private Map<String, String> fotosOficiais(List<String> codigos) {
        Map<String, String> fotos = new HashMap<>();
        if (codigos.isEmpty()) return fotos;
        Wikidata r = pedir(Wikidata.class, "https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&ids={ids}&props=claims",
                String.join("|", codigos));
        for (String c : codigos) {
            Wikidata.Entidade e = r.entities().get(c);
            if (e != null && e.fotoOficial() != null) fotos.put(c, e.fotoOficial());
        }
        return fotos;
    }

    // Faz um pedido à Wikimedia e devolve a resposta já como objeto ("tipo" é a classe da resposta:
    // Wikidata.class ou Wikipedia.class). A Wikimedia só aceita uns 6 pedidos seguidos; depois responde
    // "429 Too Many Requests" e pede para esperar 5 segundos. Então eu espero e tento de novo (até 3 vezes).
    private <T> T pedir(Class<T> tipo, String endereco, Object... valores) {
        for (int tentativa = 1; ; tentativa++) {
            try {
                return http.get().uri(endereco, valores).header("User-Agent", PROGRAMA).retrieve().body(tipo);
            } catch (HttpClientErrorException.TooManyRequests e) {
                if (tentativa == 3) throw e;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException i) {
                    throw e;
                }
            }
        }
    }

    // O título do artigo do lugar numa Wikipédia, ou null se ele não tiver artigo lá
    private String artigo(Wikidata r, String codigo, String wikipedia) {
        Wikidata.Entidade e = r.entities().get(codigo);
        return e == null ? null : e.artigo(wikipedia);
    }

    // Pergunta à Wikipédia ("pt" ou "en") a foto principal de vários artigos numa vez só.
    // Devolve título do artigo → endereço da foto (só os artigos que têm foto)
    private Map<String, String> fotos(String lingua, List<String> titulos) {
        Map<String, String> fotos = new HashMap<>();
        List<String> validos = titulos.stream().filter(t -> t != null).distinct().toList();
        if (validos.isEmpty()) return fotos;
        Wikipedia r = pedir(Wikipedia.class, "https://{lingua}.wikipedia.org/w/api.php?action=query&format=json&prop=pageimages"
                + "&piprop=thumbnail&pithumbsize=400&titles={titulos}", lingua, String.join("|", validos));
        if (r.query() == null) return fotos;
        for (Wikipedia.Pagina p : r.query().pages().values()) {
            if (p.thumbnail() != null) fotos.put(p.title(), p.thumbnail().source());
        }
        return fotos;
    }
}
