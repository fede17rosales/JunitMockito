package com.frosales.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frosales.test.springboot.app.models.Cuenta;
import com.frosales.test.springboot.app.models.TransaccionDto;
import com.frosales.test.springboot.app.services.CuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher.*;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

//import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static com.frosales.test.springboot.app.Datos.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    private MockMvc mvc;



    @MockBean
    private CuentaService cuentaService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }
    @Test
    void detalle() throws Exception{
        // given
        when(cuentaService.findById(1L)).thenReturn(crearCuenta001().orElseThrow());

        // when
        mvc.perform(get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.persona").value("Andres"))
                        .andExpect(jsonPath("$.saldo").value("1000"));
            verify(cuentaService).findById(1L);
    }

    @Test
    void testTransferir() throws Exception, JsonProcessingException {
        // given
        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1L);

        System.out.println(objectMapper.writeValueAsString(dto));
        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status","ok");
        response.put("mensaje","Transferencia realizada con exito");
        response.put("transaction",dto);

        System.out.println(objectMapper.writeValueAsString(response));


        // when
        mvc.perform(post("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))

        // then
                . andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                    .andExpect(jsonPath("$.mensaje").value("Transferencia realizada con exito"))
                    .andExpect(jsonPath("$.transaction.cuentaOrigenId").value(dto.getCuentaOrigenId()))
                    .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testListar() throws Exception, JsonProcessingException {
        // Given
        List<Cuenta> cuentas = Arrays.asList(crearCuenta001().orElseThrow(),
                crearCuenta002().orElseThrow());
        when(cuentaService.findAll()).thenReturn(cuentas);

        // when
        mvc.perform(get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))
                //Then
        .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].persona").value("Andres"))
                .andExpect(jsonPath("$[1].persona").value("John"))
                .andExpect(jsonPath("$[0].saldo").value("1000"))
                .andExpect(jsonPath("$[1].saldo").value("2000"))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(cuentas)));

    }

    @Test
    void testGuardar() throws Exception,JsonProcessingException {
        // Given
        Cuenta cuenta = new Cuenta(null,"Pepe",new BigDecimal("3000"));
        when(cuentaService.save(any())).then(invocation ->{
            Cuenta c = invocation.getArgument(0);
            c.setId(3L);
            return c;
        });

        // when
        mvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(cuenta)))

                // Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.persona",is("Pepe")))
                .andExpect(jsonPath("$.saldo",is(3000)));
        verify(cuentaService).save(any());
    }


}