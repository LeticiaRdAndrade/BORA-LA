package com.roteiro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roteiro.dao.PontoDAO;
import com.roteiro.model.Lugar;
import com.roteiro.model.Ponto;

@Service
public class PontoService {

    @Autowired
    PontoDAO dao;

    public List<Ponto> listar(int viagemId) {
        return dao.listar(viagemId);
    }

    public void adicionar(int viagemId, Lugar lugar) {
        dao.inserir(viagemId, lugar);
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
}
