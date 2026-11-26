import { useState } from "react";
import { api } from "../api.js";
import CartaoLugar from "../componentes/CartaoLugar.jsx";

// Para quando a pessoa já sabe o lugar que quer: digita o nome e eu acho a localização
export default function Digitar({ viagem, pontos, recarregar }) {
  const [texto, setTexto] = useState("");
  const [achado, setAchado] = useState(null);
  const [naoAchou, setNaoAchou] = useState(false);

  function buscar(e) {
    e.preventDefault();
    setAchado(null);
    setNaoAchou(false);
    api.buscar(texto).then((lugar) => (lugar ? setAchado(lugar) : setNaoAchou(true)));
  }

  return (
    <section>
      <h2>Digitar um lugar</h2>
      <form className="busca" onSubmit={buscar}>
        <input value={texto} onChange={(e) => setTexto(e.target.value)} placeholder="Museu do Café, Santos" aria-label="Lugar" required />
        <button className="botao">Procurar</button>
      </form>

      {naoAchou && <p className="aviso">Não encontrei esse lugar. Tente incluir a cidade, como em “Museu do Café, Santos”.</p>}

      {achado && (
        <div className="grade">
          <CartaoLugar
            lugar={achado}
            jaAdicionado={pontos.some((p) => p.nome === achado.nome)}
            aoAdicionar={() => api.adicionarPonto(viagem.id, achado).then(recarregar)}
          />
        </div>
      )}
    </section>
  );
}
