package com.roteiro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.roteiro.model.Trecho;
import com.roteiro.service.PontoService;
import com.roteiro.service.RotaService;

@RestController
public class RotaController {

    @Autowired
    PontoService pontoService;

    @Autowired
    RotaService rotaService;

    // Devolve o tempo entre cada par de pontos seguidos do roteiro
    @GetMapping("/api/viagens/{id}/trechos")
    public List<Trecho> trechos(@PathVariable int id) {
        return rotaService.trechos(pontoService.listar(id));
    }
}
