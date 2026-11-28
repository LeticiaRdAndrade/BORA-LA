import { useEffect, useState } from "react";
import { api } from "./api.js";
import { NOME } from "./config.js";
import Cabecalho from "./componentes/Cabecalho.jsx";
import Carregando from "./componentes/Carregando.jsx";
import Inicio from "./paginas/Inicio.jsx";
import Explorar from "./paginas/Explorar.jsx";
import Digitar from "./paginas/Digitar.jsx";
import Roteiro from "./paginas/Roteiro.jsx";

// Não tem login: o número da viagem fica no link (ex.: site.com/#7). Quem tem o link abre a viagem;
// sem número no link, mostro a tela inicial para criar uma viagem nova
const idDoLink = () => window.location.hash.slice(1);

export default function App() {
  const [viagem, setViagem] = useState(null);
  const [pontos, setPontos] = useState([]);
  const [aba, setAba] = useState("explorar");
  const [carregando, setCarregando] = useState(true);

  const carregarPontos = (id) => api.listarPontos(id).then(setPontos);

  // Coloca o número da viagem no link e carrega os pontos dela
  function entrar(v) {
    window.location.hash = v.id;
    setViagem(v);
    carregarPontos(v.id);
  }

  // Ao abrir o site: se o link tem o número de uma viagem, busco ela no back-end
  useEffect(() => {
    const id = idDoLink();
    if (!id) return setCarregando(false);
    api.buscarViagem(id).then(entrar).catch(() => {}).finally(() => setCarregando(false));
  }, []);

  // O título da aba do navegador mostra o nome da viagem (ex.: "Férias · Bora Lá")
  useEffect(() => {
    document.title = viagem ? `${viagem.nome} · ${NOME}` : NOME;
  }, [viagem]);

  if (carregando) return <Carregando texto="Abrindo sua viagem" telaCheia />;

  // Sem viagem ainda: mostro a tela inicial para criar uma
  if (!viagem) {
    return <Inicio aoCriar={(nome, cidade) => api.criarViagem(nome, cidade).then(entrar)} />;
  }

  const recarregar = () => carregarPontos(viagem.id);

  // Formulário de edição do cabeçalho: salvo no banco e troco a viagem pela que o back-end devolveu
  const editarViagem = (nome, cidade) => api.editarViagem(viagem.id, nome, cidade).then(setViagem);

  // "Nova viagem": volto para a tela inicial (a viagem continua salva no banco e abre de novo pelo link)
  function novaViagem() {
    window.location.hash = "";
    setViagem(null);
    setPontos([]);
    setAba("explorar");
  }

  return (
    <>
      <Cabecalho viagem={viagem} totalPontos={pontos.length} aba={aba} aoTrocarAba={setAba}
                 aoEditar={editarViagem} aoNovaViagem={novaViagem} />
      <main>
        {aba === "explorar" && <Explorar viagem={viagem} pontos={pontos} recarregar={recarregar} />}
        {aba === "digitar" && <Digitar viagem={viagem} pontos={pontos} recarregar={recarregar} />}
        {aba === "roteiro" && <Roteiro viagem={viagem} pontos={pontos} recarregar={recarregar} aoExplorar={() => setAba("explorar")} />}
      </main>
    </>
  );
}
