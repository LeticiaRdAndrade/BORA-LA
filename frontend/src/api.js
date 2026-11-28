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
  editarViagem: (id, nome, cidade) => chamar(`/viagens/${id}`, enviar("PUT", { nome, cidade })),
  buscarViagem: (id) => chamar(`/viagens/${id}`),
  listarPontos: (id) => chamar(`/viagens/${id}/pontos`),
  adicionarPonto: (id, lugar) => chamar(`/viagens/${id}/pontos`, enviar("POST", lugar)),
  salvarOrdem: (id, ids) => chamar(`/viagens/${id}/ordem`, enviar("PUT", ids)),
  otimizar: (id) => chamar(`/viagens/${id}/otimizar`, { method: "PUT" }),
  editarPonto: (id, dados) => chamar(`/pontos/${id}`, enviar("PUT", dados)),
  remover: (id) => chamar(`/pontos/${id}`, { method: "DELETE" }),
  alternarVisitado: (id) => chamar(`/pontos/${id}/visitado`, { method: "PUT" }),
  explorar: (cidade, tipo) => chamar(`/lugares/explorar?cidade=${encodeURIComponent(cidade)}&tipo=${tipo}`),
  cidades: (texto) => chamar(`/lugares/cidades?texto=${encodeURIComponent(texto)}`),
  buscar: (texto, cidade) => chamar(`/lugares/buscar?texto=${encodeURIComponent(texto)}&cidade=${encodeURIComponent(cidade)}`),
  trechos: (id) => chamar(`/viagens/${id}/trechos`),
};
