package com.roteiro.model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

// Resposta do Wikidata (base de dados da Wikipédia) quando peço vários lugares de uma vez:
// "entities" → código do lugar (ex.: "Q10285") → os dados dele
public record Wikidata(Map<String, Entidade> entities) {

    // "labels": o nome em cada língua ("pt" → { "value": "Coliseu de Roma" })
    // "sitelinks": o artigo do lugar em cada Wikipédia ("ptwiki" → { "title": "Coliseu" })
    // "claims": as informações cadastradas; a "P18" é a foto oficial do lugar (um arquivo do Wikimedia Commons)
    public record Entidade(Map<String, Texto> labels, Map<String, Artigo> sitelinks, Map<String, List<Declaracao>> claims) {

        // O nome em português, ou null se ninguém cadastrou
        public String nomePt() {
            return labels == null || !labels.containsKey("pt") ? null : labels.get("pt").value();
        }

        // O título do artigo numa Wikipédia ("ptwiki" ou "enwiki"), ou null se não tiver artigo
        public String artigo(String wikipedia) {
            return sitelinks == null || !sitelinks.containsKey(wikipedia) ? null : sitelinks.get(wikipedia).title();
        }

        // O endereço da foto oficial (P18), ou null se não tiver. "?width=400" pede uma versão pequena
        public String fotoOficial() {
            if (claims == null || !claims.containsKey("P18")) return null;
            Valor v = claims.get("P18").get(0).mainsnak().datavalue();
            if (v == null) return null;
            String arquivo = URLEncoder.encode(v.value().toString().replace(' ', '_'), StandardCharsets.UTF_8);
            return "https://commons.wikimedia.org/wiki/Special:FilePath/" + arquivo + "?width=400";
        }
    }

    public record Texto(String value) {}

    public record Artigo(String title) {}

    // Uma informação cadastrada: o valor fica em mainsnak → datavalue → value
    // (uso Object porque cada informação tem um tipo diferente; a P18 é um texto com o nome do arquivo)
    public record Declaracao(Snak mainsnak) {}

    public record Snak(Valor datavalue) {}

    public record Valor(Object value) {}
}
