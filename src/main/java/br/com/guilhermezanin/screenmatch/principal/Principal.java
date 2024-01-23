package br.com.guilhermezanin.screenmatch.principal;


import br.com.guilhermezanin.screenmatch.model.*;
import br.com.guilhermezanin.screenmatch.repository.SerieRepository;
import br.com.guilhermezanin.screenmatch.service.ConsumoApi;
import br.com.guilhermezanin.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=a5a0bf5";
    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository repositorio;
    private List<Serie> series = new ArrayList<>() ;

    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio){

        this.repositorio = repositorio;

    }

    public void exibeMenu() {
        var opcao = -1;
        while(opcao != 0) {
            var menu = """
                    \n
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por título
                    5 - Buscar por séries por ator               
                    6 - Buscar as TOP 5 séries
                    7 - Busque séries por categoria
                    8 - Busque série com valor máx de temporadas e avaliação mínima
                    9 - Busque série por trecho de episódio
                    10 - Top 5 episódios por série
                    11 - Buscar episódios a partir de uma data
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarPorCategoria();
                    break;
                case 8:
                    buscaMaxTempAvaliacaoMinima();
                    break;
                case 9:
                    buscaPorTrechoEpisodios();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosDepoisDeUmaData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarEpisodiosDepoisDeUmaData(){
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano limite de lançamento");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serie, anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }

    private void topEpisodiosPorSerie(){
        buscarSeriePorTitulo();

        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);

            topEpisodios.forEach(e -> {
                System.out.printf("Serie: %s Temporada %s - Episodio %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo());
            });

        }
    };


    private void buscaPorTrechoEpisodios() {
        System.out.println("Qual o nome do episódio para busca?");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(System.out::println);
    }

    private void buscaMaxTempAvaliacaoMinima() {
        System.out.println("Filtrar séries até quantas temporadas? ");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();

        System.out.println("Com avaliação a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();

        List<Serie> filtroSeries = repositorio.seriesPorTemporadasAvaliacao(totalTemporadas, avaliacao);

        System.out.println("*** Séries filtradas ***");

        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - avaliação: " + s.getAvaliacao())
        );
    }

    private void buscarPorCategoria() {
        System.out.println("Digite a categoria para busca:");
        var insertCat = leitura.nextLine();

        Categoria categoria = Categoria.fromPortugues(insertCat);

        List<Serie> serie = repositorio.findByGenero(categoria);

        serie.forEach(s -> {
            System.out.println("Genero: "+ s.getGenero() +", série: " + s.getTitulo() + " , avaliação: " + s.getAvaliacao());
        });

    }

    private void buscarTop5Series() {
        List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        seriesTop.forEach(s -> {
            System.out.println("Série: " + s.getTitulo() + " , avaliação: " + s.getAvaliacao());
        });
    }

    private void buscarSeriePorAtor() {
        System.out.println("\nDigite o ator que desejar buscar:");
        var nomeAtor = leitura.nextLine();

        System.out.println("\nDigite o valor da avalição:");
        var avaliacao = leitura.nextDouble();

        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);

        seriesEncontradas.forEach(s -> {
            System.out.println("Série: " + s.getTitulo() + " , avaliação: " + s.getAvaliacao());
        });
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha um série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if(serieBusca.isPresent()){
            System.out.println("Dados da série: " + serieBusca.get());
        }else{
            System.out.println("Série não encontrada");
        }
    }


    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);

        repositorio.save(serie);

        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();

        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);

        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        System.out.println("\n\nEscolha uma série pelo nome:");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if(serie.isPresent()){
            var serieEncontrada = serie.get();

            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }

            List<Episodio> episodios  = temporadas.stream()
                    .flatMap(t -> t.episodios().stream()
                        .map(e -> new Episodio(t.numero(), e)))
                        .collect(Collectors.toList());

            System.out.println("episodios" + episodios);
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);

        }else {
            System.out.println("Série não encontrada");
        }


    }

    private void listarSeriesBuscadas(){
        series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }
}