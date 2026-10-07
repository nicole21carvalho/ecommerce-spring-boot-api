package br.com.nicole.loja.comum;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.nicole.loja.produto.ProdutoNaoEncontradoException;

/**
 * Converte as exceções em respostas no formato padrão RFC 9457 (Problem Details).
 * Estende o ResponseEntityExceptionHandler para que os erros do próprio Spring MVC
 * (JSON malformado, método não permitido etc.) também sigam esse formato.
 */
@RestControllerAdvice
public class TratadorDeErros extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ProblemDetail naoEncontrado(ProdutoNaoEncontradoException erro) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, erro.getMessage());
        problema.setTitle("Recurso não encontrado");
        return problema;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException erro,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> campos = new TreeMap<>();
        erro.getBindingResult().getFieldErrors()
                .forEach(campo -> campos.putIfAbsent(campo.getField(), campo.getDefaultMessage()));

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Um ou mais campos estão inválidos");
        problema.setTitle("Dados inválidos");
        problema.setProperty("campos", campos);
        return ResponseEntity.badRequest().headers(headers).body(problema);
    }
}
