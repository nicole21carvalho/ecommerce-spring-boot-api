package br.com.nicole.loja.produto;

import java.math.BigDecimal;

/** Dados devolvidos pela API. Separado da entidade para não expor detalhes do JPA. */
public record ProdutoResponse(Long id, String nome, String descricao, BigDecimal preco, int estoque,
        boolean disponivel) {

    static ProdutoResponse de(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getEstoque(),
                produto.getEstoque() > 0);
    }
}
