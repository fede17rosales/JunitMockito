package com.frosales.test.springboot.app.services;

import com.frosales.test.springboot.app.models.Cuenta;

import java.util.*;
import java.math.BigDecimal;

public interface CuentaService {

    List<Cuenta> findAll();

    Cuenta findById(Long id);

    Cuenta save(Cuenta cuenta);

    void deleteById(Long id);

    int revisarTotalTransferencias(Long bancoId);

    BigDecimal revisarSaldo(Long cuentaId);

    void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto,Long bancoId);
}
