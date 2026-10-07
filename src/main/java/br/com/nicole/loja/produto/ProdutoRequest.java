package br.com.nicole.loja.produto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Dados recebidos para criar ou atualizar um produto. */
public record ProdutoRequest(
        @NotBlank(message = "é obrigatório")
        @Size(min = 2, max = 120, message = "deve ter entre 2 e 120 caracteres")
        String nome,

        @Size(max = 500, message = "deve ter no máximo 500 caracteres")
        String descricao,

        @NotNull(message = "é obrigatório")
        @DecimalMin(value = "0.01", message = "deve ser maior que zero")
        @Digits(integer = 8, fraction = 2, message = "deve ter no máximo 2 casas decimais")
        BigDecimal preco,

        @NotNull(message = "é obrigatório")
        @PositiveOrZero(message = "não pode ser negativo")
        Integer estoque) {
}
