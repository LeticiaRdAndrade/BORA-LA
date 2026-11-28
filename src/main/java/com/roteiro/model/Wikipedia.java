package com.roteiro.model;

import java.util.Map;

// Resposta da Wikipédia quando peço a foto principal de vários artigos de uma vez:
// "query" → "pages" → (número da página) → o título e a miniatura da foto
public record Wikipedia(Consulta query) {

    public record Consulta(Map<String, Pagina> pages) {}

    public record Pagina(String title, Miniatura thumbnail) {}

    // "source" é o endereço da foto, já no tamanho que pedi
    public record Miniatura(String source) {}
}
