package com.frosales.test.springboot.app.repositories;

import com.frosales.test.springboot.app.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CuentaRepository  extends JpaRepository<Cuenta,Long> {
    @Query("SELECT c FROM Cuenta c WHERE c.persona=?1")
    Optional<Cuenta> findByPersona(String persona);

  //  List<Cuenta> findAll();

 //   Cuenta findById(Long id);

 //   void update(Cuenta cuenta);
}
