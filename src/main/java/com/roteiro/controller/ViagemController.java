package com.roteiro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roteiro.model.Viagem;
import com.roteiro.service.ViagemService;

@RestController
@RequestMapping("/api/viagens")
public class ViagemController {

    @Autowired
    ViagemService service;

    // O React manda {nome, cidade}; o id vem zerado e o banco gera o verdadeiro
    @PostMapping
    public Viagem criar(@RequestBody Viagem viagem) {
        return service.criar(viagem);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Viagem> buscar(@PathVariable int id) {
        Viagem viagem = service.buscar(id);
        return viagem == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(viagem);
    }
}
