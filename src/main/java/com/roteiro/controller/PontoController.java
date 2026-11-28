package com.roteiro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roteiro.model.EdicaoPonto;
import com.roteiro.model.Lugar;
import com.roteiro.model.Ponto;
import com.roteiro.service.PontoService;

@RestController
@RequestMapping("/api")
public class PontoController {

    @Autowired
    PontoService service;

    @GetMapping("/viagens/{id}/pontos")
    public List<Ponto> listar(@PathVariable int id) {
        return service.listar(id);
    }

    @PostMapping("/viagens/{id}/pontos")
    public void adicionar(@PathVariable int id, @RequestBody Lugar lugar) {
        service.adicionar(id, lugar);
    }

    // O React manda a lista de ids já na nova ordem
    @PutMapping("/viagens/{id}/ordem")
    public void reordenar(@RequestBody List<Integer> ids) {
        service.reordenar(ids);
    }

    // Botão "Otimizar rota": o back-end reorganiza os pontos pelo mais próximo
    @PutMapping("/viagens/{id}/otimizar")
    public void otimizar(@PathVariable int id) {
        service.otimizar(id);
    }

    // Formulário de edição: o React manda {nome, anotacao}
    @PutMapping("/pontos/{id}")
    public void editar(@PathVariable int id, @RequestBody EdicaoPonto edicao) {
        service.editar(id, edicao);
    }

    @DeleteMapping("/pontos/{id}")
    public void remover(@PathVariable int id) {
        service.remover(id);
    }

    @PutMapping("/pontos/{id}/visitado")
    public void alternarVisitado(@PathVariable int id) {
        service.alternarVisitado(id);
    }
}
