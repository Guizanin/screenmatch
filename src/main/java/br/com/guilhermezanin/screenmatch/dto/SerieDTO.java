package br.com.guilhermezanin.screenmatch.dto;

import br.com.guilhermezanin.screenmatch.model.Categoria;

public record SerieDTO(
        Long id,
        String titulo,
        Integer totalTemporadas,
        Double avaliacao,
        Categoria genero,
        String atores,
        String poster,
        String sinopse) {
}
