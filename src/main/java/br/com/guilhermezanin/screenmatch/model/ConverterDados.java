package br.com.guilhermezanin.screenmatch.model;

import br.com.guilhermezanin.screenmatch.service.IConverteDados;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConverterDados implements IConverteDados {
    private ObjectMapper mapper = new ObjectMapper();


    @Override
    public <T> T obterDados(String json, Class<T> classe) {
        try {
            return mapper.readValue(json, classe);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
