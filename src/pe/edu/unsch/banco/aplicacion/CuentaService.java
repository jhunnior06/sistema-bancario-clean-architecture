package pe.edu.unsch.banco.aplicacion;

import pe.edu.unsch.banco.dominio.CuentaBancaria;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Casos de uso: registrar, depositar, retirar y consultar.
 * Recibe el repositorio por constructor (inversion de dependencias):
 * no sabe si los movimientos van a un archivo, a H2 o a otro medio.
 */
public class CuentaService {

    private final MovimientoRepository movimientoRepository;
    private CuentaBancaria cuentaActiva;

    public CuentaService(MovimientoRepository movimientoRepository) {
        this.movimientoRepository = Objects.requireNonNull(movimientoRepository,
                "Se requiere un MovimientoRepository");
    }

    public CuentaBancaria registrar(String titular, BigDecimal saldoInicial) {
        if (cuentaActiva != null) {
            throw new IllegalStateException("Ya existe una cuenta activa a nombre de "
                    + cuentaActiva.getTitular() + ". No se puede reemplazar.");
        }
        CuentaBancaria nueva = new CuentaBancaria(titular, saldoInicial);
        cuentaActiva = nueva;
        movimientoRepository.guardar("Cuenta creada | Titular: " + nueva.getTitular()
                + " | Saldo inicial: S/ " + nueva.getSaldo());
        return nueva;
    }

    public BigDecimal depositar(BigDecimal monto) {
        CuentaBancaria cuenta = obtenerCuentaActiva();
        cuenta.depositar(monto);
        movimientoRepository.guardar("Titular: " + cuenta.getTitular()
                + " | Deposito: S/ " + monto.setScale(2)
                + " | Saldo actual: S/ " + cuenta.getSaldo());
        return cuenta.getSaldo();
    }

    public BigDecimal retirar(BigDecimal monto) {
        CuentaBancaria cuenta = obtenerCuentaActiva();
        cuenta.retirar(monto);
        movimientoRepository.guardar("Titular: " + cuenta.getTitular()
                + " | Retiro: S/ " + monto.setScale(2)
                + " | Saldo actual: S/ " + cuenta.getSaldo());
        return cuenta.getSaldo();
    }

    public boolean existeCuentaActiva() {
        return cuentaActiva != null;
    }

    public CuentaBancaria consultar() {
        return obtenerCuentaActiva();
    }

    private CuentaBancaria obtenerCuentaActiva() {
        if (cuentaActiva == null) {
            throw new IllegalStateException("No hay una cuenta registrada. Use la opcion 1 primero.");
        }
        return cuentaActiva;
    }
}