package br.com.guilhermezanin.screenmatch;

import br.com.guilhermezanin.screenmatch.model.ConverterDados;
import br.com.guilhermezanin.screenmatch.model.DadosEpisodio;
import br.com.guilhermezanin.screenmatch.model.DadosSerie;
import br.com.guilhermezanin.screenmatch.service.ConsumoApi;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String endereco = "https://www.omdbapi.com/?apikey=a5a0bf5&t=Gilmore+girls&season=1&episode=2";

		var consumoApi = new ConsumoApi();
		var json = consumoApi.obterDados(endereco);

		ConverterDados converterDados = new ConverterDados();

		//DadosSerie dadosSerie = converterDados.obterDados(json, DadosSerie.class);
		DadosEpisodio dadosEpisodio = converterDados.obterDados(json, DadosEpisodio.class);
		System.out.println(dadosEpisodio);
	}
}
