package br.com.guilhermezanin.screenmatch.principal;

import br.com.guilhermezanin.screenmatch.model.ConverterDados;
import br.com.guilhermezanin.screenmatch.model.DadosSerie;
import br.com.guilhermezanin.screenmatch.model.DadosTemporada;
import br.com.guilhermezanin.screenmatch.service.ConsumoApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverterDados converterDados = new ConverterDados();

    //   "https://www.omdbapi.com/?apikey=a5a0bf5&t=Gilmore+girls"
    private final String ENDERECO = "https://www.omdbapi.com/?";
    private final String API_KEY = "apikey=a5a0bf5";


    public void exibeMenu(){
        System.out.println("Digite sua série: ");
        var serie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO+API_KEY+"&" +
                "t="+serie.replace(" ", "+"));
        System.out.println("pedido: " + ENDERECO+API_KEY+"&" +
                "t="+serie.replace(" ", "+"));

        DadosSerie dadosSerie = converterDados.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);

        List<DadosTemporada> requestTemporada = new ArrayList<>();

        for(int i = 1; i <= dadosSerie.totalTemporadas(); i++){
            json = consumo.obterDados(ENDERECO+API_KEY+"&" +
                    "t="+serie.replace(" ", "+")+"&season=" + i);

            DadosTemporada dadosTemporada = converterDados.obterDados(json, DadosTemporada.class);

            requestTemporada.add(dadosTemporada);
        }

        requestTemporada.forEach(System.out::println);
    }
}
