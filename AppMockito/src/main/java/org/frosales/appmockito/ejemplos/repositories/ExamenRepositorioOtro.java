package org.frosales.appmockito.ejemplos.repositories;

import org.frosales.appmockito.ejemplos.Datos;
import org.frosales.appmockito.ejemplos.models.Examen;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamenRepositorioOtro implements ExamenRepository{
    @Override
    public Examen guardar(Examen examen) {
        System.out.println("ExamenRepositorioOtro.guardar");
        return Datos.EXAMEN;
    }

    @Override
    public List<Examen> findAll() {
        System.out.println("ExamenRepositorioOtro.findAll");
        try {
            System.out.println("ExamenRepositorioOtro");
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Datos.EXAMENES;
    }
}
