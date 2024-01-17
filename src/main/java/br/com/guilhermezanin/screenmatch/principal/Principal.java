package br.com.guilhermezanin.screenmatch.principal;


import br.com.guilhermezanin.screenmatch.model.DadosSerie;
import br.com.guilhermezanin.screenmatch.model.DadosTemporada;
import br.com.guilhermezanin.screenmatch.model.Episodio;
import br.com.guilhermezanin.screenmatch.model.Serie;
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

    public Principal(SerieRepository repositorio){

        this.repositorio = repositorio;

    }

    public void exibeMenu() {
        var opcao = -1;
        while(opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                                    
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
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
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

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
                .findFirst();

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