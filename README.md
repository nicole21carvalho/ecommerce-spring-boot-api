# 🛍️ Loja CCI: API de catálogo de produtos

[![CI](https://github.com/nicole21carvalho/ecommerce-spring-boot-api/actions/workflows/ci.yml/badge.svg)](https://github.com/nicole21carvalho/ecommerce-spring-boot-api/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1-6DB33F?logo=springboot&logoColor=white)

API REST do catálogo de produtos de uma loja virtual, feita com **Java 21 e Spring Boot 4**. Tem cadastro completo de produtos (CRUD), busca por nome, paginação, validação dos dados e respostas de erro padronizadas. É o back-end de uma loja que vai ter front-end em React/Next.js.

## ✨ Funcionalidades

- 📦 **CRUD de produtos:** listar, buscar, criar, atualizar e remover
- 🔎 **Busca por nome** sem diferenciar maiúsculas (`?nome=tenis`)
- 📄 **Paginação e ordenação** (`?page=0&size=10&sort=preco,desc`)
- ✅ **Validação:** nome obrigatório, preço maior que zero com até 2 casas decimais, estoque não negativo
- ⚠️ **Erros no padrão [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457)** (Problem Details), com a mensagem de cada campo inválido
- 🟢 Campo `disponivel` calculado a partir do estoque
- 🗃️ Banco **H2 em memória** com 5 produtos de exemplo: roda sem instalar banco nenhum

## 🔌 Endpoints

| Método | Rota | Descrição | Resposta |
|---|---|---|---|
| `GET` | `/api/status` | Confere se a API está no ar | `200` |
| `GET` | `/api/produtos` | Lista os produtos, com filtro `nome` e paginação | `200` |
| `GET` | `/api/produtos/{id}` | Busca um produto | `200` ou `404` |
| `POST` | `/api/produtos` | Cria um produto | `201` com header `Location`, ou `400` |
| `PUT` | `/api/produtos/{id}` | Atualiza um produto | `200`, `400` ou `404` |
| `DELETE` | `/api/produtos/{id}` | Remove um produto | `204` ou `404` |

### Exemplos

Criar um produto:

```bash
curl -X POST http://localhost:8080/api/produtos \
  -H "Content-Type: application/json" \
  -d '{"nome": "Boné", "descricao": "Boné ajustável", "preco": 39.90, "estoque": 10}'
```

```json
{ "id": 6, "nome": "Boné", "descricao": "Boné ajustável", "preco": 39.90, "estoque": 10, "disponivel": true }
```

Dados inválidos devolvem `400` explicando cada campo:

```json
{
  "title": "Dados inválidos",
  "status": 400,
  "detail": "Um ou mais campos estão inválidos",
  "campos": {
    "estoque": "não pode ser negativo",
    "nome": "é obrigatório",
    "preco": "deve ser maior que zero"
  }
}
```

## 🏗️ Arquitetura

```
Controller  →  Service  →  Repository  →  H2
   (HTTP)     (regras e     (Spring Data
               transações)    JPA)
```

```
src/main/java/br/com/nicole/loja/
├── LojaApplication.java
├── produto/
│   ├── Produto.java                       → entidade JPA
│   ├── ProdutoRepository.java             → acesso ao banco (Spring Data)
│   ├── ProdutoService.java                → regras de negócio e transações
│   ├── ProdutoController.java             → endpoints REST
│   ├── ProdutoRequest.java                → dados de entrada, com validação
│   ├── ProdutoResponse.java               → dados de saída
│   └── ProdutoNaoEncontradoException.java
├── comum/TratadorDeErros.java             → respostas de erro (Problem Details)
└── status/StatusController.java
```

## 🧠 Decisões técnicas

- **DTOs com `record`:** a entidade JPA nunca sai da API. `ProdutoRequest` define o que pode ser enviado (e valida), `ProdutoResponse` define o que é devolvido. Assim o cliente não consegue, por exemplo, mandar o `id`.
- **`BigDecimal` para preço:** `double` tem erros de arredondamento (`0.1 + 0.2 ≠ 0.3`), o que não é aceitável para dinheiro.
- **Transações no service:** leituras com `readOnly = true`; a atualização não precisa chamar `save`, porque o JPA grava as mudanças da entidade no fim da transação.
- **`open-in-view: false`:** evita consultas ao banco escondidas durante a serialização do JSON.
- **Formato de página estável:** `serialization-mode: via-dto` devolve `{ content, page }` em vez da `PageImpl` crua do Spring Data, cujo JSON pode mudar entre versões.

## ✅ Testes

9 testes de integração em [`ProdutoControllerTest`](src/test/java/br/com/nicole/loja/produto/ProdutoControllerTest.java) sobem a aplicação com o banco H2 e testam cada endpoint pela camada HTTP, incluindo validação, `404` e paginação. Cada teste roda numa transação desfeita no final, então um não interfere no outro.

```bash
./mvnw verify
```

## 🚀 Como executar

Precisa só do **Java 21**. O Maven Wrapper baixa o Maven sozinho.

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`. Teste com `curl http://localhost:8080/api/produtos`.

No Windows, use `mvnw.cmd` no lugar de `./mvnw`.

## 🛠️ Tecnologias

Java 21 · Spring Boot 4 (Web MVC, Data JPA, Validation) · H2 · JUnit 5 · MockMvc · GitHub Actions
