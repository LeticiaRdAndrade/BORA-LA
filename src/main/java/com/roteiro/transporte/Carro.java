package com.roteiro.transporte;

// HERANÇA: Carro também é filho de MeioTransporte
public class Carro extends MeioTransporte {

    // SOBRESCRITA: cada filho escreve do seu jeito os métodos abstratos do pai
    @Override
    public String getNome() {
        return "Carro";
    }

    @Override
    public String getPerfil() {
        return "driving-car";
    }
}
