import Logo from "./Logo.jsx";

// Carregando: um círculo pontilhado girando com o aviãozinho do logo dando a volta nele,
// e embaixo a mensagem com três pontinhos. As animações estão no styles.css.
// Com "telaCheia" (só ao abrir o site) ele cobre a página toda e mostra o logo;
// sem ele, aparece só no lugar da lista que está sendo carregada.
export default function Carregando({ texto, telaCheia = false }) {
  return (
    <div className={telaCheia ? "bl-loading tela-cheia" : "bl-loading"} role="status">
      {/* o círculo inteiro gira; como o avião está preso no topo dele, gira junto, sempre de bico para a frente */}
      <div className="giro">
        <svg className="giro-aviao" viewBox="0 0 24 20" aria-hidden="true">
          <path d="M1 8L23 1L13 19L10 11z" fill="var(--ceu)" />
          <path d="M10 11L23 1" stroke="var(--roxo)" strokeWidth="1.2" opacity="0.6" />
        </svg>
      </div>
      {telaCheia && <Logo />}
      <p className="bl-mensagem">
        {texto}<span className="bl-pontos"><span>.</span><span>.</span><span>.</span></span>
      </p>
    </div>
  );
}
