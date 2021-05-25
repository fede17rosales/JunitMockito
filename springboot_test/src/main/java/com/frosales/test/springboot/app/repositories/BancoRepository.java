package com.frosales.test.springboot.app.repositories;

import com.frosales.test.springboot.app.models.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface BancoRepository extends JpaRepository<Banco,Long> {
   // List<Banco> findAll();

  // Banco findById(Long id);

  //  void update(Banco banco);
}
