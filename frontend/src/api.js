// Todas as chamadas ao back-end ficam neste arquivo, assim as telas ficam limpas.

async function chamar(caminho, opcoes) {
  const resposta = await fetch("/api" + caminho, opcoes);
  if (!resposta.ok) throw new Error("Erro " + resposta.status);
  const texto = await resposta.text(); // algumas respostas vêm vazias
  return texto ? JSON.parse(texto) : null;
}

// Monta as opções de um pedido que envia dados em JSON
const enviar = (metodo, corpo) => ({
  method: metodo,
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify(corpo),
});

export const api = {
  criarViagem: (nome, cidade) => chamar("/viagens", enviar("POST", { nome, cidade })),
  buscarViagem: (id) => chamar(`/viagens/${id}`),
  listarPontos: (id) => chamar(`/viagens/${id}/pontos`),
  adicionarPonto: (id, lugar) => chamar(`/viagens/${id}/pontos`, enviar("POST", lugar)),
  salvarOrdem: (id, ids) => chamar(`/viagens/${id}/ordem`, enviar("PUT", ids)),
  remover: (id) => chamar(`/pontos/${id}`, { method: "DELETE" }),
  alternarVisitado: (id) => chamar(`/pontos/${id}/visitado`, { method: "PUT" }),
  explorar: (cidade) => chamar(`/lugares/explorar?cidade=${encodeURIComponent(cidade)}`),
  buscar: (texto) => chamar(`/lugares/buscar?texto=${encodeURIComponent(texto)}`),
  trechos: (id) => chamar(`/viagens/${id}/trechos`),
};
