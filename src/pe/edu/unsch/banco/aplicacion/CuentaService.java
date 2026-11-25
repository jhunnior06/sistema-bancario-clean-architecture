package pe.edu.unsch.banco.aplicacion;

import pe.edu.unsch.banco.aplicacion.puertos.CuentaUseCase;
import pe.edu.unsch.banco.aplicacion.puertos.MovimientoRepository;
import pe.edu.unsch.banco.dominio.CuentaBancaria;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Caso de uso del sistema bancario.
 * Implementa el puerto de entrada CuentaUseCase y, para guardar los movimientos,
 * solo conoce la interfaz MovimientoRepository, no sabe si van a un archivo o a memoria.
 */
public class CuentaService implements CuentaUseCase {

    private final MovimientoRepository movimientoRepository;
    private CuentaBancaria cuentaActiva;

    public CuentaService(MovimientoRepository movimientoRepository) {
        this.movimientoRepository = Objects.requireNonNull(movimientoRepository,
                "Se requiere un MovimientoRepository");
    }

    @Override
    public void registrarCuenta(String titular, BigDecimal saldoInicial) {
        if (cuentaActiva != null) {
            throw new IllegalStateException("Ya existe una cuenta activa a nombre de "
                    + cuentaActiva.getTitular() + ". No se puede reemplazar.");
        }
        CuentaBancaria nueva = new CuentaBancaria(titular, saldoInicial);
        cuentaActiva = nueva;
        movimientoRepository.guardar("Cuenta creada | Titular: " + nueva.getTitular()
                + " | Saldo inicial: S/ " + nueva.getSaldo());
    }

    @Override
    public void depositar(BigDecimal monto) {
        CuentaBancaria cuenta = obtenerCuentaActiva();
        cuenta.depositar(monto);
        movimientoRepository.guardar("Titular: " + cuenta.getTitular()
                + " | Deposito: S/ " + monto.setScale(2)
                + " | Saldo actual: S/ " + cuenta.getSaldo());
    }

    @Override
    public void retirar(BigDecimal monto) {
        CuentaBancaria cuenta = obtenerCuentaActiva();
        cuenta.retirar(monto);
        movimientoRepository.guardar("Titular: " + cuenta.getTitular()
                + " | Retiro: S/ " + monto.setScale(2)
                + " | Saldo actual: S/ " + cuenta.getSaldo());
    }

    @Override
    public BigDecimal consultarSaldo() {
        return obtenerCuentaActiva().getSaldo();
    }

    @Override
    public String consultarTitular() {
        return obtenerCuentaActiva().getTitular();
    }

    @Override
    public boolean existeCuenta() {
        return cuentaActiva != null;
    }

    private CuentaBancaria obtenerCuentaActiva() {
        if (cuentaActiva == null) {
            throw new IllegalStateException("No hay una cuenta registrada. Use la opcion 1 primero.");
        }
        return cuentaActiva;
    }
}