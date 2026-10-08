package br.com.nicole.loja.status;

import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint simples para conferir se a API está no ar. */
@RestController
@RequestMapping("/api/status")
public class StatusController {

    public record Status(String status, String mensagem, Instant horario) {
    }

    @GetMapping
    public Status status() {
        return new Status("ok", "Olá, mundo Spring!", Instant.now());
    }
}
