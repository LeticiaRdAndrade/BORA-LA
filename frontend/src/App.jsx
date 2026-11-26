import { useEffect, useState } from "react";
import { api } from "./api.js";
import Cabecalho from "./componentes/Cabecalho.jsx";
import Inicio from "./paginas/Inicio.jsx";
import Explorar from "./paginas/Explorar.jsx";
import Digitar from "./paginas/Digitar.jsx";
import Roteiro from "./paginas/Roteiro.jsx";

// O número da viagem fica no link (ex.: site.com/#7) e também no navegador,
// assim consigo voltar para a mesma viagem depois, sem precisar de login.
const idSalvo = () => window.location.hash.slice(1) || localStorage.getItem("viagemId");

export default function App() {
  const [viagem, setViagem] = useState(null);
  const [pontos, setPontos] = useState([]);
  const [aba, setAba] = useState("explorar");
  const [carregando, setCarregando] = useState(true);

  const carregarPontos = (id) => api.listarPontos(id).then(setPontos);

  // Guarda a viagem no link e no navegador, e carrega os pontos dela
  function entrar(v) {
    localStorage.setItem("viagemId", v.id);
    window.location.hash = v.id;
    setViagem(v);
    carregarPontos(v.id);
  }

  // Ao abrir o site: se já existe uma viagem salva, busco ela no back-end
  useEffect(() => {
    const id = idSalvo();
    if (!id) return setCarregando(false);
    api.buscarViagem(id).then(entrar).catch(() => {}).finally(() => setCarregando(false));
  }, []);

  if (carregando) return null;

  // Sem viagem ainda: mostro a tela inicial para criar uma
  if (!viagem) {
    return <Inicio aoCriar={(nome, cidade) => api.criarViagem(nome, cidade).then(entrar)} />;
  }

  const recarregar = () => carregarPontos(viagem.id);

  return (
    <>
      <Cabecalho viagem={viagem} aba={aba} aoTrocarAba={setAba} />
      <main>
        {aba === "explorar" && <Explorar viagem={viagem} pontos={pontos} recarregar={recarregar} />}
        {aba === "digitar" && <Digitar viagem={viagem} pontos={pontos} recarregar={recarregar} />}
        {aba === "roteiro" && <Roteiro viagem={viagem} pontos={pontos} recarregar={recarregar} />}
      </main>
    </>
  );
}
