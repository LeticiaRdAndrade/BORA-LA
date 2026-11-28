# Bora lá!

# Link para acessar: https://bora-la-xrx4.onrender.com/

Site para montar o roteiro de uma viagem. A pessoa cria a viagem com um nome e a cidade e escolhe os pontos turísticos que quer conhecer. Depois organiza a ordem e vê quanto tempo leva de um lugar para o outro, a pé e de carro. No final, pode salvar o roteiro em PDF.

Trabalho da disciplina de Programação Orientada a Objetos (FATEC).

## O que dá para fazer

- **Explorar:** os lugares mais visitados da cidade (até 10), com foto e nome em português, e filtros por tipo: parques, museus, cultura, mirantes, praias, catedrais, shoppings e diversão.
- **Buscar pelo nome:** acha um lugar específico perto da cidade da viagem.
- **Meu roteiro:**
  - mudar a ordem (↑ ↓);
  - marcar como visitado;
  - editar o nome e uma anotação;
  - remover;
  - calcular o tempo entre os pontos;
  - **otimizar a rota**: a partir do primeiro ponto, vai sempre para o mais próximo;
  - salvar em PDF com o endereço de cada lugar.
- **Sem cadastro:** o número da viagem fica no link (ex.: `/#7`). Quem tem o link abre a viagem.

## Tecnologias

- **Back-end:** Java 21, Spring Boot e Maven.
- **Banco:** PostgreSQL, acessado com JDBC (`JdbcTemplate`), sem JPA.
- **Front-end:** React com Vite.
- **Publicação:** Dockerfile e Render (o site e o banco).

## Organização do código

```
src/main/java/com/roteiro/
  controller/   recebe os pedidos do site (/api/...)
  service/      regras do sistema; cada API externa tem o seu service
  dao/          SQL com JdbcTemplate
  model/        records com os dados (inclusive o formato das respostas das APIs)
  transporte/   MeioTransporte (abstrata), APe e Carro
frontend/src/
  paginas/      Inicio, Explorar, Digitar (buscar pelo nome), Roteiro
  componentes/  Cabecalho, CartaoLugar, FormViagem, FormEdicao, Logo, Carregando, Vazio
  api.js        todas as chamadas ao back-end
```

## CRUD

| Operação | Onde |
|---|---|
| Create | criar viagem; "Adicionar ao roteiro" |
| Read | abrir a viagem pelo link; lista de pontos em "Meu roteiro" |
| Update | editar viagem (nome e cidade); editar ponto (✎); visitado; ordem; otimizar rota |
| Delete | botão ✕ de cada ponto |

## Programação orientada a objetos

| Conceito | Onde |
|---|---|
| Abstração | `MeioTransporte` é uma classe abstrata, com os métodos abstratos `getNome()` e `getPerfil()` |
| Herança | `APe` e `Carro` fazem `extends MeioTransporte` |
| Sobrescrita | `APe` e `Carro` usam `@Override` para escrever `getNome()` e `getPerfil()` do seu jeito |
| Polimorfismo | no `RotaService`, a lista `List<MeioTransporte>` guarda um `APe` e um `Carro`, e o mesmo código calcula os dois |
| Sobrecarga | `RotaService.trechos(pontos)` e `trechos(pontos, escolhidos)` |

## APIs usadas

| API | Para quê |
|---|---|
| Geoapify | achar a cidade e o contorno dela, sugestões de cidade, lugares por tipo, busca e endereço completo |
| OpenTripMap | lugares mais visitados e busca pelo nome |
| Wikidata e Wikipédia | nome em português e foto dos lugares |
| OpenRouteService | tempo a pé e de carro entre os pontos |

As senhas e chaves não ficam no código. Elas vêm de variáveis de ambiente:
- banco: `DB_URL`, `DB_USER`, `DB_SENHA`;
- APIs: `ORS_CHAVE`, `OPENTRIPMAP_CHAVE`, `GEOAPIFY_CHAVE`.
