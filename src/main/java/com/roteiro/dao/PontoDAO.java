package com.roteiro.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.roteiro.model.EdicaoPonto;
import com.roteiro.model.Lugar;
import com.roteiro.model.Ponto;

import jakarta.annotation.PostConstruct;

// O DAO é a única classe que conversa com o banco da tabela "ponto"
@Repository
public class PontoDAO {

    @Autowired
    DataSource dataSource;

    JdbcTemplate jdbc;

    @PostConstruct
    private void initialize() {
        jdbc = new JdbcTemplate(dataSource);
    }

    // Transforma uma linha do banco em um objeto Ponto
    private static Ponto mapear(ResultSet rs, int linha) throws SQLException {
        return new Ponto(rs.getInt("id"), rs.getInt("viagem_id"), rs.getString("nome"),
                rs.getDouble("lat"), rs.getDouble("lng"), rs.getInt("ordem"), rs.getBoolean("visitado"), rs.getString("anotacao"), rs.getString("endereco"));
    }

    public List<Ponto> listar(int viagemId) {
        return jdbc.query("SELECT * FROM ponto WHERE viagem_id = ? ORDER BY ordem", PontoDAO::mapear, viagemId);
    }

    // O novo ponto entra no fim da lista: ordem = maior ordem atual + 1
    public void inserir(int viagemId, Lugar l) {
        String sql = "INSERT INTO ponto(viagem_id, nome, lat, lng, endereco, ordem) VALUES(?, ?, ?, ?, ?, "
                + "(SELECT COALESCE(MAX(ordem), 0) + 1 FROM ponto WHERE viagem_id = ?))";
        String endereco = l.endereco() == null ? "" : l.endereco(); // a coluna não aceita nulo
        jdbc.update(sql, viagemId, l.nome(), l.lat(), l.lng(), endereco, viagemId);
    }

    // UPDATE do formulário de edição: troca o nome e a anotação do ponto
    public void editar(int id, EdicaoPonto e) {
        String anotacao = e.anotacao() == null ? "" : e.anotacao(); // a coluna não aceita nulo
        jdbc.update("UPDATE ponto SET nome = ?, anotacao = ? WHERE id = ?", e.nome(), anotacao, id);
    }

    public void remover(int id) {
        jdbc.update("DELETE FROM ponto WHERE id = ?", id);
    }

    // NOT visitado inverte o valor: se era falso vira verdadeiro e vice-versa
    public void alternarVisitado(int id) {
        jdbc.update("UPDATE ponto SET visitado = NOT visitado WHERE id = ?", id);
    }

    // Recebe os ids na nova ordem e grava a posição de cada um (1, 2, 3...)
    public void salvarOrdem(List<Integer> ids) {
        for (int i = 0; i < ids.size(); i++) {
            jdbc.update("UPDATE ponto SET ordem = ? WHERE id = ?", i + 1, ids.get(i));
        }
    }
}
