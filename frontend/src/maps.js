// Monta o link do Google Maps já com o destino preenchido (nome + endereço; sem endereço, uso as coordenadas)
export function linkMaps(lugar) {
  const destino = lugar.endereco ? `${lugar.nome}, ${lugar.endereco}` : `${lugar.lat},${lugar.lng}`;
  return "https://www.google.com/maps/dir/?api=1&destination=" + encodeURIComponent(destino);
}
