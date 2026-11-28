import { linkMaps } from "../maps.js";

// Quatro cores para a capa dos cartões, assim a grade não fica toda igual
const cores = ["tom-ceu", "tom-coral", "tom-sol", "tom-roxo"];

// Cartão de um lugar sugerido, usado nas telas Explorar e Buscar pelo nome.
// A capa mostra a foto do lugar (vem do Wikidata). Se o lugar não tiver foto, mostra o ícone do tipo
// de lugar (sem ícone, uso o alfinete). loading="lazy": a foto só carrega quando aparece na tela.
// "indice" é a posição na lista: o CSS usa para os cartões aparecerem um depois do outro.
export default function CartaoLugar({ lugar, indice = 0, icone = "📍", jaAdicionado, aoAdicionar }) {
  return (
    <div className="cartao" style={{ "--i": indice }}>
      <div className={"capa " + cores[lugar.nome.length % cores.length]}>
        {lugar.foto ? <img src={lugar.foto} alt={lugar.nome} loading="lazy" /> : <span>{icone}</span>}
      </div>
      <div className="cartao-corpo">
        <div>
          <h3>{lugar.nome}</h3>
          {lugar.endereco && <p className="endereco">{lugar.endereco}</p>}
          <a className="link-mapa" href={linkMaps(lugar)} target="_blank" rel="noreferrer">Ver no Google Maps ↗</a>
        </div>
        <button className={jaAdicionado ? "botao-leve" : "botao"} disabled={jaAdicionado} onClick={aoAdicionar}>
          {jaAdicionado ? "✓ No roteiro" : "+ Adicionar ao roteiro"}
        </button>
      </div>
    </div>
  );
}
