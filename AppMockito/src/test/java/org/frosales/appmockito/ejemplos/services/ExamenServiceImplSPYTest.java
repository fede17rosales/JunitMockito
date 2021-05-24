package org.frosales.appmockito.ejemplos.services;

import org.frosales.appmockito.ejemplos.Datos;
import org.frosales.appmockito.ejemplos.models.Examen;
import org.frosales.appmockito.ejemplos.repositories.ExamenRepository;
import org.frosales.appmockito.ejemplos.repositories.ExamenRepositoryImpl;
import org.frosales.appmockito.ejemplos.repositories.PreguntaRepository;
import org.frosales.appmockito.ejemplos.repositories.PreguntaRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplSPYTest {

    @Spy
    ExamenRepositoryImpl repository;
    @Spy
    PreguntaRepositoryImpl preguntaRepository;

    @InjectMocks
    ExamenServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // utilizamos injeccion de depencencia
    }


    @Test
    void testSpy(){

        List<String> preguntas = Arrays.asList("aritmetica");
        //when(preguntaRepository.findPreguntasPorExamenid(anyLong())).thenReturn(Datos.PREGUNTAS);
        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenid(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5,examen.getId());
        assertEquals("Matematicas",examen.getNombre());
        assertEquals(1,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenid(anyLong());
    }

}