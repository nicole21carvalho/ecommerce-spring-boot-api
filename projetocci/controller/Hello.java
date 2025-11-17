package com.dev.projetocci.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Date;


@RestController
@RequestMapping("/api")
public class Hello {
    @GetMapping("/")
    
    
    public String hello(){
        return "Olá mundo Spring"+ new Date();
    }
    
}
