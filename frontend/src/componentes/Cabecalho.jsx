import { useState } from "react";
import { NOME } from "../config.js";

const abas = [
  ["explorar", "Explorar"],
  ["digitar", "Digitar um lugar"],
  ["roteiro", "Meu roteiro"],
];

export default function Cabecalho({ viagem, aba, aoTrocarAba }) {
  const [copiado, setCopiado] = useState(false);

  // Copia o link da viagem, para eu abrir de novo ou mandar para alguém
  function copiarLink() {
    navigator.clipboard.writeText(window.location.href);
    setCopiado(true);
    setTimeout(() => setCopiado(false), 2000);
  }

  return (
    <header className="cabecalho nao-imprimir">
      <div className="cabecalho-topo">
        <div>
          <p className="marca">{NOME}</p>
          <p className="viagem-info">
            <strong>{viagem.nome}</strong> em {viagem.cidade}
          </p>
        </div>
        <button className="botao-leve" onClick={copiarLink}>
          {copiado ? "Link copiado" : "Copiar link da viagem"}
        </button>
      </div>
      <nav className="abas">
        {abas.map(([id, texto]) => (
          <button key={id} className={aba === id ? "aba ativa" : "aba"} onClick={() => aoTrocarAba(id)}>
            {texto}
          </button>
        ))}
      </nav>
    </header>
  );
}
