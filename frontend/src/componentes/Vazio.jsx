// Tela vazia com desenho, título e mensagem (o que vem entre <Vazio> e </Vazio> é o "children").
// desenho="lupa": uma busca não achou nada (lupa com "?" e o aviãozinho procurando em volta).
// desenho="mapa": o roteiro está vazio (mapa dobrado com o caminho até o alfinete, para dar vontade de começar).
export default function Vazio({ titulo, desenho = "lupa", children }) {
  return (
    <div className="vazio">
      <svg className="vazio-ilustra" width="170" viewBox="0 0 170 125" aria-hidden="true">
        {desenho === "lupa" ? (
          <>
            {/* o rastro do avião dando a volta na lupa, e a lupa: cabo laranja, vidro e o "?" */}
            <path className="bl-rastro" d="M18 100C10 60 40 20 80 14S150 30 150 60" />
            <line x1="113" y1="83" x2="138" y2="108" stroke="var(--coral)" strokeWidth="11" strokeLinecap="round" />
            <circle cx="92" cy="62" r="30" fill="var(--papel)" stroke="var(--roxo)" strokeWidth="8" />
            <text x="92" y="75" textAnchor="middle" fontSize="36" fontWeight="800" fill="var(--coral)"
                  fontFamily="Bricolage Grotesque, sans-serif">?</text>
          </>
        ) : (
          <>
            {/* o mapa dobrado em três partes, o caminho pontilhado e o alfinete do logo no destino */}
            <path d="M20 34L60 22L105 34L150 22V100L105 112L60 100L20 112Z" fill="var(--papel)"
                  stroke="var(--roxo)" strokeWidth="4" strokeLinejoin="round" />
            <path d="M60 22V100M105 34V112" stroke="var(--roxo)" strokeWidth="2" opacity="0.25" />
            <path className="bl-rastro" d="M36 94C55 72 70 88 82 66S115 58 127 46" />
            <circle cx="36" cy="94" r="5" fill="var(--ceu)" />
            <g transform="translate(118 12) scale(0.9)">
              <path d="M10 0C4.5 0 1 4 1 9.5C1 16 7 22 10 34C13 22 19 16 19 9.5C19 4 15.5 0 10 0z" fill="var(--coral)" />
              <circle cx="10" cy="9.5" r="3.4" fill="var(--papel)" />
            </g>
          </>
        )}
        {/* o aviãozinho do logo aparece nos dois desenhos */}
        <g transform={desenho === "lupa" ? "translate(6 70) scale(1.4)" : "translate(4 0) scale(1.4)"}>
          <path d="M1 8L23 1L13 19L10 11z" fill="var(--ceu)" />
          <path d="M10 11L23 1" stroke="var(--roxo)" strokeWidth="1.2" opacity="0.6" />
        </g>
      </svg>
      <h3>{titulo}</h3>
      <div className="aviso">{children}</div>
    </div>
  );
}
