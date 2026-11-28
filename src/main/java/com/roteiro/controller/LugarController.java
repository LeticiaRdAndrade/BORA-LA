package com.roteiro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.roteiro.model.Lugar;
import com.roteiro.service.LugarService;

@RestController
@RequestMapping("/api/lugares")
public class LugarController {

    @Autowired
    LugarService service;

    // Tela "Explorar": pontos turísticos de uma cidade, filtrados pelo tipo (parques, museus...)
    @GetMapping("/explorar")
    public List<Lugar> explorar(@RequestParam String cidade, @RequestParam(defaultValue = "populares") String tipo) {
        return service.explorar(cidade, tipo);
    }

    // Formulário da viagem: sugestões de cidade enquanto a pessoa digita
    @GetMapping("/cidades")
    public List<String> cidades(@RequestParam String texto) {
        return service.cidades(texto);
    }

    // Tela "Buscar pelo nome": acha lugares pelo nome, perto da cidade da viagem
    @GetMapping("/buscar")
    public List<Lugar> buscar(@RequestParam String texto, @RequestParam String cidade) {
        return service.buscar(texto, cidade);
    }
}
