package com.frosales.test.springboot.app;

import com.frosales.test.springboot.app.models.Cuenta;
import com.frosales.test.springboot.app.repositories.CuentaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;


import java.math.BigDecimal;
import java.util.*;

@DataJpaTest
public class IntegrationJpaTest {
    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void testFindById(){
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Andrés",cuenta.orElseThrow().getPersona());

    }

    @Test
    void testFindByPersona(){
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Andres");
        assertFalse(cuenta.isPresent());
        //assertEquals("Andrés",cuenta.orElseThrow().getPersona());
    }

    @Test
    void testFindByPersonaThrows(){
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Rod");
        assertThrows(NoSuchElementException.class,cuenta::orElseThrow);
        assertFalse(cuenta.isPresent());
    }

    @Test
    void testFindAll(){
        List<Cuenta> cuentas = cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2,cuentas.size());


    }

    @Test
    void testSave(){
        //Given
        Cuenta cuentaPepe = new Cuenta(null,"Pepe",new BigDecimal("3000"));

        //when
        Cuenta save = cuentaRepository.save(cuentaPepe);
        //Cuenta cuenta = cuentaRepository.findByPersona("Pepe").orElseThrow();
        Cuenta cuenta = cuentaRepository.findById(save.getId()).orElseThrow();
        // Then
        assertEquals("Pepe",cuenta.getPersona());
        assertEquals("3000",cuenta.getSaldo().toPlainString());
        // assertEquals(3,cuenta.getId());

    }

    @Test
    void testUpdate(){
        //Given
        Cuenta cuentaPepe = new Cuenta(null,"Pepe",new BigDecimal("3000"));

        //when
        Cuenta cuenta = cuentaRepository.save(cuentaPepe);
        //Cuenta cuenta = cuentaRepository.findByPersona("Pepe").orElseThrow();
        // Cuenta cuenta = cuentaRepository.findById(save.getId()).orElseThrow();

        // Then
        assertEquals("Pepe",cuenta.getPersona());
        assertEquals("3000",cuenta.getSaldo().toPlainString());
        // assertEquals(3,cuenta.getId());

        // When
        cuenta.setSaldo(new BigDecimal("3800"));
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);

        // Then
        assertEquals("Pepe", cuentaActualizada.getPersona());
        assertEquals("3800", cuentaActualizada.getSaldo().toPlainString());


    }

    @Test
    void testDelete() {

        Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
        assertEquals("John", cuenta.getPersona());

        cuentaRepository.delete(cuenta);

        assertThrows(NoSuchElementException.class, () -> {
            cuentaRepository.findByPersona("John").orElseThrow();
        });
        assertEquals(1,cuentaRepository.findAll().size());
    }

}
