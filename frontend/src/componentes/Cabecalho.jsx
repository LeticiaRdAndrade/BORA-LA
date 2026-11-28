import { useState } from "react";
import FormViagem from "./FormViagem.jsx";
import Logo from "./Logo.jsx";

const abas = [
  ["explorar", "🧭 Explorar"],
  ["digitar", "🔎 Buscar pelo nome"],
  ["roteiro", "🗺️ Meu roteiro"],
];

// Topo do site: logo, nome e cidade da viagem (com botão para editar) e, logo abaixo,
// as abas, que ficam separadas do cabeçalho para poderem grudar no topo ao rolar a página
export default function Cabecalho({ viagem, totalPontos, aba, aoTrocarAba, aoEditar, aoNovaViagem }) {
  const [copiado, setCopiado] = useState(false);
  const [editando, setEditando] = useState(false);

  // Copia o link da viagem, para eu abrir de novo ou mandar para alguém
  function copiarLink() {
    navigator.clipboard.writeText(window.location.href);
    setCopiado(true);
    setTimeout(() => setCopiado(false), 2000);
  }

  return (
    <>
      <header className="cabecalho nao-imprimir">
        <div className="cabecalho-topo">
          <Logo claro />
          <div className="acoes">
            <button className="botao-leve" onClick={copiarLink}>{copiado ? "Link copiado ✓" : "Copiar link"}</button>
            <button className="botao-leve" onClick={aoNovaViagem}>Nova viagem</button>
          </div>
        </div>

        {/* Formulário de edição da viagem: troca o nome e a cidade e salva no banco */}
        {editando ? (
          <FormViagem viagem={viagem} textoBotao="Salvar" aoCancelar={() => setEditando(false)}
                      aoSalvar={(nome, cidade) => aoEditar(nome, cidade).then(() => setEditando(false))} />
        ) : (
          <>
            <p className="etiqueta">Sua viagem</p>
            <div className="viagem-titulo">
              <h1>{viagem.nome}</h1>
              <span className="cidade vidro">📍 {viagem.cidade}</span>
              <button className="lapis" onClick={() => setEditando(true)}>✎ Editar viagem</button>
            </div>
          </>
        )}
      </header>

      <nav className="abas nao-imprimir">
        {abas.map(([id, texto]) => (
          <button key={id} className={aba === id ? "aba ativa" : "aba"} onClick={() => aoTrocarAba(id)}>
            {texto}
            {id === "roteiro" && totalPontos > 0 && <span className="contador">{totalPontos}</span>}
          </button>
        ))}
      </nav>
    </>
  );
}
