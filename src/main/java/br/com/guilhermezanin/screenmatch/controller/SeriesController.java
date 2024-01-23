package br.com.guilhermezanin.screenmatch.controller;

import br.com.guilhermezanin.screenmatch.dto.SerieDTO;
import br.com.guilhermezanin.screenmatch.model.Serie;
import br.com.guilhermezanin.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SeriesController {

    @Autowired
    private SerieRepository repositorio;


    @GetMapping("/series")
    public List<SerieDTO> buscaSeries(){
        return repositorio.findAll().stream()
                .map(s -> new SerieDTO(s.getId(),s.getTitulo(),s.getTotalTemporadas(),
                        s.getAvaliacao(),s.getGenero(),s.getAtores(),s.getPoster(),
                        s.getSinopse()))
                .collect(Collectors.toList());
    }
}
