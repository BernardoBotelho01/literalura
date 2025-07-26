package br.com.alura.literalura.principal;

import br.com.alura.literalura.model.Autor;
import br.com.alura.literalura.model.ListaInformacoesLivro;
import br.com.alura.literalura.model.Livro;
import br.com.alura.literalura.repository.AutorRepository;
import br.com.alura.literalura.repository.LivroRepository;
import br.com.alura.literalura.service.ConsomeApi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private Scanner scan = new Scanner(System.in);
    ObjectMapper mapper = new ObjectMapper();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private String menu = """
            1- Buscar livro pelo nome e salvar no banco
            2- Listar todos livros salvos
            3- Listar autores registrados
            4- Listar autores vivos em um determinado ano
            5- Listar livros em um determinado idioma
            0- sair
            """;

    private LivroRepository livroRepository;
    private AutorRepository autorRepository;



    int opcao;

    public Principal(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public void exibirMenu() {
        while (true) {
            System.out.println(menu);
            System.out.print("Digite sua opção: ");
            opcao = scan.nextInt();
            scan.nextLine();
            if (opcao == 0) {
                System.out.println("Programa encerrado...");
                break;
            }
            if (opcao == 1) {
                buscarLivro();
            } else if (opcao == 2) {
                listarLivros();
            } else if (opcao == 3) {
                listarAutores();
            } else if (opcao == 4) {
                listarAutoresVivosPorAno();
            } else if (opcao == 5) {
                buscarLivroPorIdioma();
            } else {
                System.out.println("Opção invalida...");
            }

        }
    }

    private void buscarLivro() {
        System.out.print("Digite o nome de um livro: ");
        String nomeLivro = scan.nextLine().toLowerCase();
        System.out.println(nomeLivro);
        String url = URL_BASE + nomeLivro.replace(" ", "+");

        String json = ConsomeApi.obterDados(url);

        ListaInformacoesLivro listaInformacoes;
        try {
            listaInformacoes = mapper.readValue(json, ListaInformacoesLivro.class);
        } catch (JsonProcessingException e) {
            System.out.println("\nErro ao processar JSON da API.\n");
            return;
        }

        Optional<Livro> livroOptional = listaInformacoes.results().stream()
                .findFirst()
                .map(Livro::new);

        if (livroOptional.isEmpty()) {
            System.out.println("\nNenhum livro encontrado com esse nome.\n");
            return;
        }

        Livro livro = livroOptional.get();

        if (livroRepository.existsByTituloIgnoreCase(livro.getTitulo())) {
            System.out.println("z\nEste livro já está salvo no banco de dados.\n");
            return;
        }


        Autor autorNovo = livro.getAutor();
        Optional<Autor> autorOptional = autorRepository.findByNomeIgnoreCase(autorNovo.getNome());

        if (autorOptional.isPresent()) {

            Autor autorExistente = autorOptional.get();
            autorExistente.adicionarLivro(livro);
            livroRepository.save(livro);
            System.out.println("Livro salvo com autor existente:\n" + livro);
        } else {

            autorNovo.adicionarLivro(livro);
            autorRepository.save(autorNovo);
            System.out.println("Livro e novo autor salvos com sucesso:\n" + livro);
        }
    }



    private void listarLivros() {
        List<Livro> livros = livroRepository.findAll();
        livros.forEach(System.out::println);

    }

    private void listarAutores() {

        List<Autor> autores = autorRepository.findAll();
        autores.forEach(System.out::println);

    }

    private void listarAutoresVivosPorAno() {
        System.out.print("Digite o ano: ");
        int ano = scan.nextInt();

        List<Autor> autores = autorRepository.findAll();

        List<Autor> autoresVivos = autores.stream()
                .filter(a -> ano >= a.getAnoNascimento() && ano <= a.getAnoMorte())
                .toList();

        if (autoresVivos.isEmpty()) {
            System.out.println("\nNenhum autor estava vivo nesse ano.\n");
        } else {
            autoresVivos.forEach(System.out::println);
        }
    }

    private void buscarLivroPorIdioma(){
        System.out.print("Escolhas o idioma que quer realizar a busca:");
        System.out.print("\nes- espanhol\n"+"en- inglês\n"+"fr- francês\n"+"pt- português\n");
        System.out.print("Digite o idioma: ");
        String idioma = scan.nextLine();

        List<Livro> lirvos = livroRepository.findAll();

        List<Livro> livroPoridioma= lirvos.stream()
                .filter(livros -> livros.getIdioma().equalsIgnoreCase(idioma))
                .toList();

        if (livroPoridioma.isEmpty()){
            System.out.println("\nNenhum livro nesse idioma\n");
        }else {
            livroPoridioma.forEach(System.out::println);
        }
    }


}
