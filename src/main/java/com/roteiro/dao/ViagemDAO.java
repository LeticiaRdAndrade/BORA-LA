package com.roteiro.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.roteiro.model.NovaViagem;
import com.roteiro.model.Viagem;

import jakarta.annotation.PostConstruct;

// O DAO é a única classe que conversa com o banco da tabela "viagem"
@Repository
public class ViagemDAO {

    @Autowired
    DataSource dataSource;

    JdbcTemplate jdbc;

    @PostConstruct
    private void initialize() {
        jdbc = new JdbcTemplate(dataSource);
    }

    // Insere a viagem e já devolve o id que o banco gerou (RETURNING id)
    public int inserir(NovaViagem v) {
        String sql = "INSERT INTO viagem(nome, cidade) VALUES(?, ?) RETURNING id";
        return jdbc.queryForObject(sql, Integer.class, v.nome(), v.cidade());
    }

    public void editar(int id, NovaViagem v) {
        jdbc.update("UPDATE viagem SET nome = ?, cidade = ? WHERE id = ?", v.nome(), v.cidade(), id);
    }

    // Busca uma viagem pelo id; devolve null se ela não existir
    public Viagem buscar(int id) {
        List<Viagem> lista = jdbc.query("SELECT * FROM viagem WHERE id = ?",
                (rs, linha) -> new Viagem(rs.getInt("id"), rs.getString("nome"), rs.getString("cidade")), id);
        return lista.isEmpty() ? null : lista.get(0);
    }
}
