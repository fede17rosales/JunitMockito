package org.frosales.appmockito.ejemplos.repositories;

import org.frosales.appmockito.ejemplos.models.Examen;

import java.util.List;

public interface PreguntaRepository {
    List<String> findPreguntasPorExamenid(Long id);

    void guardarVarias(List<String> preguntas);
}
