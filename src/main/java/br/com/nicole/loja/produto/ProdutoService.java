package br.com.nicole.loja.produto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponse> listar(String nome, Pageable pageable) {
        Page<Produto> produtos = (nome == null || nome.isBlank())
                ? repository.findAll(pageable)
                : repository.findByNomeContainingIgnoreCase(nome.strip(), pageable);
        return produtos.map(ProdutoResponse::de);
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscar(Long id) {
        return ProdutoResponse.de(encontrar(id));
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest dados) {
        Produto produto = new Produto(dados.nome().strip(), dados.descricao(), dados.preco(), dados.estoque());
        return ProdutoResponse.de(repository.save(produto));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest dados) {
        Produto produto = encontrar(id);
        produto.atualizar(dados.nome().strip(), dados.descricao(), dados.preco(), dados.estoque());
        return ProdutoResponse.de(produto);
    }

    @Transactional
    public void remover(Long id) {
        repository.delete(encontrar(id));
    }

    private Produto encontrar(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }
}
