package com.roteiro.transporte;

// HERANÇA: APe é filho de MeioTransporte (extends)
public class APe extends MeioTransporte {

    // SOBRESCRITA: aqui eu escrevo o que o método abstrato do pai deve fazer para "a pé"
    @Override
    public String getNome() {
        return "A pé";
    }

    @Override
    public String getPerfil() {
        return "foot-walking";
    }
}
