package org.frosales.appmockito.ejemplos.repositories;

import org.frosales.appmockito.ejemplos.Datos;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PreguntaRepositoryImpl implements PreguntaRepository{

    @Override
    public List<String> findPreguntasPorExamenid(Long id) {
        System.out.println("PreguntaRepositoryImpl.findPreguntasPorExamenid");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Datos.PREGUNTAS;
    }

    @Override
    public void guardarVarias(List<String> preguntas) {
        System.out.println("PreguntaRepositoryImpl.guardarVarias");


    }
}
