import { useState } from "react";
import { api } from "../api.js";

// Formulário da viagem (nome + cidade). Uso o mesmo na tela inicial (criar) e no cabeçalho (editar).
// Quando vem uma "viagem", os campos já começam preenchidos com os dados dela.
export default function FormViagem({ viagem, textoBotao, aoSalvar, aoCancelar }) {
  const [nome, setNome] = useState(viagem?.nome || "");
  const [cidade, setCidade] = useState(viagem?.cidade || "");
  const [sugestoes, setSugestoes] = useState([]);
  const [erro, setErro] = useState("");

  // A cada letra digitada (a partir de 3), peço ao back-end as cidades com esse nome.
  // Elas aparecem numa lista embaixo do campo (o <datalist>), com estado e país, e a pessoa escolhe a certa.
  function digitarCidade(texto) {
    setCidade(texto);
    if (texto.length >= 3) api.cidades(texto).then(setSugestoes).catch(() => {});
  }

  function enviar(e) {
    e.preventDefault();
    setErro("");
    aoSalvar(nome, cidade).catch(() => setErro("Não consegui salvar. Tente de novo."));
  }

  return (
    <form className="painel" onSubmit={enviar}>
      <label>
        Nome da viagem
        <input value={nome} onChange={(e) => setNome(e.target.value)} placeholder="Férias de verão" required />
      </label>
      <label>
        Cidade
        <input value={cidade} onChange={(e) => digitarCidade(e.target.value)} list="sugestoes-cidade"
               placeholder="Ex.: Bonito (escolha na lista)" autoComplete="off" required />
        <datalist id="sugestoes-cidade">
          {sugestoes.map((s) => <option key={s} value={s} />)}
        </datalist>
      </label>
      <div className="acoes">
        <button className="botao">{textoBotao}</button>
        {aoCancelar && <button type="button" className="botao-leve" onClick={aoCancelar}>Cancelar</button>}
      </div>
      {erro && <p className="erro">{erro}</p>}
    </form>
  );
}
