package com.roteiro.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roteiro.dao.PontoDAO;
import com.roteiro.model.EdicaoPonto;
import com.roteiro.model.Lugar;
import com.roteiro.model.Ponto;

@Service
public class PontoService {

    @Autowired
    PontoDAO dao;

    @Autowired
    GeoapifyService geoapify;

    public List<Ponto> listar(int viagemId) {
        return dao.listar(viagemId);
    }

    // Ao adicionar, troco o endereço pelo endereço completo (rua, número, bairro, CEP), porque algumas
    // APIs só mandam o nome da cidade, e o PDF precisa do endereço de verdade
    public void adicionar(int viagemId, Lugar l) {
        String completo = geoapify.endereco(l.lat(), l.lng());
        dao.inserir(viagemId, new Lugar(l.nome(), l.lat(), l.lng(), completo != null ? completo : l.endereco(), l.foto()));
    }

    public void editar(int id, EdicaoPonto edicao) {
        dao.editar(id, edicao);
    }

    public void remover(int id) {
        dao.remover(id);
    }

    public void alternarVisitado(int id) {
        dao.alternarVisitado(id);
    }

    public void reordenar(List<Integer> ids) {
        dao.salvarOrdem(ids);
    }

    // OTIMIZAR ROTA ("vizinho mais próximo"): o primeiro ponto continua sendo o primeiro.
    // A partir dele, escolho sempre o ponto mais perto do último que coloquei, até acabar a lista.
    // Depois gravo a nova ordem no banco
    public void otimizar(int viagemId) {
        List<Ponto> faltam = new ArrayList<>(dao.listar(viagemId));
        if (faltam.isEmpty()) return;
        List<Ponto> rota = new ArrayList<>();
        rota.add(faltam.remove(0));
        while (!faltam.isEmpty()) {
            Ponto atual = rota.get(rota.size() - 1);
            Ponto maisPerto = faltam.stream().min(Comparator.comparingDouble(atual::distanciaAte)).get();
            faltam.remove(maisPerto);
            rota.add(maisPerto);
        }
        dao.salvarOrdem(rota.stream().map(Ponto::id).toList());
    }
}
