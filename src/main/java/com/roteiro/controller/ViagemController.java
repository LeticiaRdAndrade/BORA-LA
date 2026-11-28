package com.roteiro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roteiro.model.NovaViagem;
import com.roteiro.model.Viagem;
import com.roteiro.service.ViagemService;

@RestController
@RequestMapping("/api/viagens")
public class ViagemController {

    @Autowired
    ViagemService service;

    // O React manda {nome, cidade}; o id quem gera é o banco
    @PostMapping
    public Viagem criar(@RequestBody NovaViagem nova) {
        return service.criar(nova);
    }

    // Formulário de edição da viagem: o React manda {nome, cidade} e eu devolvo a viagem já alterada
    @PutMapping("/{id}")
    public Viagem editar(@PathVariable int id, @RequestBody NovaViagem dados) {
        return service.editar(id, dados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Viagem> buscar(@PathVariable int id) {
        Viagem viagem = service.buscar(id);
        return viagem == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(viagem);
    }
}
