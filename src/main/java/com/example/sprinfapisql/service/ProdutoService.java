package com.example.sprinfapisql.service;

import com.example.sprinfapisql.models.Produto;
import com.example.sprinfapisql.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    // injeção de dependência, criando um bean
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> buscarTodosProdutos() {
        return produtoRepository.findAll();
    }

    @Transactional
    public Produto salvarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Produto buscarProdutoPorId(Long id) {
        return produtoRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Produto não encontrado!"));
    }


    @Transactional
    public Produto deletarProdutoPorId(Long id) {
        Produto produto = buscarProdutoPorId(id);
        produtoRepository.deleteById(id);
        return produto;
    }

    public List<Produto> buscarProdutoPorNome(String nome) {
        return produtoRepository.findByNomeLikeIgnoreCase(nome);
    }

    public List<Produto> buscarProdutoPorNomeQuantidade(String nome, int quant) {
        return produtoRepository.findByNomeLikeIgnoreCaseAndQuantidadeEstoqueLessThanEqual(nome, quant);
    }

    public List<Produto> buscarProdutoPorNomePreco(String nome, double preco) {
        return produtoRepository.findByNomeLikeIgnoreCaseAndPrecoGreaterThanEqual(nome, preco);
    }

    public int deletarPorQuantidade(int quant) {
        int excluidos = produtoRepository.countByQuantidadeEstoqueIsLessThanEqual(quant);
        if (excluidos > 0) {
            produtoRepository.deleteByQuantidadeEstoqueIsLessThanEqual(quant);
        }
        return excluidos;
    }
}
