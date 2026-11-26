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

    // Tela "Explorar": pontos turísticos de uma cidade
    @GetMapping("/explorar")
    public List<Lugar> explorar(@RequestParam String cidade) {
        return service.explorar(cidade);
    }

    // Tela "Digitar um lugar": acha um lugar específico pelo texto
    @GetMapping("/buscar")
    public Lugar buscar(@RequestParam String texto) {
        return service.buscar(texto);
    }
}
