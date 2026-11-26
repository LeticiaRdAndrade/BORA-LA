import { useEffect, useState } from "react";
import { api } from "../api.js";
import CartaoLugar from "../componentes/CartaoLugar.jsx";

// Mostra os pontos turísticos de uma cidade para eu escolher quais entram no roteiro
export default function Explorar({ viagem, pontos, recarregar }) {
  const [cidade, setCidade] = useState(viagem.cidade);
  const [lugares, setLugares] = useState([]);
  const [buscando, setBuscando] = useState(true);

  function buscar() {
    setBuscando(true);
    api.explorar(cidade).then(setLugares).finally(() => setBuscando(false));
  }

  // Ao abrir a tela já busco os pontos da cidade da viagem
  useEffect(buscar, []);

  const jaNoRoteiro = (lugar) => pontos.some((p) => p.nome === lugar.nome);

  return (
    <section>
      <h2>Pontos turísticos</h2>
      <form className="busca" onSubmit={(e) => { e.preventDefault(); buscar(); }}>
        <input value={cidade} onChange={(e) => setCidade(e.target.value)} aria-label="Cidade" />
        <button className="botao">Buscar</button>
      </form>

      {buscando && <p className="aviso">Procurando lugares…</p>}
      {!buscando && lugares.length === 0 && (
        <p className="aviso">Não encontrei pontos turísticos para essa cidade. Confira o nome ou use a aba “Digitar um lugar”.</p>
      )}

      <div className="grade">
        {lugares.map((lugar) => (
          <CartaoLugar
            key={lugar.nome + lugar.lat}
            lugar={lugar}
            jaAdicionado={jaNoRoteiro(lugar)}
            aoAdicionar={() => api.adicionarPonto(viagem.id, lugar).then(recarregar)}
          />
        ))}
      </div>
    </section>
  );
}
