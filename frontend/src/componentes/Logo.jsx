// O logo do site: "bora lá" escrito com a fonte Bricolage Grotesque.
// O aviãozinho de papel (com o rastro pontilhado) faz o acento do "á" e o alfinete de mapa faz o "!".
// "claro" é a versão para fundo escuro (letras creme e avião amarelo); as cores estão no styles.css.
export default function Logo({ claro = false }) {
  return (
    <p className={claro ? "logo claro" : "logo"} role="img" aria-label="Bora Lá!">
      bora l
      <span className="logo-a">
        a
        <svg viewBox="-9 0 33 20" aria-hidden="true">
          <circle cx="-6.5" cy="13" r="1.3" fill="currentColor" />
          <circle cx="-2.5" cy="11.5" r="1.3" fill="currentColor" />
          <path d="M1 8L23 1L13 19L10 11z" fill="var(--aviao)" />
          <path d="M10 11L23 1" stroke="var(--roxo)" strokeWidth="1.2" opacity="0.6" />
        </svg>
      </span>
      {/* o "!" é um alfinete de mapa (com um furo no meio) e uma bolinha embaixo */}
      <svg className="logo-pino" viewBox="0 0 20 50" aria-hidden="true">
        <path d="M10 0C4.5 0 1 4 1 9.5C1 16 7 22 10 34C13 22 19 16 19 9.5C19 4 15.5 0 10 0z" fill="var(--coral)" />
        <circle cx="10" cy="9.5" r="3.4" fill="var(--furo)" />
        <circle cx="10" cy="44.5" r="5" fill="var(--coral)" />
      </svg>
    </p>
  );
}
