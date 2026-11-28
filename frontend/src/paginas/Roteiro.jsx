import { useState } from "react";
import { api } from "../api.js";
import FormEdicao from "../componentes/FormEdicao.jsx";
import Carregando from "../componentes/Carregando.jsx";
import Vazio from "../componentes/Vazio.jsx";
import Logo from "../componentes/Logo.jsx";
import { linkMaps } from "../maps.js";

// A lista de pontos na ordem da viagem, com o tempo entre um e outro
export default function Roteiro({ viagem, pontos, recarregar, aoExplorar }) {
  const [trechos, setTrechos] = useState([]);
  const [editando, setEditando] = useState(null); // id do ponto que está no formulário de edição
  const [calculando, setCalculando] = useState(false);

  // Pede ao back-end o tempo entre os pontos, mostrando a animação enquanto espera
  function calcular() {
    setCalculando(true);
    return api.trechos(viagem.id).then(setTrechos).finally(() => setCalculando(false));
  }

  // Salvar em PDF: antes de abrir a impressão, calculo os tempos (se ainda não calculei),
  // assim o PDF já sai com o tempo a pé e de carro entre as paradas.
  // O setTimeout dá um instante para a tela mostrar os tempos antes de imprimir.
  function salvarPdf() {
    const pronto = trechos.length > 0 || pontos.length < 2 ? Promise.resolve() : calcular();
    pronto.finally(() => setTimeout(() => window.print(), 300));
  }

  // Depois de qualquer mudança, os tempos calculados ficam velhos: limpo e recarrego a lista
  const atualizar = () => { setTrechos([]); recarregar(); };

  // Tira o ponto da posição atual, coloca na nova e salva a ordem no banco
  function mover(indice, deslocamento) {
    const novaLista = [...pontos];
    const [ponto] = novaLista.splice(indice, 1);
    novaLista.splice(indice + deslocamento, 0, ponto);
    api.salvarOrdem(viagem.id, novaLista.map((p) => p.id)).then(atualizar);
  }

  // Manda o formulário de edição para o banco e fecha o formulário
  function salvarEdicao(dados) {
    api.editarPonto(editando, dados).then(() => { setEditando(null); atualizar(); });
  }

  if (pontos.length === 0) {
    return (
      <Vazio desenho="mapa" titulo="Sua próxima aventura começa aqui">
        Escolha os lugares que quer conhecer em {viagem.cidade} e o bora lá! monta o caminho para você,
        com o tempo a pé e de carro entre eles.
        <br />
        <button className="botao vazio-botao" onClick={aoExplorar}>Explorar lugares</button>
      </Vazio>
    );
  }

  return (
    <section>
      <div className="roteiro-topo nao-imprimir">
        <h2>Meu roteiro</h2>
        <div className="acoes">
          <button className="botao" disabled={pontos.length < 2} onClick={calcular}>
            Calcular tempos
          </button>
          {/* o primeiro ponto fica onde está e os outros são organizados pelo mais próximo */}
          <button className="botao-leve" disabled={pontos.length < 3} onClick={() => api.otimizar(viagem.id).then(atualizar)}>
            Otimizar rota
          </button>
          <button className="botao-leve" onClick={salvarPdf}>Salvar em PDF</button>
        </div>
      </div>

      {/* capa do PDF (só aparece na impressão): faixa roxa com o logo, o nome da viagem e um resumo */}
      <header className="so-impressao capa-pdf">
        <Logo claro />
        <h1>{viagem.nome}</h1>
        <p>📍 {viagem.cidade} · {pontos.length} paradas · gerado em {new Date().toLocaleDateString("pt-BR")}</p>
      </header>
      {calculando && <Carregando texto="Calculando o caminho" />}

      <ol className="trilha">
        {pontos.map((ponto, i) => (
          <li key={ponto.id}>
            <div className={ponto.visitado ? "parada visitada" : "parada"}>
              <span className="numero">{i + 1}</span>

              {editando === ponto.id ? (
                <FormEdicao ponto={ponto} aoSalvar={salvarEdicao} aoCancelar={() => setEditando(null)} />
              ) : (
                <>
                  <div className="parada-texto">
                    <label className="parada-nome">
                      <input type="checkbox" checked={ponto.visitado} onChange={() => api.alternarVisitado(ponto.id).then(recarregar)} />
                      {ponto.nome}
                    </label>
                    {ponto.anotacao && <p className="anotacao">{ponto.anotacao}</p>}
                    {ponto.endereco && <p className="endereco">{ponto.endereco}</p>}
                    <a className="link-mapa nao-imprimir" href={linkMaps(ponto)} target="_blank" rel="noreferrer">Ir pelo Google Maps</a>
                  </div>
                  <div className="controles nao-imprimir">
                    <button aria-label="Editar" onClick={() => setEditando(ponto.id)}>✎</button>
                    <button aria-label="Subir" disabled={i === 0} onClick={() => mover(i, -1)}>↑</button>
                    <button aria-label="Descer" disabled={i === pontos.length - 1} onClick={() => mover(i, 1)}>↓</button>
                    <button aria-label="Remover" onClick={() => api.remover(ponto.id).then(atualizar)}>✕</button>
                  </div>
                </>
              )}
            </div>

            {/* o tempo até o próximo ponto aparece entre as duas paradas */}
            {trechos[i] && (
              <div className="trecho">
                {trechos[i].opcoes.map((o) => (
                  <span key={o.meio}>
                    {o.meio}: {o.minutos >= 0 ? o.minutos + " min" : "sem dado"}
                  </span>
                ))}
              </div>
            )}
          </li>
        ))}
      </ol>
      {trechos.length > 0 && <p className="nota">Tempos estimados pelo OpenRouteService.</p>}

      {/* rodapé do PDF: de onde veio o roteiro e o link para abrir a viagem no site */}
      <p className="so-impressao rodape-pdf">Roteiro feito no bora lá! · {window.location.href}</p>
    </section>
  );
}
