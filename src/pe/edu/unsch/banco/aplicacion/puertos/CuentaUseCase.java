package pe.edu.unsch.banco.aplicacion.puertos;

import java.math.BigDecimal;

/**
 * Puerto de entrada del sistema bancario.
 * Aquí se definen las operaciones que la consola puede pedirle al núcleo.
 * CuentaService las implementa y Main solo conoce esta interfaz.
 */
public interface CuentaUseCase {

    void registrarCuenta(String titular, BigDecimal saldoInicial);

    void depositar(BigDecimal monto);

    void retirar(BigDecimal monto);

    BigDecimal consultarSaldo();

    String consultarTitular();

    boolean existeCuenta();
}