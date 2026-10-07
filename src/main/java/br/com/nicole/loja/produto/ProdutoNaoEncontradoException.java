package br.com.nicole.loja.produto;

public class ProdutoNaoEncontradoException extends RuntimeException {

    public ProdutoNaoEncontradoException(Long id) {
        super("Produto " + id + " não encontrado");
    }
}
