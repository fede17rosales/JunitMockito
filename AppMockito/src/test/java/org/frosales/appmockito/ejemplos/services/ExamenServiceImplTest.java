package org.frosales.appmockito.ejemplos.services;

import org.frosales.appmockito.ejemplos.Datos;
import org.frosales.appmockito.ejemplos.models.Examen;
import org.frosales.appmockito.ejemplos.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {

    @Mock
    ExamenRepositoryImpl repository;
    @Mock
    PreguntaRepositoryImpl preguntaRepository;

    @InjectMocks
    ExamenServiceImpl service;

    @Captor
    ArgumentCaptor<Long> captor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // utilizamos injeccion de depencencia
    }

    public static class MiArgsMatchers implements ArgumentMatcher<Long>{
        private Long argument;

        @Override
        public boolean matches(Long argument) {
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return "es para un mensaje personalizado de error " +
                    "que imprime mockito en caso de que falle el test. "
                    + argument +" debe ser un entero positivo";
        }
    }

    @Test
    void findExamenPorNombre() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        Optional<Examen> examen = service.findExamenPorNombre("Matematicas");

        assertTrue(examen.isPresent());
        assertEquals(5, examen.orElseThrow().getId());
        assertEquals("Matematicas",examen.get().getNombre());


    }

    @Test
    void findExamenPorListaVacia() {
        List<Examen> datos = Collections.emptyList();

        // Given
        when(repository.findAll()).thenReturn(datos);
        // When
        Optional<Examen> examen = service.findExamenPorNombre("Matematicas");
        // Then
        assertFalse(examen.isPresent()); // falla porque no esta presente
    }

    @Test
    void testPreguntasExamen(){
        // given
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenid(5L)).thenReturn(Datos.PREGUNTAS);
        // when
        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        // then
        assertEquals(5,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("geometria"));

    }

    @Test
    void testPreguntasExamenVerify(){
        // Given
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenid(5L)).thenReturn(Datos.PREGUNTAS);
        // When
        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        // Then
        assertEquals(5,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("geometria"));

    }

    @Test
    void testNoExisteExamenVerify(){
        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(preguntaRepository.findPreguntasPorExamenid(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertNull(examen);
        // verify(repository).findAll(); // verificamos que el moc se llame a todos los metodos
       // verify(preguntaRepository).findPreguntasPorExamenid(anyLong()); // verificamos que el moc se llame a todos los metodos

    }

    @Test
    void testguardarExamen(){
        // Given: precondiciones
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        when(repository.guardar(any(Examen.class))).then(new Answer<Examen>() {
            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocation) throws Throwable {
                Examen examen = invocation.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        });

        //When : ejecutamos un metodo real
        Examen examen = service.guardar(newExamen);

        //Then : validamos
        assertNotNull(examen.getId());
        assertEquals(8L,examen.getId());
        assertEquals("Fisica",examen.getNombre());
        verify(repository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    @Test
    void testManejoException(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
        when(preguntaRepository.findPreguntasPorExamenid(isNull())).thenThrow(IllegalArgumentException.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamenPorNombreConPreguntas("Matematicas");
        });
        
        assertEquals(IllegalArgumentException.class, exception.getClass());

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenid(isNull());
    }

    @Test
    void testArgumentMatchers() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenid(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(repository).findAll();
        //verify(preguntaRepository).findPreguntasPorExamenid(argThat(arg -> arg != null && arg.equals(5L)));
        //verify(preguntaRepository).findPreguntasPorExamenid(eq(5L));
        verify(preguntaRepository).findPreguntasPorExamenid(argThat(arg -> arg != null && arg >= 5L));
    }

    @Test
    void testArgumentMatchers2() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenid(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenid(argThat(new MiArgsMatchers()));
    }

    @Test
    void testArgumentMatchers3() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenid(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenid(argThat( (argument) -> argument != null && argument > 0));
    }

    @Test
    void testArgumentCaptor(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenid(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

        // ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(preguntaRepository).findPreguntasPorExamenid(captor.capture());

        assertEquals(5L, captor.getValue());
    }

    @Test
    void testDoThrow(){ // se utiliza para los void
        Examen examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);
        doThrow(IllegalArgumentException.class).when(preguntaRepository).guardarVarias(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
            service.guardar(examen);
        });
    }

    @Test
    void testDoAnswer(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5L? Datos.PREGUNTAS: null;
        }).when(preguntaRepository).findPreguntasPorExamenid(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5L, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("geometria"));
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas",examen.getNombre());

        verify(preguntaRepository).findPreguntasPorExamenid(anyLong());
    }

    @Test
    void testDoAnswerExamen(){
        // Given: precondiciones
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        doAnswer(new Answer<Examen>() {
            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocation) throws Throwable {
                Examen examen = invocation.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        }).when(repository).guardar(any(Examen.class));

        //When : ejecutamos un metodo real
        Examen examen = service.guardar(newExamen);

        //Then : validamos
        assertNotNull(examen.getId());
        assertEquals(8L,examen.getId());
        assertEquals("Fisica",examen.getNombre());
        verify(repository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    @Test
    void testdoCallRealMethod(){ // invoca los metodos reales
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        //when(preguntaRepository.findPreguntasPorExamenid(anyLong())).thenReturn(Datos.PREGUNTAS);

        doCallRealMethod().when(preguntaRepository).findPreguntasPorExamenid(anyLong());
        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas",examen.getNombre());
    }

    @Test
    void testSpy(){
        ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
        PreguntaRepository preguntaRepository = spy(PreguntaRepositoryImpl.class);
        ExamenService examenService =new ExamenServiceImpl(examenRepository, preguntaRepository);

        List<String> preguntas = Arrays.asList("aritmetica");
        //when(preguntaRepository.findPreguntasPorExamenid(anyLong())).thenReturn(Datos.PREGUNTAS);
        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenid(anyLong());

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matematicas");
        assertEquals(5,examen.getId());
        assertEquals("Matematicas",examen.getNombre());
        assertEquals(1,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenid(anyLong());
    }

    @Test
    void testOrdenesDeInvocaciones() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matematicas");
        service.findExamenPorNombreConPreguntas("Lengua");

        InOrder inOrder = inOrder(preguntaRepository);
        inOrder.verify(preguntaRepository).findPreguntasPorExamenid(5L);
        inOrder.verify(preguntaRepository).findPreguntasPorExamenid(6L);
    }

    @Test
    void testOrdenesDeInvocaciones2() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matematicas");
        service.findExamenPorNombreConPreguntas("Lengua");

        InOrder inOrder = inOrder(repository,preguntaRepository);
        inOrder.verify(repository).findAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamenid(5L);

        inOrder.verify(repository).findAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamenid(6L);
    }

    @Test
    void testNumeroDeInvocaciones() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(preguntaRepository, times(1)).findPreguntasPorExamenid(5L);
        verify(preguntaRepository, atLeast(1)).findPreguntasPorExamenid(5L);
        verify(preguntaRepository, atLeastOnce()).findPreguntasPorExamenid(5L);
        verify(preguntaRepository, atMost(1)).findPreguntasPorExamenid(5L);
        verify(preguntaRepository, atLeastOnce()).findPreguntasPorExamenid(5L);


    }

    @Test
    void testNumeroDeInvocaciones2() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(preguntaRepository, never()).findPreguntasPorExamenid(5L);
        verifyNoMoreInteractions(preguntaRepository);

        verify(repository).findAll();
        verify(repository,times(1)).findAll();
        verify(repository,atLeast(1)).findAll();
        verify(repository,atMost(1)).findAll();
        verify(repository,atLeastOnce()).findAll();
        verify(repository,atMostOnce()).findAll();


    }


}