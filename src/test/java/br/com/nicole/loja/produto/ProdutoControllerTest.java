package br.com.nicole.loja.produto;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** Testa a API de ponta a ponta, com o banco H2 e os produtos do data.sql. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProdutoControllerTest {

    private static final String PRODUTO_VALIDO = """
            {"nome": "Boné", "descricao": "Boné ajustável", "preco": 39.90, "estoque": 10}
            """;

    @Autowired
    private MockMvc mvc;

    @Test
    void listaOsProdutosEmOrdemAlfabeticaComPaginacao() throws Exception {
        mvc.perform(get("/api/produtos").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].nome").value("Calça jeans"))
                .andExpect(jsonPath("$.page.totalElements").value(5))
                .andExpect(jsonPath("$.page.totalPages").value(3));
    }

    @Test
    void filtraPorNomeSemDiferenciarMaiusculas() throws Exception {
        mvc.perform(get("/api/produtos").param("nome", "TÊNIS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome").value("Tênis de corrida"));
    }

    @Test
    void buscaUmProdutoEIndicaSeEstaDisponivel() throws Exception {
        mvc.perform(get("/api/produtos/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Mochila escolar"))
                .andExpect(jsonPath("$.estoque").value(0))
                .andExpect(jsonPath("$.disponivel").value(false));
    }

    @Test
    void devolve404QuandoOProdutoNaoExiste() throws Exception {
        mvc.perform(get("/api/produtos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.detail").value("Produto 999 não encontrado"));
    }

    @Test
    void criaUmProdutoEDevolveALocalizacao() throws Exception {
        mvc.perform(post("/api/produtos").contentType(MediaType.APPLICATION_JSON).content(PRODUTO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/produtos/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Boné"))
                .andExpect(jsonPath("$.disponivel").value(true));
    }

    @Test
    void recusaProdutoComDadosInvalidosEExplicaCadaCampo() throws Exception {
        String invalido = """
                {"nome": "", "preco": -5, "estoque": -1}
                """;
        mvc.perform(post("/api/produtos").contentType(MediaType.APPLICATION_JSON).content(invalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Dados inválidos"))
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.preco").value("deve ser maior que zero"))
                .andExpect(jsonPath("$.campos.estoque").value("não pode ser negativo"));
    }

    @Test
    void atualizaUmProduto() throws Exception {
        String atualizacao = """
                {"nome": "Camiseta básica", "preco": 59.90, "estoque": 80}
                """;
        mvc.perform(put("/api/produtos/1").contentType(MediaType.APPLICATION_JSON).content(atualizacao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preco").value(59.90))
                .andExpect(jsonPath("$.estoque").value(80));
    }

    @Test
    void removeUmProduto() throws Exception {
        mvc.perform(delete("/api/produtos/2")).andExpect(status().isNoContent());
        mvc.perform(get("/api/produtos/2")).andExpect(status().isNotFound());
    }

    @Test
    void statusDaApi() throws Exception {
        mvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }
}
