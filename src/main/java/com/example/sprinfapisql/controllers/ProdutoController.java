package com.example.sprinfapisql.controllers;

import com.example.sprinfapisql.models.Produto;
import com.example.sprinfapisql.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.*;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController{

    private final ProdutoService produtoService;
    private final Validator validador;

    public ProdutoController(ProdutoService produtoService, Validator validator) {
        this.produtoService = produtoService;
        this.validador = validator;
    }


    @GetMapping("/selecionar")
    @Operation(summary = "Lista todos os produtos",
               description = "Retorna uma lista de todos os produtos disponíveis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
            content = @Content())
    })
    public List<Produto> listarProdutos() {
        return produtoService.buscarTodosProdutos();
    }

    @PostMapping("/inserir") // método post, o parâmetro vai estar no corpo da mensagem
    @Operation(summary = "Insere um produto",
            description = "Retorna o produto inserido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto inserido com sucesso!",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content())
    })
    public ResponseEntity<?> inserirProduto( @Valid @RequestBody Produto produto, BindingResult result) {
        // @RequestBody - está falando que como parâmetro vamos passar um JSON, mas, ele que transforma
        // o objeto Produto em JSON, depois ele coloca no body da API um produto ao acionarmos a API.

        // método save que insere, faz a mesma coisa que o persist do JPA
        // além de inserir e4le já salva também

//        try {
//            produtoService.salvarProduto(produto);
//            return ResponseEntity.ok("Produto inserido com sucesso");
//        } catch (DataIntegrityViolationException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ocorreu um erro ao inserir");
//        }


        if (result.hasErrors()){
            Map<String, String> erros = new HashMap<>();
            for (FieldError erro : result.getFieldErrors()) {
                // Coloque o nome do campo e a mensagem de erro no mapa
                erros.put(erro.getField(), erro.getDefaultMessage());
            }

            return new ResponseEntity<>(erros, HttpStatus.BAD_REQUEST);
        } else {
            produtoService.salvarProduto(produto);
            return new ResponseEntity<>("Produto inserido com sucesso", HttpStatus.OK);
        }

    }
    // está retornando uma mensagem de ok, código 200

    @DeleteMapping("/excluir/{id}")
    @Transactional
    @Operation(summary = "Deleta um produto pelo id",
            description = "Retorna o produto deletado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto deletado com sucesso!",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content())
    })
    public ResponseEntity<String> excluirProduto(@Parameter(name = "id", description = "Recebe o id do produto que será deletado", required = true) @PathVariable Long id) {
        // a única coisa que muda é que um parâmetro será extraído da url, no caso, o ID
        // para chamar isso no request http: DELETE http://localhost:8080/api/produtos/excluir/<id>
        // exemplo: DELETE http://localhost:8080/api/produtos/excluir/11

        // @PathVariable pega o parâmetro

//        Optional<Produto> produtoExistente = produtoRepository.findById(id);
//
//        if (produtoExistente.isPresent()) {
//            produtoRepository.delete(produtoExistente.get());
//            return ResponseEntity.status(HttpStatus.OK).body("Produto excluído com sucesso");
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
//        }

//        Long produtoDeletado = produtoRepository.deleteProdutoById(id);
//        if (produtoDeletado > 0) {
//            return ResponseEntity.status(HttpStatus.OK).body("Produto excluído com sucesso");
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
//        }


        try {
            produtoService.deletarProdutoPorId(id);
            return ResponseEntity.status(HttpStatus.OK).body("Produto excluído com sucesso");
        } catch (RuntimeException r){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(r.getMessage());
        }

    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<?> atualizarProduto(@PathVariable Long id, @Valid @RequestBody Produto produtoAtualizado, BindingResult result) {

        // produtoAtualizado é o que você tá passando na requisição no body, como que a gente quer que o produto fique
        // Optional<Produto> produtoExistente = produtoRepository.findById(id);
        // o Optional recebe ou um objeto Produto ou vazio
        // precisamos do Optional para verificar se o produto existe
        // save = se o id não existir, insere, se não, atualiza
//        try {
//            Produto produto = produtoService.buscarProdutoPorId(id);
////          Produto produto = produtoExistente.get();
//
//            produto.setNome(produtoAtualizado.getNome());
//            produto.setDescricao(produtoAtualizado.getDescricao());
//            produto.setPreco(produtoAtualizado.getPreco());
//            produto.setquantidadeEstoque(produtoAtualizado.getquantidadeEstoque());
//            // produtoRepository.save(produto);
//            produtoService.salvarProduto(produto);
//            return ResponseEntity.ok("Produto atualizado com sucesso");
//        } catch (RuntimeException r) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
//        }

        if (result.hasErrors()){
            Map<String, String> erros = new HashMap<>();
            for (FieldError erro : result.getFieldErrors()) {
                // Coloque o nome do campo e a mensagem de erro no mapa
                erros.put(erro.getField(), erro.getDefaultMessage());
            }

            return new ResponseEntity<>(erros, HttpStatus.BAD_REQUEST);
        } else {
            Produto produto = produtoService.buscarProdutoPorId(id);

            produto.setNome(produtoAtualizado.getNome());
            produto.setDescricao(produtoAtualizado.getDescricao());
            produto.setPreco(produtoAtualizado.getPreco());
            produto.setquantidadeEstoque(produtoAtualizado.getquantidadeEstoque());
            // produtoRepository.save(produto);
            produtoService.salvarProduto(produto);
            return new ResponseEntity<>("Produto atualizado com sucesso", HttpStatus.OK);
        }


    }

    // Nome e quantidadeEstoque não podem ser nulos

    // alterar parcialmente
    // @PatchMapping -> verificar se no corpo da requisição o atributo está presente ou não
    // passo no body somente aquilo que quero alterar
    // pelo map conseguimos verificar se o atributo está presente ou não
    @PatchMapping("/atualizarParcial/{id}")
    public ResponseEntity<String> atualizarParcial(@PathVariable Long id, @RequestBody Map<String, Object> update) {
//        Optional<Produto> produtoExistente = produtoRepository.findById(id);

        StringBuilder erros = new StringBuilder();
        String erro;
        try {
            Produto produto = produtoService.buscarProdutoPorId(id);
//            Produto produto = produtoExistente.get();

            // atualiza apenas aqueles que estão presentes no corpo da requisição
            if (update.containsKey("nome")) {
                produto.setNome((String) update.get("nome"));
            }
            if (update.containsKey("preco")) {
                String precoInt = update.get("preco").toString();
                produto.setPreco(Double.parseDouble(precoInt));
            }
            if (update.containsKey("descricao")) {
                produto.setDescricao((String) update.get("descricao"));
            }
            if (update.containsKey("quantidadeEstoque")) {
                produto.setquantidadeEstoque((int) update.get("quantidadeEstoque"));
            }

//            DataBinder binder = new DataBinder(produto);
//            binder.setValidator(validator);
//            binder.validate();
//            BindingResult result = binder.getBindingResult();
//            Map<String, String> erros = getErros(result);

            // Set = interface no Java que representa uma coleção que não permite elementos duplicados
            // é um conjunto que contém violações de restrição (ConstraintViolation) associadas a um objeto Produto.
            // Ele é usado para coletar todas as violações de restrição encontradas durante a validação do objeto Produto

            // validador = aplicar essas regras de validação ao objeto Produto e retornar um conjunto de violações
            // de restrição (ConstraintViolation) para quaisquer campos que não estejam em conformidade com as regras.

            Set<ConstraintViolation<Produto>> restricoes = validador.validate(produto);
            if (restricoes.isEmpty()) {
                produtoService.salvarProduto(produto);
                return ResponseEntity.ok("Produto alterado com sucesso");
            } else {
                for (ConstraintViolation<Produto> violacao : restricoes) {
                    erros.append(" | ").append(violacao.getPropertyPath()).append(": ").append(violacao.getMessage());
                }
                erro = erros.substring(3);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
            }
        } catch (RuntimeException r){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(r.getMessage());
        }

    }

    // encontrar pelo nome
        @PostMapping("/buscarPorNome")
        public ResponseEntity<?> buscarProdutosPorNome(@RequestParam String nome) {
            List<Produto> recebido = produtoService.buscarProdutoPorNome(nome);
            if (!recebido.isEmpty()) {
                return new ResponseEntity<>(recebido, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum produto encontrado com esse nome!");
            }
        }

    @PostMapping("/contarPelaQuantidade")
    public ResponseEntity<?> contarPelaQuantidade(@RequestParam int quant) {
        int recebido = produtoService.deletarPorQuantidade(quant);
        if (recebido > 0) {
            return new ResponseEntity<>("Quantidade de produtos excluídos: " + recebido, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum produto encontrado com essa quantidade!");
        }
    }

    @GetMapping("/buscarPeloNomeQuantidade/{nome}/{quant}")
    public ResponseEntity<?> buscarPorNomeQuantidade(@PathVariable String nome, @PathVariable int quant) {
        List<Produto> produtos = produtoService.buscarProdutoPorNomeQuantidade(nome, quant);
        if (!produtos.isEmpty()) {
            return new ResponseEntity<>(produtos, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum produto encontrado");
        }
    }

    @PostMapping("/buscarPeloNomePreco")
    public ResponseEntity<?> buscarPorNomePreco(@RequestParam String nome, @RequestParam double preco) {
        List<Produto> produtos = produtoService.buscarProdutoPorNomePreco(nome, preco);
        if (!produtos.isEmpty()) {
            return new ResponseEntity<>(produtos, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum produto encontrado");
        }
    }

    @DeleteMapping("/excluirQuantidade/{quant}")
    @Transactional
    public ResponseEntity<String> excluirProdutoQuantidade(@PathVariable int quant) {

        int excluidos = produtoService.deletarPorQuantidade(quant);

        if (excluidos  > 0) {
            return ResponseEntity.status(HttpStatus.OK).body("Produto excluído com sucesso");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum produto encontrado com a quantidade menor ou igual!");
        }
    }

//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<String> handleRuntimeException(RuntimeException re) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(re.getMessage());
//    }

//    private static Map<String, String> getErros(BindingResult result) {
//        Map<String, String> erros = new HashMap<>();
//        for (FieldError erro : result.getFieldErrors()) {
//            erros.put(erro.getField(), erro.getDefaultMessage());
//        }
//        return erros;
//    }

}




