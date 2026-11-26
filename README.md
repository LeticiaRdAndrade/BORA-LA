# Meu Roteiro

Site para montar o roteiro de uma viagem: explorar pontos turísticos de uma cidade (ou digitar um lugar), escolher a ordem, marcar o que já foi visitado, ver o tempo entre os pontos (a pé e de carro), chamar um Uber e salvar em PDF.

**Tecnologias:** Java 21, Maven, Spring Boot, PostgreSQL (Render) e React (Vite).

## Estrutura

```
src/main/java/com/roteiro/
  controller/   recebe os pedidos do site (rotas /api/...)
  service/      regras; chama o DAO e as APIs externas
  dao/          só SQL (JdbcTemplate)
  model/        classes que guardam dados (records)
  transporte/   MeioTransporte, APe e Carro (herança)
src/main/resources/
  application.yaml        configuração (lê tudo de variáveis de ambiente)
  schema-postgres.sql     cria as tabelas
frontend/src/
  paginas/      telas (Inicio, Explorar, Digitar, Roteiro)
  componentes/  pedaços reutilizáveis (Cabecalho, CartaoLugar)
  api.js        todas as chamadas ao back-end
  styles.css    todo o visual
Dockerfile      compila o React e o Java e roda o .jar
```

## Onde apliquei cada conceito

| Conceito | Onde |
|---|---|
| Abstração | `transporte/MeioTransporte.java` (classe e métodos abstratos) |
| Herança | `APe` e `Carro` usam `extends MeioTransporte` |
| Sobrescrita | `@Override` em `APe`, `Carro` e no `getLinkApp` do carro (Uber) |
| Polimorfismo | `RotaService`: a lista `List<MeioTransporte>` guarda `APe` e `Carro` e chama os mesmos métodos |
| Sobrecarga | `RotaService.trechos(pontos)` e `trechos(pontos, escolhidos)` |

## Variáveis de ambiente

| Nome | O que é |
|---|---|
| `DB_URL` | `jdbc:postgresql://HOST:5432/NOME_DO_BANCO` |
| `DB_USER` | usuário do banco |
| `DB_SENHA` | senha do banco |
| `ORS_CHAVE` | chave gratuita do OpenRouteService (openrouteservice.org) |
| `OPENTRIPMAP_CHAVE` | chave gratuita da OpenTripMap (dev.opentripmap.org/register) |

## Rodar na minha máquina

Precisa de JDK 21 e Node.js. Em dois terminais:

```powershell
# 1) back-end (PowerShell): defina as variáveis e rode
$env:DB_URL="jdbc:postgresql://HOST:5432/NOME"; $env:DB_USER="..."; $env:DB_SENHA="..."
$env:ORS_CHAVE="..."; $env:OPENTRIPMAP_CHAVE="..."
.\mvnw spring-boot:run

# 2) front-end
cd frontend
npm install
npm run dev
```

Abra http://localhost:5173. Na máquina, use o endereço **externo** do banco do Render.
