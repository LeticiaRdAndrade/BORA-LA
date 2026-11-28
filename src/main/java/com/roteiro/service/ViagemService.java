package com.roteiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roteiro.dao.ViagemDAO;
import com.roteiro.model.NovaViagem;
import com.roteiro.model.Viagem;

// O controller chama o service e o service chama o DAO
@Service
public class ViagemService {

    @Autowired
    ViagemDAO dao;

    // Salva a viagem e devolve ela já com o id novo
    public Viagem criar(NovaViagem v) {
        int id = dao.inserir(v);
        return new Viagem(id, v.nome(), v.cidade());
    }

    // Altera o nome e a cidade e devolve a viagem como ficou
    public Viagem editar(int id, NovaViagem v) {
        dao.editar(id, v);
        return new Viagem(id, v.nome(), v.cidade());
    }

    public Viagem buscar(int id) {
        return dao.buscar(id);
    }
}
