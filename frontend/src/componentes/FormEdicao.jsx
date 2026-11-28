import { useState } from "react";

// Formulário para editar um ponto do roteiro: o nome e uma anotação (ex.: "chegar às 9h")
export default function FormEdicao({ ponto, aoSalvar, aoCancelar }) {
  const [nome, setNome] = useState(ponto.nome);
  const [anotacao, setAnotacao] = useState(ponto.anotacao || "");

  function enviar(e) {
    e.preventDefault();
    aoSalvar({ nome, anotacao });
  }

  return (
    <form className="form-edicao" onSubmit={enviar}>
      <input value={nome} onChange={(e) => setNome(e.target.value)} aria-label="Nome do ponto" required />
      <input value={anotacao} onChange={(e) => setAnotacao(e.target.value)} maxLength={300}
             placeholder="Anotação (ex.: chegar às 9h)" aria-label="Anotação" />
      <div className="acoes">
        <button className="botao">Salvar</button>
        <button type="button" className="botao-leve" onClick={aoCancelar}>Cancelar</button>
      </div>
    </form>
  );
}
