import FormViagem from "../componentes/FormViagem.jsx";
import Logo from "../componentes/Logo.jsx";

// Fotos da colagem: cartões-postais de cidades brasileiras, com fotos do Wikimedia Commons
// ("?width=600" pede uma versão menor, que carrega mais rápido)
const commons = "https://commons.wikimedia.org/wiki/Special:FilePath/";
const destinos = [
  ["Santos", commons + "Jardins%20da%20Orla%20de%20Santos%20e%20sua%20ciclovia.jpg?width=600"],
  ["Gramado", commons + "Lago%20Negro12.jpg?width=600"],
  ["Rio de Janeiro", commons + "Aerial%20view%20of%20Copacabana%20beach.jpg?width=600"],
];

// Primeira tela: a pessoa diz o nome da viagem e a cidade.
// Em cima, a mesma faixa roxa com o logo que as outras telas têm, para o site todo combinar
export default function Inicio({ aoCriar }) {
  return (
    <>
    <header className="cabecalho cabecalho-inicio">
      <div className="cabecalho-topo">
        <Logo claro />
        <span className="etiqueta-logo">monte seu roteiro de viagem</span>
      </div>
    </header>
    <main className="inicio">
      <div>
        <h1>Para onde você <span className="destaque">vai</span>?</h1>
        <p className="sub">Escolha os lugares que quer conhecer, deixe na ordem que preferir e veja quanto tempo leva de um para o outro.</p>
        <FormViagem textoBotao="Criar viagem" aoSalvar={aoCriar} />
        <ul className="vantagens">
          <li>Sem cadastro</li>
          <li>Tempo a pé e de carro</li>
          <li>Roteiro em PDF</li>
        </ul>
      </div>
      {/* colagem de cartões-postais em leque (só para enfeitar): cada destino é uma foto com o nome embaixo */}
      <div className="colagem" aria-hidden="true">
        {destinos.map(([cidade, foto], i) => (
          <figure key={cidade} className={"postal postal-" + i}>
            <img src={foto} alt="" />
            <figcaption>📍 {cidade}</figcaption>
          </figure>
        ))}
      </div>
    </main>
    </>
  );
}
