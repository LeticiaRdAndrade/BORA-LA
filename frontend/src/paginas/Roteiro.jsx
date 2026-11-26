import { useState } from "react";
import { api } from "../api.js";

// A lista de pontos na ordem da viagem, com o tempo entre um e outro
export default function Roteiro({ viagem, pontos, recarregar }) {
  const [trechos, setTrechos] = useState([]);

  // Depois de qualquer mudança, os tempos calculados ficam velhos: limpo e recarrego a lista
  const atualizar = () => { setTrechos([]); recarregar(); };

  // Tira o ponto da posição atual, coloca na nova e salva a ordem no banco
  function mover(indice, deslocamento) {
    const novaLista = [...pontos];
    const [ponto] = novaLista.splice(indice, 1);
    novaLista.splice(indice + deslocamento, 0, ponto);
    api.salvarOrdem(viagem.id, novaLista.map((p) => p.id)).then(atualizar);
  }

  if (pontos.length === 0) {
    return <p className="aviso">Seu roteiro está vazio. Explore a cidade ou digite um lugar para começar.</p>;
  }

  return (
    <section>
      <div className="roteiro-topo nao-imprimir">
        <h2>Meu roteiro</h2>
        <div className="acoes">
          <button className="botao" disabled={pontos.length < 2} onClick={() => api.trechos(viagem.id).then(setTrechos)}>
            Calcular tempos
          </button>
          <button className="botao-leve" onClick={() => window.print()}>Salvar em PDF</button>
        </div>
      </div>
      <h2 className="so-impressao">{viagem.nome} em {viagem.cidade}</h2>

      <ol className="trilha">
        {pontos.map((ponto, i) => (
          <li key={ponto.id}>
            <div className={ponto.visitado ? "parada visitada" : "parada"}>
              <span className="numero">{i + 1}</span>
              <label className="parada-nome">
                <input type="checkbox" checked={ponto.visitado} onChange={() => api.alternarVisitado(ponto.id).then(recarregar)} />
                {ponto.nome}
              </label>
              <div className="controles nao-imprimir">
                <button aria-label="Subir" disabled={i === 0} onClick={() => mover(i, -1)}>↑</button>
                <button aria-label="Descer" disabled={i === pontos.length - 1} onClick={() => mover(i, 1)}>↓</button>
                <button aria-label="Remover" onClick={() => api.remover(ponto.id).then(atualizar)}>✕</button>
              </div>
            </div>

            {/* o tempo até o próximo ponto aparece entre as duas paradas */}
            {trechos[i] && (
              <div className="trecho">
                {trechos[i].opcoes.map((o) => (
                  <span key={o.meio}>
                    {o.meio}: {o.minutos >= 0 ? o.minutos + " min" : "sem dado"}
                    {o.link && <a className="nao-imprimir" href={o.link} target="_blank" rel="noreferrer">Chamar Uber</a>}
                  </span>
                ))}
              </div>
            )}
          </li>
        ))}
      </ol>
      {trechos.length > 0 && <p className="nota">Tempos estimados pelo OpenRouteService.</p>}
    </section>
  );
}
