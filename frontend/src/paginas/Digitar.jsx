import { useState } from "react";
import { api } from "../api.js";
import CartaoLugar from "../componentes/CartaoLugar.jsx";
import Carregando from "../componentes/Carregando.jsx";
import Vazio from "../componentes/Vazio.jsx";

// Para quando a pessoa já sabe o lugar que quer e ele não apareceu no Explorar:
// ela digita só o nome e o back-end procura perto da cidade da viagem, devolvendo algumas opções
export default function Digitar({ viagem, pontos, recarregar }) {
  const [texto, setTexto] = useState("");
  const [achados, setAchados] = useState(null); // null = ainda não buscou; [] = buscou e não achou
  const [buscando, setBuscando] = useState(false);

  function buscar(e) {
    e.preventDefault();
    setAchados(null);
    setBuscando(true);
    api.buscar(texto, viagem.cidade).then(setAchados).catch(() => setAchados([])).finally(() => setBuscando(false));
  }

  return (
    <section>
      <h2>Já sabe aonde quer ir?</h2>
      <p className="aviso">Se o lugar não apareceu no Explorar, digite o nome dele aqui. Eu procuro em {viagem.cidade}.</p>
      <form className="busca" onSubmit={buscar}>
        <input value={texto} onChange={(e) => setTexto(e.target.value)} placeholder="Nome do lugar (ex.: Museu do Café)" aria-label="Nome do lugar" required />
        <button className="botao">Procurar</button>
      </form>

      {buscando && <Carregando texto="Procurando o lugar" />}
      {achados?.length === 0 && (
        <Vazio titulo="Não achei esse lugar">
          Não encontrei nada com esse nome em {viagem.cidade}. Confira se está escrito certo e tente de novo.
        </Vazio>
      )}

      {achados?.length > 0 && (
        <div className="grade">
          {achados.map((lugar, i) => (
            <CartaoLugar
              key={lugar.nome + lugar.lat}
              lugar={lugar}
              indice={i}
              jaAdicionado={pontos.some((p) => p.nome === lugar.nome)}
              aoAdicionar={() => api.adicionarPonto(viagem.id, lugar).then(recarregar)}
            />
          ))}
        </div>
      )}
    </section>
  );
}
