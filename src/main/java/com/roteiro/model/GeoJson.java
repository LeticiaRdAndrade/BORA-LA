package com.roteiro.model;

import java.util.List;

// O formato GeoJSON, que a Geoapify e a OpenTripMap usam para mandar lugares:
// uma lista de "features"; cada uma tem a "geometry" (onde fica no mapa) e as "properties" (os dados).
// São records: o Spring lê o JSON e preenche sozinho os campos que têm o mesmo nome (o resto é ignorado).
public record GeoJson(List<Feature> features) {

    // Um lugar (ou uma cidade) da resposta
    public record Feature(Geometria geometry, Propriedades properties) {}

    // "coordinates" é [longitude, latitude] num ponto, mas vira listas dentro de listas no contorno
    // de uma cidade. Por isso guardo como Object e só leio como número quando é um ponto.
    public record Geometria(String type, Object coordinates) {
        public double lon() { return numero(0); }
        public double lat() { return numero(1); }

        private double numero(int posicao) {
            return ((Number) ((List<?>) coordinates).get(posicao)).doubleValue();
        }
    }

    // Os dados de um lugar. Cada API manda uma parte desses campos; o que não vier fica null
    // (por isso Integer e Double, e não int e double, que não aceitam null).
    // Os nomes com "_" (address_line2, place_id) são iguais aos do JSON da Geoapify.
    // "summary" só vem na rota do OpenRouteService (o resumo do caminho, com a duração em segundos).
    public record Propriedades(String name, Integer rate, String kinds, String wikidata, Double dist,
            String formatted, String address_line2, String place_id, Datasource datasource, Resumo summary) {

        // O lugar é desse tipo? A OpenTripMap manda os tipos juntos (ex.: "museums,cultural,historic")
        public boolean tipo(String t) {
            return kinds != null && kinds.contains(t);
        }

        // A nota vem de 1 a 3; patrimônio histórico vem como 5, 6 e 7 (1h, 2h, 3h). Tiro 4 desses
        // para tudo ficar de 1 a 3 (senão um patrimônio de nota 1 passaria na frente de um de nota 3)
        public int popularidade() {
            int nota = rate == null ? 0 : rate;
            return nota > 3 ? nota - 4 : nota;
        }

        // Estátua, monumento a alguém ou túmulo (as fontes não contam, mesmo tendo escultura)
        public boolean homenagem() {
            return (tipo("monuments_and_memorials") || tipo("sculptures") || tipo("burial_places")) && !tipo("fountains");
        }

        // Código do lugar no Wikidata (ex.: o Coliseu é "Q10285"). A OpenTripMap manda em "wikidata";
        // a Geoapify, em "datasource.raw.wikidata". Homenagens não usam: o código é o da PESSOA homenageada
        // (ex.: a estátua "Pelé" aponta para o Pelé), e não o do lugar.
        public String codigo() {
            if (homenagem()) return null;
            if (wikidata != null) return wikidata;
            return datasource == null || datasource.raw() == null ? null : datasource.raw().wikidata();
        }

        // O número do código ("Q10285" → 10285). Os lugares mais famosos do mundo foram cadastrados
        // primeiro no Wikidata, então têm número menor. Sem código, vai para o fim da fila.
        public long numeroWikidata() {
            String c = codigo();
            return c != null && c.matches("Q\\d+") ? Long.parseLong(c.substring(1)) : Long.MAX_VALUE;
        }

        // Distância (em metros) até o centro da cidade; só a OpenTripMap manda
        public double distancia() {
            return dist == null ? 0 : dist;
        }

        // Endereço para mostrar no cartão: "address_line2" é o endereço sem o nome do lugar;
        // se faltar, uso o endereço completo; se a API não mandar nenhum (OpenTripMap), uso a cidade
        public String endereco(String cidade) {
            return address_line2 != null ? address_line2 : formatted != null ? formatted : cidade;
        }
    }

    // A Geoapify repassa os dados originais do OpenStreetMap em "datasource.raw"
    public record Datasource(Raw raw) {}

    public record Raw(String wikidata) {}

    // Resumo de uma rota do OpenRouteService: quantos segundos leva
    public record Resumo(Double duration) {}
}
