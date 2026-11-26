import { useState } from "react";
import { NOME } from "../config.js";

// Primeira tela: a pessoa diz o nome da viagem e a cidade
export default function Inicio({ aoCriar }) {
  const [nome, setNome] = useState("");
  const [cidade, setCidade] = useState("");
  const [erro, setErro] = useState("");

  function enviar(e) {
    e.preventDefault();
    setErro("");
    aoCriar(nome, cidade).catch(() => setErro("Não consegui criar a viagem. Tente de novo."));
  }

  return (
    <main className="inicio">
      <p className="marca">{NOME}</p>
      <h1>Para onde você vai?</h1>
      <p className="sub">Escolha os lugares que quer conhecer, deixe na ordem que preferir e veja quanto tempo leva de um para o outro.</p>
      <form className="painel" onSubmit={enviar}>
        <label>
          Nome da viagem
          <input value={nome} onChange={(e) => setNome(e.target.value)} placeholder="Fim de semana na praia" required />
        </label>
        <label>
          Cidade
          <input value={cidade} onChange={(e) => setCidade(e.target.value)} placeholder="Santos" required />
        </label>
        <button className="botao">Criar viagem</button>
        {erro && <p className="erro">{erro}</p>}
      </form>
    </main>
  );
}
