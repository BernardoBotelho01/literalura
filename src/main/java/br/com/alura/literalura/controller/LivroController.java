package br.com.alura.literalura.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LivroController {

    @GetMapping("/livro")
    public String home(){
        return "Ola moleque safado";
    }
}
