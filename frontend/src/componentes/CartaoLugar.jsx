// Cartão de um lugar sugerido, usado nas telas Explorar e Digitar
export default function CartaoLugar({ lugar, jaAdicionado, aoAdicionar }) {
  return (
    <div className="cartao">
      <h3>{lugar.nome}</h3>
      <button className={jaAdicionado ? "botao-leve" : "botao"} disabled={jaAdicionado} onClick={aoAdicionar}>
        {jaAdicionado ? "No roteiro" : "Adicionar ao roteiro"}
      </button>
    </div>
  );
}
