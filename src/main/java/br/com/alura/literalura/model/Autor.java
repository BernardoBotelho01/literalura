package br.com.alura.literalura.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String nome;

    private int anoNascimento;
    private int anoMorte;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Livro> livros = new ArrayList<>();

    public Autor() {}

    public Autor(DadosAutor dados) {
        this.nome = dados.nome();
        this.anoNascimento = dados.anoNascimento();
        this.anoMorte = dados.anoMorte();
    }

    public void adicionarLivro(Livro livro) {
        livros.add(livro);
        livro.setAutor(this);
    }

    @Override
    public String toString() {
        return  "\n---------Autor---------\n"+
                "Nome: " + nome + "\n"+
                "Ano de nascimento: " + anoNascimento + "\n"+
                "Ano de falecimento: " + anoMorte + "\n"+
                "Livros: " + livros.stream().map(Livro::getTitulo)
                        .reduce("", (acc, titulo) -> acc + "\n- " + titulo)+"\n"+
                "-------------------------\n";

    }
}
