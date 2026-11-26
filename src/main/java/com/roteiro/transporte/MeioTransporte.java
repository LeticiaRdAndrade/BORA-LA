package com.roteiro.transporte;

import com.roteiro.model.Ponto;

// ABSTRAÇÃO: esta classe é abstrata, então não dá para fazer "new MeioTransporte()".
// Ela só define o que todo meio de transporte precisa ter; cada filho preenche do seu jeito.
public abstract class MeioTransporte {

    // Nome que aparece na tela (cada filho é obrigado a implementar)
    public abstract String getNome();

    // Nome do perfil que o OpenRouteService entende (também obrigatório nos filhos)
    public abstract String getPerfil();

    // Por padrão o meio não tem app para chamar, então devolve null.
    // Quem tiver (o carro) sobrescreve este método.
    public String getLinkApp(Ponto origem, Ponto destino) {
        return null;
    }
}
