package org.frosales.appmockito.ejemplos.repositories;

import org.frosales.appmockito.ejemplos.models.Examen;
import org.frosales.appmockito.ejemplos.services.ExamenService;

import java.util.List;

public interface ExamenRepository {
    Examen guardar(Examen examen);
    List<Examen> findAll();
}
