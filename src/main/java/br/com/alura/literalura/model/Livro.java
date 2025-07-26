package br.com.alura.literalura.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "livros")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String titulo;

    private String idioma;

    private int numeroDownload;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Autor autor;

    public Livro() {}

    public Livro(DadosLivro dadosLivro) {
        this.titulo = dadosLivro.title();
        this.idioma = dadosLivro.languages().isEmpty() ? "desconhecido" : dadosLivro.languages().get(0);
        this.numeroDownload = dadosLivro.download_count();
        if (!dadosLivro.authors().isEmpty()) {
            this.autor = new Autor(dadosLivro.authors().get(0)); // construtor com DadosAutor
        } else {
            this.autor = new Autor(); // vazio
        }
    }

    @Override
    public String toString() {
        return "\n--------Livro--------\n" +
                "Titulo: " + titulo + "\n" +
                "Autor: " + (autor != null ? autor.getNome() : "Desconhecido") + "\n" +
                "Idioma: " + idioma + "\n" +
                "Numero de download: " + numeroDownload + "\n" +
                "------------------------\n";
    }
}
