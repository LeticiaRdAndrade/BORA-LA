import { useEffect, useState } from "react";
import { api } from "../api.js";
import CartaoLugar from "../componentes/CartaoLugar.jsx";
import Carregando from "../componentes/Carregando.jsx";
import Vazio from "../componentes/Vazio.jsx";

// Os tipos de lugar que dá para filtrar: [id que vai para o back-end, texto do botão]
const tipos = [
  ["populares", "⭐ Mais visitados"], ["parques", "🌳 Parques"], ["museus", "🏛️ Museus"], ["cultura", "🎭 Cultura"],
  ["mirantes", "🌄 Mirantes"], ["praias", "🏖️ Praias"], ["catedrais", "⛪ Catedrais"],
  ["shoppings", "🛍️ Shoppings"], ["diversao", "🎡 Diversão"],
];

// Mostra os pontos turísticos da cidade da viagem para eu escolher quais entram no roteiro
export default function Explorar({ viagem, pontos, recarregar }) {
  const [tipo, setTipo] = useState("populares");
  const [lugares, setLugares] = useState([]);
  const [buscando, setBuscando] = useState(true);

  // Busco de novo quando troco o tipo ou quando a cidade da viagem é editada no cabeçalho
  // Se eu trocar de filtro antes da resposta chegar, a resposta velha é ignorada ("atual" vira false
  // na função de limpeza do useEffect), senão ela poderia chegar depois e apagar a nova
  useEffect(() => {
    let atual = true;
    setBuscando(true);
    api.explorar(viagem.cidade, tipo)
      .then((l) => atual && setLugares(l))
      .finally(() => atual && setBuscando(false));
    return () => { atual = false; };
  }, [tipo, viagem.cidade]);

  const jaNoRoteiro = (lugar) => pontos.some((p) => p.nome === lugar.nome);
  // O ícone dos cartões é o emoji do filtro escolhido (ex.: "🌳 Parques" → "🌳")
  const icone = tipos.find(([id]) => id === tipo)[1].split(" ")[0];

  return (
    <section>
      <h2>O que ver por lá</h2>
      <p className="aviso">Escolha um tipo de passeio e adicione ao roteiro os lugares que quiser conhecer.</p>

      <div className="filtros">
        {tipos.map(([id, texto]) => (
          <button key={id} className={tipo === id ? "filtro ativo" : "filtro"} onClick={() => setTipo(id)}>
            {texto}
          </button>
        ))}
      </div>

      {buscando && <Carregando texto="Procurando lugares" />}
      {!buscando && lugares.length === 0 && (
        <Vazio titulo="Nada por aqui…">
          Não encontrei lugares desse tipo em {viagem.cidade}. Tente outro filtro ou use a aba “Buscar pelo nome”.
        </Vazio>
      )}

      {!buscando && (
        <div className="grade">
          {lugares.map((lugar, i) => (
            <CartaoLugar
              key={lugar.nome + lugar.lat}
              lugar={lugar}
              indice={i}
              icone={icone}
              jaAdicionado={jaNoRoteiro(lugar)}
              aoAdicionar={() => api.adicionarPonto(viagem.id, lugar).then(recarregar)}
            />
          ))}
        </div>
      )}
      <p className="nota">Lugares: OpenTripMap, Geoapify e OpenStreetMap. Nomes e fotos: Wikidata e Wikimedia Commons.</p>
    </section>
  );
}
